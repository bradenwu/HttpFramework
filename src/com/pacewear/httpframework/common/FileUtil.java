
package com.pacewear.httpframework.common;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileUtil {
    public static String getFileExtensionFromUrl(String url) {
        // this method do extract extension name from url
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename = 0 <= filenamePos ? url.substring(filenamePos + 1)
                    : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty()
                    && Pattern
                            .matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return "";
    }

    public static File createFile(String target) {
        File targetFile = new File(target);
        if (!(targetFile.exists())) {
            File dir = targetFile.getParentFile();
            if ((dir.exists()) || (dir.mkdirs())) {
                try {
                    targetFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    targetFile = null;
                }
            }
        }
        return targetFile;
    }
}
