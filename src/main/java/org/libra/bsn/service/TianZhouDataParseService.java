package org.libra.bsn.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 天舟数据解析
 * @author xianhu.wang
 * @date 2022年10月20日 4:38 下午
 */
public interface TianZhouDataParseService {

    void parseNftList(JSONObject jsonObject);

    void parseNftInfo(JSONObject jsonObject);

    void parseNftTrading(JSONObject jsonObject);
}
