package org.libra.service.impl;

import org.libra.service.DataParseService;
import org.libra.util.HttpClientUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xianhu.wang
 * @date 2022年07月20日 11:52 上午
 */
@Service
public class DataParseServiceImpl implements DataParseService {

    @Override
    public boolean parseOriginSource(String url, Integer start, Integer end) {

        for (int i = start; i <= end; i++) {
            Map<String, Object> map = new HashMap<String, Object>(2);
            map.put("pageNum",i);
            map.put("pageSize",15);
            String sendGet = HttpClientUtil.sendGet(url, null, map);
        }
        return false;
    }
}
