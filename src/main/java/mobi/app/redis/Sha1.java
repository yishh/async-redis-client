package mobi.app.redis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.MessageDigest.getInstance;

/**
 * User: thor
 * Date: 12-12-28
 * Time: 下午1:31
 */
public class Sha1 {
    static MessageDigest md;
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    static {
        try {
            md = getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String sha1(String str) {
        return byteToString(md.digest(str.getBytes()));
    }

    public static String byteToString(byte[] digest) {
        int len = digest.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (byte aDigest : digest) {
            buf.append(HEX_DIGITS[(aDigest >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[aDigest & 0x0f]);
        }
        return buf.toString();

//        String str = "";
//        String tempStr = "";
//
//        for (int i = 1; i < digest.length; i++) {
//            tempStr = (Integer.toHexString(digest[i] & 0xff));
//            if (tempStr.length() == 1) {
//                str = str + "0" + tempStr;
//            } else {
//                str = str + tempStr;
//            }
//        }
//        return str.toLowerCase();
    }
}
