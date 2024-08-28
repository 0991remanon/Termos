package com.termux.shared.net.uri;

import android.net.Uri;

/**
 * The {@link Uri} schemes.
 * <p><a href="
 ">* https://www.iana.org/assignments/uri-schemes/uri-schemes.x</a>html<a href="
 ">* https://en.wikipedia.org/wiki/List_of_URI_sch</a>emes
 */
public class UriScheme {

    /** Android content provider. <a href="https://www.iana.org/assignments/uri-schemes/prov/content">...</a>. */
    public static final String SCHEME_CONTENT = "content";

    /** Filesystem or android app asset. <a href="https://www.rfc-editor.org/rfc/rfc8089.html">...</a>. */
    public static final String SCHEME_FILE = "file";

}
