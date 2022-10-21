package org.libra.bsn.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


/**
 * @author zhoujunwen
 * @date 2022-10-2022/10/18 下午1:30
 **/
public class SignUtils {

    public static byte[] signature(String url, String method, String date, Map<String, Object> query) throws MalformedURLException, NoSuchAlgorithmException, InvalidKeyException {
        String httpVersion = "HTTP/1.1";
        String s1 = "ylb";
        String s2 = "xlTxP";
        String path = url;
        if (url.startsWith("https")) {
            httpVersion = "HTTP/1.1";
            URL uri = new URL(url);
            path = uri.getPath();
            if (query != null && query.size() > 0) {
                boolean first = true;
                StringBuilder sb = new StringBuilder(path);
                for (String key : query.keySet()) {
                    char ch = '&';
                    if (first) {
                        ch = '?';
                        first = false;
                    }
                    String value = query.get(key).toString();
                    sb.append(ch).append(key).append("=").append(value);
                }
                path = sb.toString();
            }
        }

        String content = "x-date: " + date + "\n" + method.toUpperCase() + " " +
                path + " " + httpVersion;
        String key = s1 + s2;

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    public static String authorization(byte[] sig, String username) {
        String algorithm = "hmac-sha256";
        String sign = Base64Utils.encode(sig);
        return "hmac username=\"" + username + "\", algorithm=\""
                + algorithm + "\", headers=\"x-date request-line\", signature=\""
                + sign + "\"";

    }
}
