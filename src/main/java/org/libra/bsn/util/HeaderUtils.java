package org.libra.bsn.util;

import org.libra.bsn.service.impl.TianZhouBlockDataSpiderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.libra.bsn.constant.BSNConstants.*;

/**
 * @author xianhu.wang
 * @date 2022年10月20日 5:30 下午
 */
public class HeaderUtils {

    private static final Logger logger = LoggerFactory.getLogger(HeaderUtils.class);

    /**
     * 组装最终header
     *
     * @param url
     * @param parameter
     * @param method
     * @param path
     * @return
     */
    public static Map<String, String> assemblyFinalHead(String url, Map<String, Object> parameter,
                                                  String method, String path) {
        Map<String, String> fixedHeaders = HEADERS;
        fixedHeaders.put(":authority", TIAN_ZHOU_AUTHORITY);
        String xDate = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
        byte[] sign = new byte[0];
        try {
            sign = SignUtils.signature(url, method, xDate, parameter);
        } catch (Exception e) {
            logger.error("get sign appear error={}", e.getMessage(), e);
        }
        String auth = SignUtils.authorization(sign, TIANZHOU_USERNAME);
        fixedHeaders.put("authorization", auth);
        fixedHeaders.put("x-date", xDate);
        fixedHeaders.put(":path", path);

        return fixedHeaders;
    }
}
