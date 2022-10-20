package org.libra.bsn.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xianhu.wang
 * @date 2022年10月20日 1:37 下午
 */
public class BSNConstants {

    public static final String TIANZHOU_USERNAME = "hmac-tianzhou";

    public static final String TIAN_ZHOU_AUTHORITY = "backend.tianzhou.wenchang.bianjie.ai";

    public static final String CODE="code";
    public static final String DATA="data";

    // 存储对象头信息
    public static  final Map<String,String> HEADERS = new HashMap<>();
    static {
        HEADERS.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
        HEADERS.put("origin", "https://tianzhou.wenchang.bianjie.ai");
        HEADERS.put("referer", "https://tianzhou.wenchang.bianjie.ai/");
        HEADERS.put("sec-ch-ua-mobile", "?0");
        HEADERS.put("sec-ch-ua-platform", "macOS");
        HEADERS.put("sec-fetch-dest", "empty");
        HEADERS.put("sec-fetch-mode", "cors");
        HEADERS.put("sec-fetch-site", "same-site");
        HEADERS.put("accept", "application/json, text/plain, */*");
        HEADERS.put("accept-encoding", "gzip, deflate, br");
        HEADERS.put("accept-language", "zh-CN,zh;q=0.9");
        HEADERS.put("Content-Type", "application/json");
        HEADERS.put(":method", "GET");
        HEADERS.put(":scheme", "https");
        HEADERS.put("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}
