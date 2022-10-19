package org.libra.bsn.service.impl;

//import org.apache.commons.lang3.time.DateFormatUtils;
import org.libra.bsn.service.TianzhouBlockDataSpiderService;
import org.libra.bsn.util.OkHttpUtils;
import org.libra.bsn.util.SignUtils;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author xianhu.wang
 * @date 2022年07月20日 11:52 上午
 */
@Service
public class TianzhouBlockDataSpiderServiceImpl implements TianzhouBlockDataSpiderService {
    private static final String TIANZHOU_USERNAME = "hmac-tianzhou";

    @Override
    public boolean spider(String url, Integer start, Integer end) throws MalformedURLException {

        for (int i = start; i <= end; i++) {
            Map<String, Object> map = new HashMap<String, Object>(2);
            map.put("pageNum", i);
            map.put("pageSize", 10);

//            String xDate = DateFormatUtils.format(new Date(), "EEE, d MMM yyyy HH:mm:ss z", TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
            String xDate = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));

            System.out.println("1111111111111");
            byte[] sign = new byte[0];
            try {
                sign = SignUtils.signature(url, "get", xDate, map);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
            }
            String auth = SignUtils.authorization(sign, TIANZHOU_USERNAME);
            Map<String, String> headers = new HashMap<>(24);
            headers.put("authorization", auth);
            headers.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
            headers.put("x-date", xDate);
            headers.put("origin", "https://tianzhou.wenchang.bianjie.ai");
            headers.put("referer", "https://tianzhou.wenchang.bianjie.ai/");
            headers.put("sec-ch-ua-mobile", "?0");
            headers.put("sec-ch-ua-platform", "macOS");
            headers.put("sec-fetch-dest", "empty");
            headers.put("sec-fetch-mode", "cors");
            headers.put("sec-fetch-site", "same-site");
            headers.put("accept", "application/json, text/plain, */*");
            headers.put("accept-encoding", "gzip, deflate, br");
            headers.put("accept-language", "zh-CN,zh;q=0.9");
            headers.put("Content-Type","application/json");
            headers.put(":authority", "backend.tianzhou.wenchang.bianjie.ai");
            headers.put(":method", "GET");
            headers.put(":path", "/nodejs/txs?pageNum="+ i + "&pageSize=10");
            headers.put(":scheme", "https");
            headers.put("Referrer-Policy", "strict-origin-when-cross-origin");

            OkHttpUtils okHttpUtils = OkHttpUtils.builder().url(url);
            headers.forEach((k, v) -> {
                okHttpUtils.addHeader(k, v);
//                System.out.println(">>>>>key=" + k + ", value=" + v);
            });
            map.forEach((k, v) -> {
                okHttpUtils.addParam(k, "" + v);
            });
            String s = okHttpUtils.get().sync();
            System.out.println(s);


        }
        return false;
    }

    @Override
    public boolean spiderByPagation() {
        return false;
    }

    public static void main(String[] args) throws MalformedURLException {
        String url = "https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs";

        TianzhouBlockDataSpiderServiceImpl dataParseService = new TianzhouBlockDataSpiderServiceImpl();
        dataParseService.spider(url, 1, 1);
    }
}
