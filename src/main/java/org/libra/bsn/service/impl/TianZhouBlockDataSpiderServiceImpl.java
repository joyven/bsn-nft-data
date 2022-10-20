package org.libra.bsn.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.libra.bsn.service.TianZhouDataParseService;
import org.libra.bsn.service.WenChangDataSpiderService;
import org.libra.bsn.util.HeaderUtils;
import org.libra.bsn.util.HttpClientUtil;
import org.libra.bsn.util.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.libra.bsn.constant.BSNConstants.CODE;
import static org.libra.bsn.constant.BSNConstants.DATA;

/**
 * 文昌链-天舟
 *
 * @author xianhu.wang
 * @date 2022年07月20日 11:52 上午
 */
@Service("tianZhouBlockDataSpiderService")
public class TianZhouBlockDataSpiderServiceImpl implements WenChangDataSpiderService {
    private static final Logger logger = LoggerFactory.getLogger(TianZhouBlockDataSpiderServiceImpl.class);

    @Autowired
    private TianZhouDataParseService tianZhouDataParseService;


    @Override
    public boolean spider(String url, Integer start, Integer end) {

        for (int i = start; i <= end; i++) {
            Map<String, Object> parameter = new HashMap<String, Object>(2);
            parameter.put("denomId", "");
            parameter.put("nftId", "");
            parameter.put("owner", "");
            parameter.put("pageNum", i);
            parameter.put("pageSize", 15);

            Map<String, String> headers = HeaderUtils.assemblyFinalHead(url, parameter, "get", "/nodejs/nfts?denomId=&nftId=&owner=&pageNum=" + i + "&pageSize=" + 15 + "");

            String response = HttpClientUtil.sendGet(url, headers, parameter);

            if (!StringUtils.isNotBlank(response) || !response.contains(DATA) || !response.contains(CODE)) {
                continue;
            }
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject.containsKey(CODE) && jsonObject.getInteger(CODE) == 0) {
                JSONObject data = jsonObject.getJSONObject(DATA);
                if (data != null && data.containsKey(DATA)) {
                    JSONArray jsonArray = data.getJSONArray(DATA);
                    for (Object object:jsonArray){
                        JSONObject tempObj = JSONObject.parseObject(object.toString());
                        // 提交解析列表入库任务
                        tianZhouDataParseService.parseNftList(tempObj);
                        ThreadPoolUtils.NFT_PARSE_POOL.submit(() -> tianZhouDataParseService.parseNftList(tempObj));
                        // 藏品信息 需要根据列表中nft类别名称&nft名称查询一次
                        ThreadPoolUtils.NFT_PARSE_POOL.submit(() -> tianZhouDataParseService.parseNftInfo(tempObj));
                        // nft交易记录 根据nft标识查询
                        ThreadPoolUtils.NFT_PARSE_POOL.submit(() -> tianZhouDataParseService.parseNftTrading(tempObj));
                    }
                }
            }
            try {
                // 每次休眠10s
                TimeUnit.MILLISECONDS.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return true;
    }
}
