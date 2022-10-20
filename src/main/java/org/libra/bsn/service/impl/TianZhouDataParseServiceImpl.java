package org.libra.bsn.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.libra.bsn.service.TianZhouDataParseService;
import org.libra.bsn.service.WenChangDataSpiderService;
import org.libra.bsn.util.HeaderUtils;
import org.libra.bsn.util.HttpClientUtil;
import org.libra.bsn.util.ThreadPoolUtils;
import org.libra.entity.NFTInfo;
import org.libra.entity.NFTList;
import org.libra.entity.NFTTrading;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.libra.bsn.constant.BSNConstants.CODE;
import static org.libra.bsn.constant.BSNConstants.DATA;

/**
 * @author xianhu.wang
 * @date 2022年10月20日 4:41 下午
 */
@Service
public class TianZhouDataParseServiceImpl implements TianZhouDataParseService {


    @Override
    public void parseNftList(JSONObject jsonObject) {
        // 解析入库
        NFTList nftList = new NFTList();
        nftList.setNftName(jsonObject.getString("nft_name"));
        nftList.setOwner(jsonObject.getString("owner"));
        nftList.setCategoryId(jsonObject.getString("denom_id"));
        nftList.setCategoryName(jsonObject.getString("denom_name"));
        nftList.setNtfType(jsonObject.getString("nft_id"));
        nftList.setOccurTime(jsonObject.getInteger("create_time"));

        System.out.println(nftList);
    }

    @Override
    public void parseNftInfo(JSONObject jsonObject) {
        // 根据列表中nft类别名称&nft名称查询一次
        NFTInfo nftInfo = new NFTInfo();
        nftInfo.setCategoryId(jsonObject.getString("denom_id"));
        nftInfo.setCategoryName(jsonObject.getString("denom_name"));
        nftInfo.setOccurTime(jsonObject.getInteger("create_time"));
        nftInfo.setNtfType(jsonObject.getString("nft_id"));

        // 组装NftInfo
        CompletableFuture<Void> completableFuture = assemblyNftInfo(jsonObject, nftInfo);

        CompletableFuture<Void> completableFuture1 = assemblyNftUrl(jsonObject, nftInfo);
        // 等待所有任务完成后处理入库
        CompletableFuture.allOf(completableFuture1, completableFuture).join();

        System.out.println(nftInfo);

    }

    private CompletableFuture<Void> assemblyNftUrl(JSONObject jsonObject, NFTInfo nftInfo) {

        return CompletableFuture.runAsync(
                () -> {
                    String nftId = jsonObject.getString("nft_id");
                    String denomId = jsonObject.getString("denom_id");
                    Map<String, Object> parameter = new HashMap<String, Object>(2);
                    parameter.put("denomId", denomId);
                    parameter.put("nftId", nftId);

                    Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/nfts/details", parameter, "get", "/nodejs/nfts/details?denomId=" + denomId + "&nftId=" + nftId + "");
                    //发送http
                    String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/nfts/details", headers, parameter);
                    if (!StringUtils.isNotBlank(response) || !response.contains(DATA) || !response.contains(CODE)) {
                        return;
                    }
                    JSONObject parseObject = JSONObject.parseObject(response);
                    if (parseObject.containsKey(CODE) && parseObject.getInteger(CODE) == 0) {
                        JSONObject data = parseObject.getJSONObject(DATA);
                        if (data != null && data.containsKey(DATA)) {
                            JSONArray jsonArray = data.getJSONArray(DATA);
                            String tokenUri = jsonArray.getJSONObject(0).getString("tokenUri");
                            if (StringUtils.isBlank(tokenUri)) {
                                tokenUri = jsonArray.getJSONObject(0).getString("tokenData");
                            }
                            nftInfo.setNftUrl(tokenUri);
                        }
                    }
                }, ThreadPoolUtils.NFT_PARSE_INFO_POOL
        );
    }

    private CompletableFuture<Void> assemblyNftInfo(JSONObject jsonObject, NFTInfo nftInfo) {

        return CompletableFuture.runAsync(
                () -> {
                    String denomId = jsonObject.getString("denom_id");
                    Map<String, Object> parameter = new HashMap<String, Object>(2);
                    parameter.put("pageNum", 1);
                    parameter.put("pageSize", 10);
                    parameter.put("denomNameOrId", denomId);
                    Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/denoms", parameter, "get", "/nodejs/denoms?pageNum=1&pageSize=10&denomNameOrId=" + denomId + "");
                    //发送http
                    String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/denoms", headers, parameter);
                    if (!StringUtils.isNotBlank(response) || !response.contains(DATA) || !response.contains(CODE)) {
                        return;
                    }
                    JSONObject parseObject = JSONObject.parseObject(response);
                    if (parseObject.containsKey(CODE) && parseObject.getInteger(CODE) == 0) {
                        JSONObject data = parseObject.getJSONObject(DATA);
                        if (data != null && data.containsKey(DATA)) {
                            JSONArray jsonArray = data.getJSONArray(DATA);
                            nftInfo.setNftNum(jsonArray.getJSONObject(0).getInteger("nftCount"));
                            nftInfo.setNftPublisher(jsonArray.getJSONObject(0).getString("sender"));
                        }
                    }
                }, ThreadPoolUtils.NFT_PARSE_INFO_POOL
        );
    }

    @Override
    public void parseNftTrading(JSONObject jsonObject) {
        int i = 1;
        while (true) {
            String nftId = jsonObject.getString("nft_id");
            String denomId = jsonObject.getString("denom_id");
            Map<String, Object> parameter = new HashMap<String, Object>(4);
            parameter.put("tokenId", nftId);
            parameter.put("denomId", denomId);
            parameter.put("pageNum", i);
            parameter.put("pageSize", 10);

            Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/nfts", parameter, "get", "/nodejs/txs/nfts?tokenId=" + nftId + "&denomId=" + denomId + "&pageNum=" + i + "&pageSize=10");
            //发送http
            String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/nfts", headers, parameter);
            if (!StringUtils.isNotBlank(response) || !response.contains(DATA) || !response.contains(CODE)) {
                break;
            }
            JSONObject parseObject = JSONObject.parseObject(response);
            if (parseObject.containsKey(CODE) && parseObject.getInteger(CODE) == 0) {
                JSONObject data = parseObject.getJSONObject(DATA);
                if (data != null && data.containsKey(DATA)) {
                    JSONArray jsonArray = data.getJSONArray(DATA);
                    for (Object o : jsonArray) {
                        JSONObject object = JSONObject.parseObject(o.toString());
                        // 处理交易信息
                        dealTradingInfo(object);
                    }
                }
            }
            i++;
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void dealTradingInfo(JSONObject object) {
        NFTTrading nftTrading = new NFTTrading();

        nftTrading.setTradingHash(object.getString("tx_hash"));
        nftTrading.setTradingType(object.getString("type"));

        nftTrading.setTradingSender(object.getJSONArray("signers").getString(0));
        nftTrading.setOccurTime(object.getInteger("time"));
        nftTrading.setHeight(object.getString("height"));
        nftTrading.setEnergyValue(object.getIntValue("fee"));

    }
}
