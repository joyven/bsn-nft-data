package org.libra.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author zhoujunwen
 * @date 2022-10-2022/10/18 下午3:22
 **/
public class Base64Utils {
    private static Charset UTF_8 = StandardCharsets.UTF_8;

    public static String encode(String rawString){
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encoded = encoder.encode(rawString.getBytes(UTF_8));
        return new String(encoded, UTF_8);
    }

    public static String encode(byte[] bytes){
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encoded = encoder.encode(bytes);
        return new String(encoded, UTF_8);
    }

    public static String decode(String encodeString){
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decoded = decoder.decode(encodeString.getBytes(UTF_8));
        return new String(decoded, UTF_8);
    }
}
