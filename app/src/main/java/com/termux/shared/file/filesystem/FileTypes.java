package com.termux.shared.file.filesystem;

import android.system.Os;

import androidx.annotation.NonNull;

import java.io.File;

public class FileTypes {

    /** Flags to represent regular, directory and symlink file types defined by {@link FileType} */
    public static final int FILE_TYPE_NORMAL_FLAGS = FileType.REGULAR.getValue() | FileType.DIRECTORY.getValue() | FileType.SYMLINK.getValue();

    /** Flags to represent any file type defined by {@link FileType} */
    public static final int FILE_TYPE_ANY_FLAGS = Integer.MAX_VALUE; // 1111111111111111111111111111111 (31 1's)

    public static String convertFileTypeFlagsToNamesString(int fileTypeFlags) {
        StringBuilder fileTypeFlagsStringBuilder = new StringBuilder();

        FileType[] fileTypes = {FileType.REGULAR, FileType.DIRECTORY, FileType.SYMLINK, FileType.CHARACTER, FileType.FIFO, FileType.BLOCK, FileType.UNKNOWN};
        for (FileType fileType : fileTypes) {
            if ((fileTypeFlags & fileType.getValue()) > 0)
                fileTypeFlagsStringBuilder.append(fileType.getName()).append(",");
        }

        String fileTypeFlagsString = fileTypeFlagsStringBuilder.toString();

        if (fileTypeFlagsString.endsWith(","))
            fileTypeFlagsString = fileTypeFlagsString.substring(0, fileTypeFlagsString.lastIndexOf(","));

        return fileTypeFlagsString;
    }

    /**
     * Checks the type of file that exists at {@code filePath}.
     * <p>
     * Returns:
     * - {@link FileType#NO_EXIST} if {@code filePath} is {@code null}, empty, an exception is raised
     *      or no file exists at {@code filePath}.
     * - {@link FileType#REGULAR} if file at {@code filePath} is a regular file.
     * - {@link FileType#DIRECTORY} if file at {@code filePath} is a directory file.
     * - {@link FileType#SYMLINK} if file at {@code filePath} is a symlink file and {@code followLinks} is {@code false}.
     * - {@link FileType#CHARACTER} if file at {@code filePath} is a character special file.
     * - {@link FileType#FIFO} if file at {@code filePath} is a fifo special file.
     * - {@link FileType#BLOCK} if file at {@code filePath} is a block special file.
     * - {@link FileType#UNKNOWN} if file at {@code filePath} is of unknown type.
     * <p>
     * The {@link File#isFile()} and {@link File#isDirectory()} uses {@link Os#stat(String)} system
     * call (not {@link Os#lstat(String)}) to check file type and does follow symlinks.
     * <p>
     * The {@link File#exists()} uses {@link Os#access(String, int)} system call to check if file is
     * accessible and does not follow symlinks. However, it returns {@code false} for dangling symlinks,
     * on android at l<a href="east.">Check https://stackoverflow.com/a/57747</a>064/14686958
     * <p>
     * Basically {@link File} API is not reliable to check for symlinks.
     * <p>
     * So we get the file type directly with {@link Os#lstat(String)} if {@code followLinks} is
     * {@code false} and {@link Os#stat(String)} if {@code followLinks} is {@code true}. All exceptions
     * are assumed as non-existence.
     * <p>
     * The {@link org.apache.commons.io.FileUtils#isSymlink(File)} can also be used for checking
     * symlinks but {@link FileAttributes} will provide access to more attributes if necessary,
     * including getting other special file types considering that {@link File#exists()} can't be
     * used to reliably check for non-existence and exclude the other 3 file types. commons.io is
     * also not compatible with android < 8 for man<a href="y">things.
     * <p>
     * https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:libcore/ojluni/src/main/</a>java/jav<a href="a/io/File.java;l=793
     ">* https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:libcore/ojluni/src/main/java/java/</a>io/UnixF<a href="ileSystem.java;l=248
     ">* https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:libcore/ojluni/src/main/nati</a>ve/UnixF<a href="ileSystem_md.c;l=121
     ">* https://cs.android.com/android/_/android/platform/libcore/+/001ac51d61ad</a>7443ba51<a href="8bf2cf7e086efe698c6d
     ">* https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:libcore/luni/src/main/</a>java/lib<a href="core/io/Os.java;l=51
     ">* https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:libcore/luni/src/main/java/</a>libcore/<a href="io/Libcore.java;l=45
     ">* https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/ap</a>p/ActivityThread.java;l=7530
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
        if (filePath == null || filePath.isEmpty()) return FileType.NO_EXIST;

        try {
            FileAttributes fileAttributes = FileAttributes.get(filePath, followLinks);
            return getFileType(fileAttributes);
        } catch (Exception e) {
            return FileType.NO_EXIST;
        }
    }

    public static FileType getFileType(@NonNull final FileAttributes fileAttributes) {
        if (fileAttributes.isRegularFile())
            return FileType.REGULAR;
        else if (fileAttributes.isDirectory())
            return FileType.DIRECTORY;
        else if (fileAttributes.isSymbolicLink())
            return FileType.SYMLINK;
        else if (fileAttributes.isSocket())
            return FileType.SOCKET;
        else if (fileAttributes.isCharacter())
            return FileType.CHARACTER;
        else if (fileAttributes.isFifo())
            return FileType.FIFO;
        else if (fileAttributes.isBlock())
            return FileType.BLOCK;
        else
            return FileType.UNKNOWN;
    }

}
