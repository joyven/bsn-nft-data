package org.libra.bsn.constant;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.libra.bsn.entity.NFTTradingInfo;

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
    public static final String MSGS="msgs";
    public static final String TYPE="type";
    public static final String MSG="msg";
    public static final String SENDER="sender";
    public static final String RECIPIENT="recipient";
    public static final String NAME="name";
    public static final String NFT_NAME="nft_name";
    public static final String ID="id";
    public static final String DENOM="denom";
    public static final String DENOM_NAME="denom_name";
    public static final String URI="uri";
    public static final String URI_HASH="uri_hash";
    public static final String TX_HASH="tx_hash";
    public static final String TIME="time";
    public static final String HEIGHT="height";
    public static final String EVENTS_NEW="events_new";
    public static final String FEE="fee";
    public static final String MEMO="memo";
    public static final String STATUS="status";
    public static final String GAS="gas";
    public static final String WC_TZ="WC-TZ";
    public static final String TRANSFER_NFT="transfer_nft";
    public static final String EVENTS="events";

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
