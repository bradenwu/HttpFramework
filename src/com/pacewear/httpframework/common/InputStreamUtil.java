
package com.pacewear.httpframework.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class InputStreamUtil {
    /**
     * @param inputStream
     * @param bufferSize
     * @return
     * @throws Exception
     */
    public static String inputStream2String(InputStream inputStream, int bufferSize)
            throws Exception {

        if (inputStream == null || bufferSize < 1) {
            return null;
        }
        int i = -1;
        byte[] b = new byte[bufferSize];
        StringBuffer sb = new StringBuffer();
        while ((i = inputStream.read(b)) != -1) {
            sb.append(new String(b, 0, i));
        }
        return sb.toString();

    }

    /**
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static String inputStream2String(InputStream inputStream) throws Exception {

        if (inputStream == null) {
            return null;
        }
        int i = -1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((i = inputStream.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();

    }

    /**
     * @param string
     * @return
     */
    public static InputStream string2InputStream(String string) {
        if (string == null) {
            return null;
        }
        return new ByteArrayInputStream(string.getBytes());
    }
}
