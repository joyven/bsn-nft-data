package org.libra.bsn.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.libra.bsn.service.TianZhouBlockDataParseService;
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

import static org.libra.bsn.constant.BSNConstants.*;

/**
 * 文昌链-天舟
 *
 * @author xianhu.wang
 * @date 2022年07月20日 11:52 上午
 */
@Service("tianZhouBlockDataSpiderService")
public class TianZhouBlockDataSpiderServiceImpl implements WenChangDataSpiderService {
    private static final Logger logger = LoggerFactory.getLogger(TianZhouBlockDataSpiderServiceImpl.class);

//    @Autowired
//    private TianZhouDataParseService tianZhouDataParseService;

    @Autowired
    private TianZhouBlockDataParseService tianZhouBlockDataParseService;


    @Override
    public boolean spiderSourceBlock(Integer heightStart, Integer heightEnd) {

        for (int i = heightStart; i <= heightEnd; i++) {
            // 获取一个高度内所有数据
            int pageNum = 1;
            while (true) {
                Map<String, Object> parameter = new HashMap<String, Object>(4);
                parameter.put("pageNum", pageNum);
                parameter.put("pageSize", 10);
                parameter.put("height", i);
                Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/blocks",
                        parameter, "get", "/nodejs/txs/blocks?pageNum=" + pageNum + "&pageSize=10&height=" + i + "");

                String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/blocks", headers, parameter);

                if (!StringUtils.isNotBlank(response) || !response.contains(DATA)
                        || !response.contains(CODE)) {
                    // 进行下一个高度
                    logger.warn("height={} not find data", i);
                    break;
                }
                JSONObject jsonObject = JSONObject.parseObject(response);
                if (jsonObject.containsKey(CODE) && jsonObject.getInteger(CODE) == 0) {
                    JSONObject data = jsonObject.getJSONObject(DATA);
                    if (data == null || !data.containsKey(DATA) || data.getJSONArray(DATA).size() == 0) {
                        // 进行下一个高度
                        logger.warn("height={} find data is end", i);
                        break;
                    }
                    JSONArray jsonArray = data.getJSONArray(DATA);
                    for (Object object : jsonArray) {
                        JSONObject tempObj = JSONObject.parseObject(object.toString());
                        // 提交解析列表入库任务
                        ThreadPoolUtils.NFT_PARSE_POOL.submit(() -> tianZhouBlockDataParseService.parseNftTradingInfo(tempObj));

                    }
                }
                try {
                    // 每次休眠10s
                    TimeUnit.MILLISECONDS.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pageNum++;
            }
        }


        return false;
    }


    @Override
    public boolean spider(String url, Integer start, Integer end) {
//
//        for (int i = start; i <= end; i++) {
//            Map<String, Object> parameter = new HashMap<String, Object>(2);
//            parameter.put("denomId", "");
//            parameter.put("nftId", "");
//            parameter.put("owner", "");
//            parameter.put("pageNum", i);
//            parameter.put("pageSize", 15);
//
//            Map<String, String> headers = HeaderUtils.assemblyFinalHead(url, parameter, "get", "/nodejs/nfts?denomId=&nftId=&owner=&pageNum=" + i + "&pageSize=" + 15 + "");
//
//            String response = HttpClientUtil.sendGet(url, headers, parameter);
//
//            if (!StringUtils.isNotBlank(response) || !response.contains(DATA) || !response.contains(CODE)) {
//                continue;
//            }
//            JSONObject jsonObject = JSONObject.parseObject(response);
//            if (jsonObject.containsKey(CODE) && jsonObject.getInteger(CODE) == 0) {
//                JSONObject data = jsonObject.getJSONObject(DATA);
//                if (data != null && data.containsKey(DATA)) {
//                    JSONArray jsonArray = data.getJSONArray(DATA);
//                    for (Object object:jsonArray){
//                        JSONObject tempObj = JSONObject.parseObject(object.toString());
//                        // 提交解析列表入库任务
//                        tianZhouDataParseService.parseNftList(tempObj);
//                        ThreadPoolUtils.NFT_PARSE_POOL.submit(() -> tianZhouDataParseService.parseNftList(tempObj));
//                        // 藏品信息 需要根据列表中nft类别名称&nft名称查询一次
//                        ThreadPoolUtils.NFT_PARSE_POOL.submit(() -> tianZhouDataParseService.parseNftInfo(tempObj));
//                        // nft交易记录 根据nft标识查询
//                        ThreadPoolUtils.NFT_PARSE_POOL.submit(() -> tianZhouDataParseService.parseNftTrading(tempObj));
//                    }
//                }
//            }
//            try {
//                // 每次休眠10s
//                TimeUnit.MILLISECONDS.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
        return true;
    }
}
