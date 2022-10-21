package org.libra.bsn.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.libra.bsn.entity.NFTInfo;
import org.libra.bsn.entity.NFTTradingInfo;
import org.libra.bsn.service.TianZhouBlockDataParseService;
import org.libra.bsn.util.HeaderUtils;
import org.libra.bsn.util.HttpClientUtil;
import org.libra.bsn.util.ThreadPoolUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.libra.bsn.constant.BSNConstants.CODE;
import static org.libra.bsn.constant.BSNConstants.DATA;

/**
 * 天舟块数据解析
 *
 * @author xianhu.wang
 * @date 2022年10月21日 8:26 上午
 */
@Service("tianZhouBlockDataParseService")
public class TianZhouBlockDataParseServiceImpl implements TianZhouBlockDataParseService {
    @Override
    public void parseNftTradingInfo(JSONObject tempObj) {

        String txHash = tempObj.getString("tx_hash");

        Map<String, Object> parameter = new HashMap<String, Object>(4);

        Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/" + txHash + "",
                parameter, "get", "/nodejs/txs/" + txHash + "");

        String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/" + txHash + "", headers, parameter);

        if (!StringUtils.isNotBlank(response) || !response.contains(DATA)
                || !response.contains(CODE)) {
            // 进行下一个
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.containsKey(CODE) && jsonObject.getInteger(CODE) == 0) {
            JSONObject data = jsonObject.getJSONObject(DATA);
            if (MapUtils.isEmpty(data)) {
                // 进行下一个
                return;
            }
            // 提交解析列表入库任务
            parseOrigin(data);
        }
    }

    private void parseOrigin(JSONObject origin) {
        NFTInfo nftInfo = new NFTInfo();
        // 解析msgs
        JSONObject msgs = (JSONObject) origin.getJSONArray("msgs").get(0);
        nftInfo.setTradingType(msgs.getString("type"));
        JSONObject msg = msgs.getJSONObject("msg");
        nftInfo.setSender(msg.getString("sender"));
        nftInfo.setRecipient(msg.getString("recipient"));
        String nftName = msg.getString("nft_name");
        if (StringUtils.isBlank(nftName)) {
            nftName = msg.getString("name");
        }
        nftInfo.setNtfName(nftName);
        nftInfo.setNtfId(msg.getString("id"));
        nftInfo.setCategoryId(msgs.getString("denom"));
        nftInfo.setCategoryName(msg.getString("denom_name"));
        nftInfo.setChainLink(msg.getString("uri"));
        nftInfo.setChainData(msg.getString("data"));
        nftInfo.setChainLinkHash(msg.getString("uri_hash"));
        nftInfo.setTradingHash(origin.getString("tx_hash"));
        nftInfo.setOccurTime(origin.getInteger("time"));
        nftInfo.setHeight(origin.getInteger("height"));
        JSONArray eventsNew = origin.getJSONArray("events_new");
        nftInfo.setEvents(eventsNew.toJSONString());
        JSONObject fee = origin.getJSONObject("fee");
        nftInfo.setTradingStatus(origin.getInteger("status"));
        nftInfo.setRemark(origin.getString("memo"));
        JSONObject code = new JSONObject();
        code.put("events", origin.getJSONArray("events_new"));
        code.put("msgs", origin.getJSONArray("msgs"));
        nftInfo.setCode(code.toJSONString());
        nftInfo.setEnergyValue(fee.getInteger("gas"));
        nftInfo.setDataOrigin("WC-TZ");
        // nftInfo入库
        if ("transfer_nft".equals(msgs.getString("type"))) {
            NFTTradingInfo nftTradingInfo = new NFTTradingInfo();
            nftTradingInfo.setSender(msg.getString("sender"));
            nftTradingInfo.setRecipient(msg.getString("recipient"));
            nftTradingInfo.setNtfName(nftName);
            nftTradingInfo.setTradingHash(origin.getString("tx_hash"));
            nftTradingInfo.setTradingType(msgs.getString("type"));
            nftTradingInfo.setNtfId(msg.getString("id"));
            nftTradingInfo.setCategoryId(msgs.getString("denom"));
            nftTradingInfo.setHeight(origin.getInteger("height"));
            nftTradingInfo.setEnergyValue(fee.getInteger("gas"));
            nftTradingInfo.setDataOrigin("WC-TZ");
            nftTradingInfo.setAmount(getRandomRedPacketBetweenMinAndMax());

            // nftTradingInfo 入库
        }

    }

    /**
     * 获取金额
     *
     * @return
     */
    private static BigDecimal getRandomRedPacketBetweenMinAndMax() {
        BigDecimal min = new BigDecimal(1);
        BigDecimal max = new BigDecimal(10000);
        float minF = min.floatValue();
        float maxF = max.floatValue();
        //生成随机数
        BigDecimal db = new BigDecimal(Math.random() * (maxF - minF) + minF);

        //返回保留两位小数的随机数。不进行四舍五入
        return db.setScale(2, BigDecimal.ROUND_DOWN);
    }
}
