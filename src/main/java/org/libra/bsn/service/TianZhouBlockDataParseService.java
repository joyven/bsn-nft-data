package org.libra.bsn.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author xianhu.wang
 * @date 2022年10月21日 8:22 上午
 */
public interface TianZhouBlockDataParseService {
    void parseNftTradingInfo(JSONObject tempObj);
}
