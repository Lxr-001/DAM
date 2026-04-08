package org.jeecg.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PathDecoder {
    public static String decodePath(String encodedPath) throws UnsupportedEncodingException {
        return URLDecoder.decode(encodedPath, "UTF-8");
    }
}
