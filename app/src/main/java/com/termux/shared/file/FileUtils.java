package com.termux.shared.file;

import android.os.Build;
import android.system.Os;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.io.RecursiveDeleteOption;
import com.termux.shared.data.DataUtils;
import com.termux.shared.errors.Error;
import com.termux.shared.errors.FunctionErrno;
import com.termux.shared.file.filesystem.FileType;
import com.termux.shared.file.filesystem.FileTypes;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class FileUtils {

    /** Required file permissions for the executable file for app usage. Executable file must have read and execute permissions */
    public static final String APP_EXECUTABLE_FILE_PERMISSIONS = "r-x"; // Default: "r-x"
    /** Required file permissions for the working directory for app usage. Working directory must have read and write permissions.
     * Execute permissions should be attempted to be set, but ignored if they are missing */
    public static final String APP_WORKING_DIRECTORY_PERMISSIONS = "rwx"; // Default: "rwx"

    /**
     * Get canonical path.
     * <p>
     * If path is already an absolute path, then it is used as is to get canonical path.
     * If path is not an absolute path and {code prefixForNonAbsolutePath} is not {@code null}, then
     * {code prefixForNonAbsolutePath} + "/" is prefixed before path before getting canonical path.
     * If path is not an absolute path and {code prefixForNonAbsolutePath} is {@code null}, then
     * "/" is prefixed before path before getting canonical path.
     * <p>
     * If an exception is raised to get the canonical path, then absolute path is returned.
     *
     * @param path The {@code path} to convert.
     * @param prefixForNonAbsolutePath Optional prefix path to prefix before non-absolute paths. This
     *                                 can be set to {@code null} if non-absolute paths should
     *                                 be prefixed with "/". The call to {@link File#getCanonicalPath()}
     *                                 will automatically do this anyways.
     * @return Returns the {@code canonical path}.
     */
    public static String getCanonicalPath(String path, final String prefixForNonAbsolutePath) {
        if (path == null) path = "";

        String absolutePath;

        // If path is already an absolute path
        if (path.startsWith("/")) {
            absolutePath = path;
        } else {
            if (prefixForNonAbsolutePath != null)
                absolutePath = prefixForNonAbsolutePath + "/" + path;
            else
                absolutePath = "/" + path;
        }

        try {
            return new File(absolutePath).getCanonicalPath();
        } catch(Exception e) {
        }

        return absolutePath;
    }

    /**
     * Removes one or more forward slashes "//" with single slash "/"
     * Removes "./"
     * Removes trailing forward slash "/"
     *
     * @param path The {@code path} to convert.
     * @return Returns the {@code normalized path}.
     */
    @Nullable
    public static String normalizePath(String path) {
        if (path == null) return null;

        path = path.replaceAll("/+", "/");
        path = path.replaceAll("\\./", "");

        if (path.endsWith("/")) {
            path = path.replaceAll("/+$", "");
        }

        return path;
    }

    /**
     * Determines whether path is in {@code dirPath}. The {@code dirPath} is not canonicalized and
     * only normalized.
     *
     * @param path The {@code path} to check.
     * @param dirPath The {@code directory path} to check in.
     * @param ensureUnder If set to {@code true}, then it will be ensured that {@code path} is
     *                    under the directory and does not equal it.
     * @return Returns {@code true} if path in {@code dirPath}, otherwise returns {@code false}.
     */
    public static boolean isPathInDirPath(String path, final String dirPath, final boolean ensureUnder) {
        return isPathInDirPaths(path, Collections.singletonList(dirPath), ensureUnder);
    }

    /**
     * Determines whether path is in one of the {@code dirPaths}. The {@code dirPaths} are not
     * canonicalized and only normalized.
     *
     * @param path The {@code path} to check.
     * @param dirPaths The {@code directory paths} to check in.
     * @param ensureUnder If set to {@code true}, then it will be ensured that {@code path} is
     *                    under the directories and does not equal it.
     * @return Returns {@code true} if path in {@code dirPaths}, otherwise returns {@code false}.
     */
    public static boolean isPathInDirPaths(String path, final List<String> dirPaths, final boolean ensureUnder) {
        if (path == null || path.isEmpty() || dirPaths == null || dirPaths.size() < 1) return false;

        try {
            path = new File(path).getCanonicalPath();
        } catch(Exception e) {
            return false;
        }

        boolean isPathInDirPaths;

        for (String dirPath : dirPaths) {
            String normalizedDirPath = normalizePath(dirPath);

            if (ensureUnder)
                isPathInDirPaths = !path.equals(normalizedDirPath) && path.startsWith(normalizedDirPath + "/");
            else
                isPathInDirPaths = path.startsWith(normalizedDirPath + "/");

            if (isPathInDirPaths) return true;
        }

        return false;
    }

    /**
     * Validate that directory is empty or contains only files in {@code ignoredSubFilePaths}.
     * <p>
     * If parent path of an ignored file exists, but ignored file itself does not exist, then directory
     * is not considered empty.
     *
     * @param label The optional label for directory to check. This can optionally be {@code null}.
     * @param filePath The {@code path} for directory to check.
     * @param ignoredSubFilePaths The list of absolute file paths under {@code filePath} dir.
     *                            Validation is done for the paths.
     * @param ignoreNonExistentFile The {@code boolean} that decides if it should be considered an
     *                              error if file to be checked doesn't exist.
     * @return Returns {@code null} if directory is empty or contains only files in {@code ignoredSubFilePaths}.
     * Returns {@code FileUtilsErrno#ERRNO_NON_EMPTY_DIRECTORY_FILE} if a file was found that did not
     * exist in the {@code ignoredSubFilePaths}, otherwise returns an appropriate {@code error} if
     * checking was not successful.
     */
    public static Error validateDirectoryFileEmptyOrOnlyContainsSpecificFiles(String label, String filePath,
                                                                              final List<String> ignoredSubFilePaths,
                                                                              final boolean ignoreNonExistentFile) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "file path", "isDirectoryFileEmptyOrOnlyContainsSpecificFiles");

        try {
            File file = new File(filePath);
            FileType fileType = getFileType(filePath, false);

            // If file exists but not a directory file
            if (fileType != FileType.NO_EXIST && fileType != FileType.DIRECTORY) {
                return FileUtilsErrno.ERRNO_NON_DIRECTORY_FILE_FOUND.getError(label + "directory", filePath).setLabel(label + "directory");
            }

            // If file does not exist
            if (fileType == FileType.NO_EXIST) {
                // If checking is to be ignored if file does not exist
                if (ignoreNonExistentFile)
                    return null;
                else {
                    label += "directory to check if is empty or only contains specific files";
                    return FileUtilsErrno.ERRNO_FILE_NOT_FOUND_AT_PATH.getError(label, filePath).setLabel(label);
                }
            }

            File[] subFiles = file.listFiles();
            if (subFiles == null || subFiles.length == 0)
                return null;

            // If sub files exists but no file should be ignored
            if (ignoredSubFilePaths == null || ignoredSubFilePaths.size() == 0)
                return FileUtilsErrno.ERRNO_NON_EMPTY_DIRECTORY_FILE.getError(label, filePath);

            // If a sub file does not exist in ignored file path
            if (nonIgnoredSubFileExists(subFiles, ignoredSubFilePaths)) {
                return FileUtilsErrno.ERRNO_NON_EMPTY_DIRECTORY_FILE.getError(label, filePath);
            }

        } catch (Exception e) {
            return FileUtilsErrno.ERRNO_VALIDATE_DIRECTORY_EMPTY_OR_ONLY_CONTAINS_SPECIFIC_FILES_FAILED_WITH_EXCEPTION.getError(e, label + "directory", filePath, e.getMessage());
        }

        return null;
    }

    /**
     * Check if {@code subFiles} contains contains a file not in {@code ignoredSubFilePaths}.
     * <p>
     * If parent path of an ignored file exists, but ignored file itself does not exist, then directory
     * is not considered empty.
     * <p>
     * This function should ideally not be called by itself but through
     * {@link #validateDirectoryFileEmptyOrOnlyContainsSpecificFiles(String, String, List, boolean)}.
     *
     * @param subFiles The list of files of a directory to check.
     * @param ignoredSubFilePaths The list of absolute file paths under {@code filePath} dir.
     *                            Validation is done for the paths.
     * @return Returns {@code true} if a file was found that did not exist in the {@code ignoredSubFilePaths},
     * otherwise  {@code false}.
     */
    public static boolean nonIgnoredSubFileExists(File[] subFiles, @NonNull List<String> ignoredSubFilePaths) {
        if (subFiles == null || subFiles.length == 0) return false;

        String subFilePath;
        for (File subFile : subFiles) {
            subFilePath = subFile.getAbsolutePath();
            // If sub file does not exist in ignored sub file paths
            if (!ignoredSubFilePaths.contains(subFilePath)) {
                boolean isParentPath = false;
                for (String ignoredSubFilePath : ignoredSubFilePaths) {
                    if (ignoredSubFilePath.startsWith(subFilePath + "/") && fileExists(ignoredSubFilePath, false)) {
                        isParentPath = true;
                        break;
                    }
                }
                // If sub file is not a parent of any existing ignored sub file paths
                if (!isParentPath) {
                    return true;
                }
            }
                
            if (getFileType(subFilePath, false) == FileType.DIRECTORY) {
                // If non ignored sub file found, then early exit, otherwise continue looking
                if (nonIgnoredSubFileExists(subFile.listFiles(), ignoredSubFilePaths))
                     return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a directory file exists at {@code filePath}.
     *
     * @param filePath The {@code path} for directory file to check.
     * @param followLinks The {@code boolean} that decides if symlinks will be followed while
     *                       finding if file exists. Check {@link #getFileType(String, boolean)}
     *                       for details.
     * @return Returns {@code true} if directory file exists, otherwise {@code false}.
     */
    public static boolean directoryFileExists(final String filePath, final boolean followLinks) {
        return getFileType(filePath, followLinks) == FileType.DIRECTORY;
    }

    /**
     * Checks whether any file exists at {@code filePath}.
     *
     * @param filePath The {@code path} for file to check.
     * @param followLinks The {@code boolean} that decides if symlinks will be followed while
     *                       finding if file exists. Check {@link #getFileType(String, boolean)}
     *                       for details.
     * @return Returns {@code true} if file exists, otherwise {@code false}.
     */
    public static boolean fileExists(final String filePath, final boolean followLinks) {
        return getFileType(filePath, followLinks) != FileType.NO_EXIST;
    }

    /**
     * Get the type of file that exists at {@code filePath}.
     * <p>
     * This function is a wrapper for
     * {@link FileTypes#getFileType(String, boolean)}
     *
     * @param filePath The {@code path} for file to check.
     * @param followLinks The {@code boolean} that decides if symlinks will be followed while
     *                       finding type. If set to {@code true}, then type of symlink target will
     *                       be returned if file at {@code filePath} is a symlink. If set to
     *                       {@code false}, then type of file at {@code filePath} itself will be
     *                       returned.
     * @return Returns the {@link FileType} of file.
     */
    @NonNull
    public static FileType getFileType(final String filePath, final boolean followLinks) {
        return FileTypes.getFileType(filePath, followLinks);
    }

    /**
     * Validate the existence and permissions of regular file at path.
     * <p>
     * If the {@code parentDirPath} is not {@code null}, then setting of missing permissions will
     * only be done if {@code path} is under {@code parentDirPath}.
     *
     * @param label The optional label for the regular file. This can optionally be {@code null}.
     * @param filePath The {@code path} for file to validate. Symlinks will not be followed.
     * @param parentDirPath The optional {@code parent directory path} to restrict operations to.
     *                      This can optionally be {@code null}. It is not canonicalized and only normalized.
     * @param permissionsToCheck The 3 character string that contains the "r", "w", "x" or "-" in-order.
     * @param setPermissions The {@code boolean} that decides if permissions are to be
     *                              automatically set defined by {@code permissionsToCheck}.
     * @param setMissingPermissionsOnly The {@code boolean} that decides if only missing permissions
     *                                  are to be set or if they should be overridden.
     * @param ignoreErrorsIfPathIsUnderParentDirPath The {@code boolean} that decides if permission
     *                                               errors are to be ignored if path is under
     *                                               {@code parentDirPath}.
     * @return Returns the {@code error} if path is not a regular file, or validating permissions
     * failed, otherwise {@code null}.
     */
    public static Error validateRegularFileExistenceAndPermissions(String label, final String filePath, final String parentDirPath,
                                                                   final String permissionsToCheck, final boolean setPermissions, final boolean setMissingPermissionsOnly,
                                                                   final boolean ignoreErrorsIfPathIsUnderParentDirPath) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "regular file path", "validateRegularFileExistenceAndPermissions");

        try {
            FileType fileType = getFileType(filePath, false);

            // If file exists but not a regular file
            if (fileType != FileType.NO_EXIST && fileType != FileType.REGULAR) {
                return FileUtilsErrno.ERRNO_NON_REGULAR_FILE_FOUND.getError(label + "file", filePath).setLabel(label + "file");
            }

            boolean isPathUnderParentDirPath = false;
            if (parentDirPath != null) {
                // The path can only be under parent directory path
                isPathUnderParentDirPath = isPathInDirPath(filePath, parentDirPath, true);
            }

            // If setPermissions is enabled and path is a regular file
            if (setPermissions && permissionsToCheck != null && fileType == FileType.REGULAR) {
                // If there is not parentDirPath restriction or path is under parentDirPath
                if (parentDirPath == null || (isPathUnderParentDirPath && getFileType(parentDirPath, false) == FileType.DIRECTORY)) {
                    if (setMissingPermissionsOnly)
                        setMissingFilePermissions(label + "file", filePath, permissionsToCheck);
                    else
                        setFilePermissions(label + "file", filePath, permissionsToCheck);
                }
            }

            // If path is not a regular file
            // Regular files cannot be automatically created so we do not ignore if missing
            if (fileType != FileType.REGULAR) {
                label += "regular file";
                return FileUtilsErrno.ERRNO_FILE_NOT_FOUND_AT_PATH.getError(label, filePath).setLabel(label);
            }

            // If there is not parentDirPath restriction or path is not under parentDirPath or
            // if permission errors must not be ignored for paths under parentDirPath
            if (parentDirPath == null || !isPathUnderParentDirPath || !ignoreErrorsIfPathIsUnderParentDirPath) {
                if (permissionsToCheck != null) {
                    // Check if permissions are missing
                    return checkMissingFilePermissions(label + "regular", filePath, permissionsToCheck, false);
                }
            }
        } catch (Exception e) {
            return FileUtilsErrno.ERRNO_VALIDATE_FILE_EXISTENCE_AND_PERMISSIONS_FAILED_WITH_EXCEPTION.getError(e, label + "file", filePath, e.getMessage());
        }

        return null;

    }

    /**
     * Validate the existence and permissions of directory file at path.
     * <p>
     * If the {@code parentDirPath} is not {@code null}, then creation of missing directory and
     * setting of missing permissions will only be done if {@code path} is under
     * {@code parentDirPath} or equals {@code parentDirPath}.
     *
     * @param label The optional label for the directory file. This can optionally be {@code null}.
     * @param filePath The {@code path} for file to validate or create. Symlinks will not be followed.
     * @param parentDirPath The optional {@code parent directory path} to restrict operations to.
     *                      This can optionally be {@code null}. It is not canonicalized and only normalized.
     * @param createDirectoryIfMissing The {@code boolean} that decides if directory file
     *                                 should be created if its missing.
     * @param permissionsToCheck The 3 character string that contains the "r", "w", "x" or "-" in-order.
     * @param setPermissions The {@code boolean} that decides if permissions are to be
     *                              automatically set defined by {@code permissionsToCheck}.
     * @param setMissingPermissionsOnly The {@code boolean} that decides if only missing permissions
     *                                  are to be set or if they should be overridden.
     * @param ignoreErrorsIfPathIsInParentDirPath The {@code boolean} that decides if existence
     *                                  and permission errors are to be ignored if path is
     *                                  in {@code parentDirPath}.
     * @param ignoreIfNotExecutable The {@code boolean} that decides if missing executable permission
     *                              error is to be ignored. This allows making an attempt to set
     *                              executable permissions, but ignoring if it fails.
     * @return Returns the {@code error} if path is not a directory file, failed to create it,
     * or validating permissions failed, otherwise {@code null}.
     */
    public static Error validateDirectoryFileExistenceAndPermissions(String label, final String filePath, final String parentDirPath, final boolean createDirectoryIfMissing,
                                                                     final String permissionsToCheck, final boolean setPermissions, final boolean setMissingPermissionsOnly,
                                                                     final boolean ignoreErrorsIfPathIsInParentDirPath, final boolean ignoreIfNotExecutable) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "directory file path", "validateDirectoryExistenceAndPermissions");

        try {
            File file = new File(filePath);
            FileType fileType = getFileType(filePath, false);

            // If file exists but not a directory file
            if (fileType != FileType.NO_EXIST && fileType != FileType.DIRECTORY) {
                return FileUtilsErrno.ERRNO_NON_DIRECTORY_FILE_FOUND.getError(label + "directory", filePath).setLabel(label + "directory");
            }

            boolean isPathInParentDirPath = false;
            if (parentDirPath != null) {
                // The path can be equal to parent directory path or under it
                isPathInParentDirPath = isPathInDirPath(filePath, parentDirPath, false);
            }

            if (createDirectoryIfMissing || setPermissions) {
                // If there is not parentDirPath restriction or path is in parentDirPath
                if (parentDirPath == null || (isPathInParentDirPath && getFileType(parentDirPath, false) == FileType.DIRECTORY)) {
                    // If createDirectoryIfMissing is enabled and no file exists at path, then create directory
                    if (createDirectoryIfMissing && fileType == FileType.NO_EXIST) {

                        // Create directory and update fileType if successful, otherwise return with error
                        // It "might" be possible that mkdirs returns false even though directory was created
                        boolean result = file.mkdirs();
                        fileType = getFileType(filePath, false);
                        if (!result && fileType != FileType.DIRECTORY)
                            return FileUtilsErrno.ERRNO_CREATING_FILE_FAILED.getError(label + "directory file", filePath);
                    }

                    // If setPermissions is enabled and path is a directory
                    if (setPermissions && permissionsToCheck != null && fileType == FileType.DIRECTORY) {
                        if (setMissingPermissionsOnly)
                            setMissingFilePermissions(label + "directory", filePath, permissionsToCheck);
                        else
                            setFilePermissions(label + "directory", filePath, permissionsToCheck);
                    }
                }
            }

            // If there is not parentDirPath restriction or path is not in parentDirPath or
            // if existence or permission errors must not be ignored for paths in parentDirPath
            if (parentDirPath == null || !isPathInParentDirPath || !ignoreErrorsIfPathIsInParentDirPath) {
                // If path is not a directory
                // Directories can be automatically created so we can ignore if missing with above check
                if (fileType != FileType.DIRECTORY) {
                    label += "directory";
                    return FileUtilsErrno.ERRNO_FILE_NOT_FOUND_AT_PATH.getError(label, filePath).setLabel(label);
                }

                if (permissionsToCheck != null) {
                    // Check if permissions are missing
                    return checkMissingFilePermissions(label + "directory", filePath, permissionsToCheck, ignoreIfNotExecutable);
                }
            }
        } catch (Exception e) {
            return FileUtilsErrno.ERRNO_VALIDATE_DIRECTORY_EXISTENCE_AND_PERMISSIONS_FAILED_WITH_EXCEPTION.getError(e, label + "directory file", filePath, e.getMessage());
        }

        return null;
    }

    /**
     * Create parent directory of file at path.
     * <p>
     * This function is a wrapper for
     * {@link #validateDirectoryFileExistenceAndPermissions(String, String, String, boolean, String, boolean, boolean, boolean, boolean)}.
     *
     * @param label The optional label for the parent directory file. This can optionally be {@code null}.
     * @param filePath The {@code path} for file whose parent needs to be created.
     * @return Returns the {@code error} if parent path is not a directory file or failed to create it,
     * otherwise {@code null}.
     */
    public static Error createParentDirectoryFile(final String label, final String filePath) {
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "file path", "createParentDirectoryFile");

        File file = new File(filePath);
        String fileParentPath = file.getParent();

        if (fileParentPath != null)
            return createDirectoryFile(label, fileParentPath,
                null, false, false);
        else
            return null;
    }

    /**
     * Create a directory file at path.
     * <p>
     * This function is a wrapper for
     * {@link #validateDirectoryFileExistenceAndPermissions(String, String, String, boolean, String, boolean, boolean, boolean, boolean)}.
     *
     * @param filePath The {@code path} for directory file to create.
     * @return Returns the {@code error} if path is not a directory file or failed to create it,
     * otherwise {@code null}.
     */
    public static Error createDirectoryFile(final String filePath) {
        return createDirectoryFile(null, filePath);
    }

    /**
     * Create a directory file at path.
     * <p>
     * This function is a wrapper for
     * {@link #validateDirectoryFileExistenceAndPermissions(String, String, String, boolean, String, boolean, boolean, boolean, boolean)}.
     *
     * @param label The optional label for the directory file. This can optionally be {@code null}.
     * @param filePath The {@code path} for directory file to create.
     * @return Returns the {@code error} if path is not a directory file or failed to create it,
     * otherwise {@code null}.
     */
    public static Error createDirectoryFile(final String label, final String filePath) {
        return createDirectoryFile(label, filePath,
            null, false, false);
    }

    /**
     * Create a directory file at path.
     * <p>
     * This function is a wrapper for
     * {@link #validateDirectoryFileExistenceAndPermissions(String, String, String, boolean, String, boolean, boolean, boolean, boolean)}.
     *
     * @param label The optional label for the directory file. This can optionally be {@code null}.
     * @param filePath The {@code path} for directory file to create.
     * @param permissionsToCheck The 3 character string that contains the "r", "w", "x" or "-" in-order.
     * @param setPermissions The {@code boolean} that decides if permissions are to be
     *                              automatically set defined by {@code permissionsToCheck}.
     * @param setMissingPermissionsOnly The {@code boolean} that decides if only missing permissions
     *                                  are to be set or if they should be overridden.
     * @return Returns the {@code error} if path is not a directory file, failed to create it,
     * or validating permissions failed, otherwise {@code null}.
     */
    public static Error createDirectoryFile(final String label, final String filePath,
                                            final String permissionsToCheck, final boolean setPermissions, final boolean setMissingPermissionsOnly) {
        return validateDirectoryFileExistenceAndPermissions(label, filePath,
            null, true,
            permissionsToCheck, setPermissions, setMissingPermissionsOnly,
            false, false);
    }

    /**
     * Create a symlink file at path.
     * <p>
     * This function is a wrapper for
     * {@link #createSymlinkFile(String, String, String, boolean, boolean, boolean)}.
     * <p>
     * Dangling symlinks will be allowed.
     * Symlink destination will be overwritten if it already exists but only if its a symlink.
     *
     * @param label The optional label for the symlink file. This can optionally be {@code null}.
     * @param targetFilePath The {@code path} TO which the symlink file will be created.
     * @param destFilePath The {@code path} AT which the symlink file will be created.
     * @return Returns the {@code error} if path is not a symlink file, failed to create it,
     * otherwise {@code null}.
     */
    public static Error createSymlinkFile(String label, final String targetFilePath, final String destFilePath) {
        return createSymlinkFile(label, targetFilePath, destFilePath,
            true, true, true);
    }

    /**
     * Create a symlink file at path.
     *
     * @param label The optional label for the symlink file. This can optionally be {@code null}.
     * @param targetFilePath The {@code path} TO which the symlink file will be created.
     * @param destFilePath The {@code path} AT which the symlink file will be created.
     * @param allowDangling The {@code boolean} that decides if it should be considered an
     *                              error if source file doesn't exist.
     * @param overwrite The {@code boolean} that decides if destination file should be overwritten if
     *                  it already exists. If set to {@code true}, then destination file will be
     *                  deleted before symlink is created.
     * @param overwriteOnlyIfDestIsASymlink The {@code boolean} that decides if overwrite should
     *                                         only be done if destination file is also a symlink.
     * @return Returns the {@code error} if path is not a symlink file, failed to create it,
     * or validating permissions failed, otherwise {@code null}.
     */
    public static Error createSymlinkFile(String label, final String targetFilePath, final String destFilePath,
                                          final boolean allowDangling, final boolean overwrite, final boolean overwriteOnlyIfDestIsASymlink) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (targetFilePath == null || targetFilePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "target file path", "createSymlinkFile");
        if (destFilePath == null || destFilePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "destination file path", "createSymlinkFile");

        Error error;

        try {
            File destFile = new File(destFilePath);

            String targetFileAbsolutePath = targetFilePath;
            // If target path is relative instead of absolute
            if (!targetFilePath.startsWith("/")) {
                String destFileParentPath = destFile.getParent();
                if (destFileParentPath != null)
                    targetFileAbsolutePath = destFileParentPath + "/" +  targetFilePath;
            }

            FileType targetFileType = getFileType(targetFileAbsolutePath, false);
            FileType destFileType = getFileType(destFilePath, false);

            // If target file does not exist
            if (targetFileType == FileType.NO_EXIST) {
                // If dangling symlink should not be allowed, then return with error
                if (!allowDangling) {
                    label += "symlink target file";
                    return FileUtilsErrno.ERRNO_FILE_NOT_FOUND_AT_PATH.getError(label, targetFileAbsolutePath).setLabel(label);
                }
            }

            // If destination exists
            if (destFileType != FileType.NO_EXIST) {
                // If destination must not be overwritten
                if (!overwrite) {
                    return null;
                }

                // If overwriteOnlyIfDestIsASymlink is enabled but destination file is not a symlink
                if (overwriteOnlyIfDestIsASymlink && destFileType != FileType.SYMLINK)
                    return FileUtilsErrno.ERRNO_CANNOT_OVERWRITE_A_NON_SYMLINK_FILE_TYPE.getError(label + " file", destFilePath, targetFilePath, destFileType.getName());

                // Delete the destination file
                error = deleteFile(label + "symlink destination", destFilePath, true);
                if (error != null)
                    return error;
            } else {
                // Create the destination file parent directory
                error = createParentDirectoryFile(label + "symlink destination file parent", destFilePath);
                if (error != null)
                    return error;
            }

            // create a symlink at destFilePath to targetFilePath

            Os.symlink(targetFilePath, destFilePath);
        } catch (Exception e) {
            return FileUtilsErrno.ERRNO_CREATING_SYMLINK_FILE_FAILED_WITH_EXCEPTION.getError(e, label + "symlink file", destFilePath, targetFilePath, e.getMessage());
        }

        return null;
    }

    /**
     * Move a regular file from {@code sourceFilePath} to {@code destFilePath}.
     * <p>
     * This function is a wrapper for
     * {@link #copyOrMoveFile(String, String, String, boolean, boolean, int, boolean, boolean)}.
     * <p>
     * If destination file already exists, then it will be overwritten, but only if its a regular
     * file, otherwise an error will be returned.
     *
     * @param label The optional label for file to move. This can optionally be {@code null}.
     * @param srcFilePath The {@code source path} for file to move.
     * @param destFilePath The {@code destination path} for file to move.
     * @param ignoreNonExistentSrcFile The {@code boolean} that decides if it should be considered an
     *                              error if source file to moved doesn't exist.
     * @return Returns the {@code error} if move was not successful, otherwise {@code null}.
     */
    public static Error moveRegularFile(final String label, final String srcFilePath, final String destFilePath, final boolean ignoreNonExistentSrcFile) {
        return copyOrMoveFile(label, srcFilePath, destFilePath,
            true, ignoreNonExistentSrcFile, FileType.REGULAR.getValue(),
            true, true);
    }

    /**
     * Copy or move a file from {@code sourceFilePath} to {@code destFilePath}.
     * <p>
     * The {@code sourceFilePath} and {@code destFilePath} must be the canonical path to the source
     * and destination since symlinks will not be followed.
     * <p>
     * If the {@code sourceFilePath} or {@code destFilePath} is a canonical path to a directory,
     * then any symlink files found under the directory will be deleted, but not their targets when
     * deleting source after move and deleting destination before copy/move.
     *
     * @param label The optional label for file to copy or move. This can optionally be {@code null}.
     * @param srcFilePath The {@code source path} for file to copy or move.
     * @param destFilePath The {@code destination path} for file to copy or move.
     * @param moveFile The {@code boolean} that decides if source file needs to be copied or moved.
     *                 If set to {@code true}, then source file will be moved, otherwise it will be
     *                 copied.
     * @param ignoreNonExistentSrcFile The {@code boolean} that decides if it should be considered an
     *                              error if source file to copied or moved doesn't exist.
     * @param allowedFileTypeFlags The flags that are matched against the source file's {@link FileType}
     *                             to see if it should be copied/moved or not. This is a safety measure
     *                             to prevent accidental copy/move/delete of the wrong type of file,
     *                             like a directory instead of a regular file. You can pass
     *                             {@link FileTypes#FILE_TYPE_ANY_FLAGS} to allow copy/move of any file type.
     * @param overwrite The {@code boolean} that decides if destination file should be overwritten if
     *                  it already exists. If set to {@code true}, then destination file will be
     *                  deleted before source is copied or moved.
     * @param overwriteOnlyIfDestSameFileTypeAsSrc The {@code boolean} that decides if overwrite should
     *                                         only be done if destination file is also the same file
     *                                          type as the source file.
     * @return Returns the {@code error} if copy or move was not successful, otherwise {@code null}.
     */
    public static Error copyOrMoveFile(String label, final String srcFilePath, final String destFilePath,
                                       final boolean moveFile, final boolean ignoreNonExistentSrcFile, int allowedFileTypeFlags,
                                       final boolean overwrite, final boolean overwriteOnlyIfDestSameFileTypeAsSrc) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (srcFilePath == null || srcFilePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "source file path", "copyOrMoveFile");
        if (destFilePath == null || destFilePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "destination file path", "copyOrMoveFile");

        String mode = (moveFile ? "Moving" : "Copying");
        String modePast = (moveFile ? "moved" : "copied");

        Error error;

        try {

            File srcFile = new File(srcFilePath);
            File destFile = new File(destFilePath);

            FileType srcFileType = getFileType(srcFilePath, false);
            FileType destFileType = getFileType(destFilePath, false);

            String srcFileCanonicalPath = srcFile.getCanonicalPath();
            String destFileCanonicalPath = destFile.getCanonicalPath();

            // If source file does not exist
            if (srcFileType == FileType.NO_EXIST) {
                // If copy or move is to be ignored if source file is not found
                if (ignoreNonExistentSrcFile)
                    return null;
                    // Else return with error
                else {
                    label += "source file";
                    return FileUtilsErrno.ERRNO_FILE_NOT_FOUND_AT_PATH.getError(label, srcFilePath).setLabel(label);
                }
            }

            // If the file type of the source file does not exist in the allowedFileTypeFlags, then return with error
            if ((allowedFileTypeFlags & srcFileType.getValue()) <= 0)
                return FileUtilsErrno.ERRNO_FILE_NOT_AN_ALLOWED_FILE_TYPE.getError(label + "source file meant to be " + modePast, srcFilePath, FileTypes.convertFileTypeFlagsToNamesString(allowedFileTypeFlags));

            // If source and destination file path are the same
            if (srcFileCanonicalPath.equals(destFileCanonicalPath))
                return FileUtilsErrno.ERRNO_COPYING_OR_MOVING_FILE_TO_SAME_PATH.getError(mode + " " + label + "source file", srcFilePath, destFilePath);

            // If destination exists
            if (destFileType != FileType.NO_EXIST) {
                // If destination must not be overwritten
                if (!overwrite) {
                    return null;
                }

                // If overwriteOnlyIfDestSameFileTypeAsSrc is enabled but destination file does not match source file type
                if (overwriteOnlyIfDestSameFileTypeAsSrc && destFileType != srcFileType)
                    return FileUtilsErrno.ERRNO_CANNOT_OVERWRITE_A_DIFFERENT_FILE_TYPE.getError(label + "source file", mode.toLowerCase(), srcFilePath, destFilePath, destFileType.getName(), srcFileType.getName());

                // Delete the destination file
                error = deleteFile(label + "destination", destFilePath, true);
                if (error != null)
                    return error;
            }


            // Copy or move source file to dest
            boolean copyFile = !moveFile;

            // If moveFile is true
            if (moveFile) {
                // We first try to rename source file to destination file to save a copy operation in case both source and destination are on the same filesystem

                // https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:libcore/ojluni/src/main/java/java/io/UnixFileSystem.java;l=358
                // https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:libcore/luni/src/main/java/android/system/Os.java;l=512
                // Uses File.getPath() to get the path of source and destination and not the canonical path
                if (!srcFile.renameTo(destFile)) {
                    // If destination directory is a subdirectory of the source directory
                    // Copying is still allowed by copyDirectory() by excluding destination directory files
                    if (srcFileType == FileType.DIRECTORY && destFileCanonicalPath.startsWith(srcFileCanonicalPath + File.separator))
                        return FileUtilsErrno.ERRNO_CANNOT_MOVE_DIRECTORY_TO_SUB_DIRECTORY_OF_ITSELF.getError(label + "source directory", srcFilePath, destFilePath);

                    // If rename failed, then we copy

                    copyFile = true;
                }
            }

            // If moveFile is false or renameTo failed while moving
            if (copyFile) {


                // Create the dest file parent directory
                error = createParentDirectoryFile(label + "dest file parent", destFilePath);
                if (error != null)
                    return error;

                if (srcFileType == FileType.DIRECTORY) {
                    // Will give runtime exceptions on android < 8 due to missing classes like java.nio.file.Path if org.apache.commons.io version > 2.5
                    org.apache.commons.io.FileUtils.copyDirectory(srcFile, destFile, true);
                } else if (srcFileType == FileType.SYMLINK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        java.nio.file.Files.copy(srcFile.toPath(), destFile.toPath(), LinkOption.NOFOLLOW_LINKS, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        // read the target for the source file and create a symlink at dest
                        // source file metadata will be lost
                        error = createSymlinkFile(label + "dest", Os.readlink(srcFilePath), destFilePath);
                        if (error != null)
                            return error;
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        java.nio.file.Files.copy(srcFile.toPath(), destFile.toPath(), LinkOption.NOFOLLOW_LINKS, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        // Will give runtime exceptions on android < 8 due to missing classes like java.nio.file.Path if org.apache.commons.io version > 2.5
                        org.apache.commons.io.FileUtils.copyFile(srcFile, destFile, true);
                    }
                }
            }

            // If source file had to be moved
            if (moveFile) {
                // Delete the source file since copying would have succeeded
                error = deleteFile(label + "source", srcFilePath, true);
                if (error != null)
                    return error;
            }

        }
        catch (Exception e) {
            return FileUtilsErrno.ERRNO_COPYING_OR_MOVING_FILE_FAILED_WITH_EXCEPTION.getError(e, mode + " " + label + "file", srcFilePath, destFilePath, e.getMessage());
        }

        return null;
    }

    /**
     * Delete regular, directory or symlink file at path.
     * <p>
     * This function is a wrapper for {@link #deleteFile(String, String, boolean, boolean, int)}.
     *
     * @param label The optional label for file to delete. This can optionally be {@code null}.
     * @param filePath The {@code path} for file to delete.
     * @param ignoreNonExistentFile The {@code boolean} that decides if it should be considered an
     *                              error if file to deleted doesn't exist.
     * @return Returns the {@code error} if deletion was not successful, otherwise {@code null}.
     */
    public static Error deleteFile(String label, final String filePath, final boolean ignoreNonExistentFile) {
        return deleteFile(label, filePath, ignoreNonExistentFile, false, FileTypes.FILE_TYPE_NORMAL_FLAGS);
    }

    /**
     * Delete file at path.
     * <p>
     * The {@code filePath} must be the canonical path to the file to be deleted since symlinks will
     * not be followed.
     * If the {@code filePath} is a canonical path to a directory, then any symlink files found under
     * the directory will be deleted, but not their targets.
     *
     * @param label The optional label for file to delete. This can optionally be {@code null}.
     * @param filePath The {@code path} for file to delete.
     * @param ignoreNonExistentFile The {@code boolean} that decides if it should be considered an
     *                              error if file to deleted doesn't exist.
     * @param ignoreWrongFileType The {@code boolean} that decides if it should be considered an
     *                              error if file type is not one from {@code allowedFileTypeFlags}.
     * @param allowedFileTypeFlags The flags that are matched against the file's {@link FileType} to
     *                             see if it should be deleted or not. This is a safety measure to
     *                             prevent accidental deletion of the wrong type of file, like a
     *                             directory instead of a regular file. You can pass
     *                             {@link FileTypes#FILE_TYPE_ANY_FLAGS} to allow deletion of any file type.
     * @return Returns the {@code error} if deletion was not successful, otherwise {@code null}.
     */
    public static Error deleteFile(String label, final String filePath, final boolean ignoreNonExistentFile, final boolean ignoreWrongFileType, int allowedFileTypeFlags) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "file path", "deleteFile");

        try {
            File file = new File(filePath);
            FileType fileType = getFileType(filePath, false);

            // If file does not exist
            if (fileType == FileType.NO_EXIST) {
                // If delete is to be ignored if file does not exist
                if (ignoreNonExistentFile)
                    return null;
                    // Else return with error
                else {
                    label += "file meant to be deleted";
                    return FileUtilsErrno.ERRNO_FILE_NOT_FOUND_AT_PATH.getError(label, filePath).setLabel(label);
                }
            }

            // If the file type of the file does not exist in the allowedFileTypeFlags
            if ((allowedFileTypeFlags & fileType.getValue()) <= 0) {
                // If wrong file type is to be ignored
                if (ignoreWrongFileType) {
                    return null;
                }

                // Else return with error
                return FileUtilsErrno.ERRNO_FILE_NOT_AN_ALLOWED_FILE_TYPE.getError(label + "file meant to be deleted", filePath, fileType.getName(), FileTypes.convertFileTypeFlagsToNamesString(allowedFileTypeFlags));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                /*
                 * Try to use {@link SecureDirectoryStream} if available for safer directory
                 * deletion, it should be available for android >= 8.0
                 * https://guava.dev/releases/24.1-jre/api/docs/com/google/common/io/MoreFiles.html#deleteRecursively-java.nio.file.Path-com.google.common.io.RecursiveDeleteOption...-
                 * https://github.com/google/guava/issues/365
                 * https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:libcore/ojluni/src/main/java/sun/nio/fs/UnixSecureDirectoryStream.java
                 *
                 * MoreUtils is marked with the @Beta annotation so the API may be removed in
                 * future but has been there for a few years now.
                 *
                 * If an exception is thrown, the exception message might not contain the full errors.
                 * Individual failures get added to suppressed throwables which can be extracted
                 * from the exception object by calling `Throwable[] getSuppressed()`. So just logging
                 * the exception message and stacktrace may not be enough, the suppressed throwables
                 * need to be logged as well, which the Loger class does if they are found in the
                 * exception added to the Error that's returned by this function.
                 * https://github.com/google/guava/blob/v30.1.1/guava/src/com/google/common/io/MoreFiles.java#L775
                 */
                //noinspection UnstableApiUsage
                com.google.common.io.MoreFiles.deleteRecursively(file.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
            } else {
                if (fileType == FileType.DIRECTORY) {
                    // deleteDirectory() instead of forceDelete() gets the files list first instead of walking directory tree, so seems safer
                    // Will give runtime exceptions on android < 8 due to missing classes like java.nio.file.Path if org.apache.commons.io version > 2.5
                    org.apache.commons.io.FileUtils.deleteDirectory(file);
                } else {
                    // Will give runtime exceptions on android < 8 due to missing classes like java.nio.file.Path if org.apache.commons.io version > 2.5
                    org.apache.commons.io.FileUtils.forceDelete(file);
                }
            }

            // If file still exists after deleting it
            fileType = getFileType(filePath, false);
            if (fileType != FileType.NO_EXIST)
                return FileUtilsErrno.ERRNO_FILE_STILL_EXISTS_AFTER_DELETING.getError(label + "file meant to be deleted", filePath);
        }
        catch (Exception e) {
            return FileUtilsErrno.ERRNO_DELETING_FILE_FAILED_WITH_EXCEPTION.getError(e, label + "file", filePath, e.getMessage());
        }

        return null;
    }

    /**
     * Clear contents of directory at path without deleting the directory. If directory does not exist
     * it will be created automatically.
     * <p>
     * The {@code filePath} must be the canonical path to a directory since symlinks will not be followed.
     * Any symlink files found under the directory will be deleted, but not their targets.
     *
     * @param label The optional label for directory to clear. This can optionally be {@code null}.
     * @param filePath The {@code path} for directory to clear.
     * @return Returns the {@code error} if clearing was not successful, otherwise {@code null}.
     */
    public static Error clearDirectory(String label, final String filePath) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "file path", "clearDirectory");

        Error error;

        try {

            File file = new File(filePath);
            FileType fileType = getFileType(filePath, false);

            // If file exists but not a directory file
            if (fileType != FileType.NO_EXIST && fileType != FileType.DIRECTORY) {
                return FileUtilsErrno.ERRNO_NON_DIRECTORY_FILE_FOUND.getError(label + "directory", filePath).setLabel(label + "directory");
            }

            // If directory exists, clear its contents
            if (fileType == FileType.DIRECTORY) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    /* If an exception is thrown, the exception message might not contain the full errors.
                     * Individual failures get added to suppressed throwables. */
                    //noinspection UnstableApiUsage
                    com.google.common.io.MoreFiles.deleteDirectoryContents(file.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
                } else {
                    // Will give runtime exceptions on android < 8 due to missing classes like java.nio.file.Path if org.apache.commons.io version > 2.5
                    org.apache.commons.io.FileUtils.cleanDirectory(new File(filePath));
                }
            }
            // Else create it
            else {
                error = createDirectoryFile(label, filePath);
                if (error != null)
                    return error;
            }
        } catch (Exception e) {
            return FileUtilsErrno.ERRNO_CLEARING_DIRECTORY_FAILED_WITH_EXCEPTION.getError(e, label + "directory", filePath, e.getMessage());
        }

        return null;
    }

    /**
     * Delete files under a directory older than x days.
     * <p>
     * The {@code filePath} must be the canonical path to a directory since symlinks will not be followed.
     * Any symlink files found under the directory will be deleted, but not their targets.
     *
     * @param label The optional label for directory to clear. This can optionally be {@code null}.
     * @param filePath The {@code path} for directory to clear.
     * @param dirFilter  The optional filter to apply when finding subdirectories.
     *                   If this parameter is {@code null}, subdirectories will not be included in the
     *                   search. Use TrueFileFilter.INSTANCE to match all directories.
     * @param days The x amount of days before which files should be deleted. This must be `>=0`.
     * @param ignoreNonExistentFile The {@code boolean} that decides if it should be considered an
     *                              error if file to deleted doesn't exist.
     * @param allowedFileTypeFlags The flags that are matched against the file's {@link FileType} to
     *                             see if it should be deleted or not. This is a safety measure to
     *                             prevent accidental deletion of the wrong type of file, like a
     *                             directory instead of a regular file. You can pass
     *                             {@link FileTypes#FILE_TYPE_ANY_FLAGS} to allow deletion of any file type.
     * @return Returns the {@code error} if deleting was not successful, otherwise {@code null}.
     */
    public static Error deleteFilesOlderThanXDays(String label, final String filePath, final IOFileFilter dirFilter, int days, final boolean ignoreNonExistentFile, int allowedFileTypeFlags) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "file path", "deleteFilesOlderThanXDays");
        if (days < 0) return FunctionErrno.ERRNO_INVALID_PARAMETER.getError(label + "days", "deleteFilesOlderThanXDays", " It must be >= 0.");

        Error error;

        try {

            File file = new File(filePath);
            FileType fileType = getFileType(filePath, false);

            // If file exists but not a directory file
            if (fileType != FileType.NO_EXIST && fileType != FileType.DIRECTORY) {
                return FileUtilsErrno.ERRNO_NON_DIRECTORY_FILE_FOUND.getError(label + "directory", filePath).setLabel(label + "directory");
            }

            // If file does not exist
            if (fileType == FileType.NO_EXIST) {
                // If delete is to be ignored if file does not exist
                if (ignoreNonExistentFile)
                    return null;
                    // Else return with error
                else {
                    label += "directory under which files had to be deleted";
                    return FileUtilsErrno.ERRNO_FILE_NOT_FOUND_AT_PATH.getError(label, filePath).setLabel(label);
                }
            }

            // TODO: Use FileAttributes with support for atime (default), mtime, ctime. Add regex for ignoring file and dir absolute paths.
            // FIXME: iterateFiles() does not return subdirectories even with TrueFileFilter for file and dir.
            // FIXME: Empty directories remain

            // If directory exists, delete its contents
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -(days));
            // AgeFileFilter seems to apply to symlink destination timestamp instead of symlink file itself
            Iterator<File> filesToDelete =
                org.apache.commons.io.FileUtils.iterateFiles(file, new AgeFileFilter(calendar.getTime()), dirFilter);
            while (filesToDelete.hasNext()) {
                File subFile = filesToDelete.next();
                error = deleteFile(label + " directory sub", subFile.getAbsolutePath(), true, true, allowedFileTypeFlags);
                if (error != null)
                    return error;
            }
        } catch (Exception e) {
            return FileUtilsErrno.ERRNO_DELETING_FILES_OLDER_THAN_X_DAYS_FAILED_WITH_EXCEPTION.getError(e, label + "directory", filePath, days, e.getMessage());
        }

        return null;

    }

    /**
     * Write text {@code dataString} with a specific {@link Charset} to file at path.
     *
     * @param label The optional label for file to write. This can optionally be {@code null}.
     * @param filePath The {@code path} for file to write.
     * @param charset The {@link Charset} of the {@code dataString}. If this is {@code null},
     *                then default {@link Charset} will be used.
     * @param dataString The data to write to file.
     * @param append The {@code boolean} that decides if file should be appended to or not.
     * @return Returns the {@code error} if writing was not successful, otherwise {@code null}.
     */
    public static Error writeTextToFile(String label, final String filePath, Charset charset, final String dataString, final boolean append) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "file path", "writeStringToFile");

        Error error;

        error = preWriteToFile(label, filePath);
        if (error != null)
            return error;

        if (charset == null) charset = Charset.defaultCharset();

        // Check if charset is supported
        error = isCharsetSupported(charset);
        if (error != null)
            return error;

        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            // Write text to file
            fileOutputStream = new FileOutputStream(filePath, append);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, charset));

            bufferedWriter.write(dataString);
            bufferedWriter.flush();
        } catch (Exception e) {
            return FileUtilsErrno.ERRNO_WRITING_TEXT_TO_FILE_FAILED_WITH_EXCEPTION.getError(e, label + "file", filePath, e.getMessage());
        } finally {
            closeCloseable(fileOutputStream);
            closeCloseable(bufferedWriter);
        }

        return null;
    }

    private static Error preWriteToFile(String label, String filePath) {
        Error error;

        FileType fileType = getFileType(filePath, false);

        // If file exists but not a regular file
        if (fileType != FileType.NO_EXIST && fileType != FileType.REGULAR) {
            return FileUtilsErrno.ERRNO_NON_REGULAR_FILE_FOUND.getError(label + "file", filePath).setLabel(label + "file");
        }

        // Create the file parent directory
        error = createParentDirectoryFile(label + "file parent", filePath);
        return error;
    }

    /**
     * Check if a specific {@link Charset} is supported.
     *
     * @param charset The {@link Charset} to check.
     * @return Returns the {@code error} if charset is not supported or failed to check it, otherwise {@code null}.
     */
    public static Error isCharsetSupported(final Charset charset) {
        if (charset == null) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError("charset", "isCharsetSupported");

        try {
            if (!Charset.isSupported(charset.name())) {
                return FileUtilsErrno.ERRNO_UNSUPPORTED_CHARSET.getError(charset.name());
            }
        } catch (Exception e) {
            return FileUtilsErrno.ERRNO_CHECKING_IF_CHARSET_SUPPORTED_FAILED.getError(e, charset.name(), e.getMessage());
        }

        return null;
    }

    /**
     * Close a {@link Closeable} object if not {@code null} and ignore any exceptions raised.
     *
     * @param closeable The {@link Closeable} object to close.
     */
    public static void closeCloseable(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Set permissions for file at path. Existing permission outside the {@code permissionsToSet}
     * will be removed.
     *
     * @param label The optional label for the file. This can optionally be {@code null}.
     * @param filePath The {@code path} for file to set permissions to.
     * @param permissionsToSet The 3 character string that contains the "r", "w", "x" or "-" in-order.
     */
    public static void setFilePermissions(String label, final String filePath, final String permissionsToSet) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return;

        if (!isValidPermissionString(permissionsToSet)) {
            return;
        }

        File file = new File(filePath);

        if (permissionsToSet.contains("r")) {
            if (!file.canRead()) {
                file.setReadable(true);
            }
        } else {
            if (file.canRead()) {
                file.setReadable(false);
            }
        }


        if (permissionsToSet.contains("w")) {
            if (!file.canWrite()) {
                file.setWritable(true);
            }
        } else {
            if (file.canWrite()) {
                file.setWritable(false);
            }
        }


        if (permissionsToSet.contains("x")) {
            if (!file.canExecute()) {
                file.setExecutable(true);
            }
        } else {
            if (file.canExecute()) {
                file.setExecutable(false);
            }
        }
    }

    /**
     * Set missing permissions for file at path. Existing permission outside the {@code permissionsToSet}
     * will not be removed.
     *
     * @param label The optional label for the file. This can optionally be {@code null}.
     * @param filePath The {@code path} for file to set permissions to.
     * @param permissionsToSet The 3 character string that contains the "r", "w", "x" or "-" in-order.
     */
    public static void setMissingFilePermissions(String label, final String filePath, final String permissionsToSet) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return;

        if (!isValidPermissionString(permissionsToSet)) {
            return;
        }

        File file = new File(filePath);

        if (permissionsToSet.contains("r") && !file.canRead()) {
            file.setReadable(true);
        }

        if (permissionsToSet.contains("w") && !file.canWrite()) {
            file.setWritable(true);
        }

        if (permissionsToSet.contains("x") && !file.canExecute()) {
            file.setExecutable(true);
        }
    }

    /**
     * Checking missing permissions for file at path.
     *
     * @param label The optional label for the file. This can optionally be {@code null}.
     * @param filePath The {@code path} for file to check permissions for.
     * @param permissionsToCheck The 3 character string that contains the "r", "w", "x" or "-" in-order.
     * @param ignoreIfNotExecutable The {@code boolean} that decides if missing executable permission
     *                              error is to be ignored.
     * @return Returns the {@code error} if validating permissions failed, otherwise {@code null}.
     */
    public static Error checkMissingFilePermissions(String label, final String filePath, final String permissionsToCheck, final boolean ignoreIfNotExecutable) {
        label = (label == null || label.isEmpty() ? "" : label + " ");
        if (filePath == null || filePath.isEmpty()) return FunctionErrno.ERRNO_NULL_OR_EMPTY_PARAMETER.getError(label + "file path", "checkMissingFilePermissions");

        if (!isValidPermissionString(permissionsToCheck)) {
            return FileUtilsErrno.ERRNO_INVALID_FILE_PERMISSIONS_STRING_TO_CHECK.getError();
        }

        File file = new File(filePath);

        // If file is not readable
        if (permissionsToCheck.contains("r") && !file.canRead()) {
            return FileUtilsErrno.ERRNO_FILE_NOT_READABLE.getError(label + "file", filePath).setLabel(label + "file");
        }

        // If file is not writable
        if (permissionsToCheck.contains("w") && !file.canWrite()) {
            return FileUtilsErrno.ERRNO_FILE_NOT_WRITABLE.getError(label + "file", filePath).setLabel(label + "file");
        }
        // If file is not executable
        // This canExecute() will give "avc: granted { execute }" warnings for target sdk 29
        else if (permissionsToCheck.contains("x") && !file.canExecute() && !ignoreIfNotExecutable) {
            return FileUtilsErrno.ERRNO_FILE_NOT_EXECUTABLE.getError(label + "file", filePath).setLabel(label + "file");
        }

        return null;
    }

    /**
     * Checks whether string exactly matches the 3 character permission string that
     * contains the "r", "w", "x" or "-" in-order.
     *
     * @param string The {@link String} to check.
     * @return Returns {@code true} if string exactly matches a permission string, otherwise {@code false}.
     */
    public static boolean isValidPermissionString(final String string) {
        if (string == null || string.isEmpty()) return false;
        return Pattern.compile("^([r-])[w-][x-]$", 0).matcher(string).matches();
    }

    /**
     * Get file basename for file at {@code filePath}.
     *
     * @param filePath The {@code path} for file.
     * @return Returns the file basename if not {@code null}.
     */
    public static String getFileBasename(String filePath) {
        if (DataUtils.isNullOrEmpty(filePath)) return null;
        int lastSlash = filePath.lastIndexOf('/');
        return (lastSlash == -1) ? filePath : filePath.substring(lastSlash + 1);
    }

}
