package org.libra.bsn.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.libra.bsn.constant.BSNConstants;
import org.libra.bsn.dao.NftInfoDao;
import org.libra.bsn.dao.NftTaringInfoDao;
import org.libra.bsn.entity.NFTInfo;
import org.libra.bsn.entity.NFTTradingInfo;
import org.libra.bsn.service.TianZhouBlockDataParseService;
import org.libra.bsn.util.HeaderUtils;
import org.libra.bsn.util.HttpClientUtil;
import org.libra.bsn.util.ImageHttpClientUtils;
import org.libra.bsn.util.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

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

    private static final Logger logger = LoggerFactory.getLogger(TianZhouBlockDataParseServiceImpl.class);

    @Autowired
    private NftInfoDao nftInfoDao;

    @Autowired
    private NftTaringInfoDao nfttaringInfoDao;

    @Override
    public void parseNftTradingInfo(JSONObject tempObj) {

        String txHash = tempObj.getString(BSNConstants.TX_HASH);

        Map<String, Object> parameter = new HashMap<String, Object>(4);

        Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/" + txHash + "",
                parameter, "get", "/nodejs/txs/" + txHash + "");

        String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/" + txHash + "", headers, parameter);

        if (!StringUtils.isNotBlank(response) || !response.contains(DATA)
                || !response.contains(CODE)) {
            // 进行下一个
            logger.warn("当前txHash={}数据为空 dump1", txHash);
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.containsKey(CODE) && jsonObject.getInteger(CODE) == 0) {
            JSONObject data = jsonObject.getJSONObject(DATA);
            if (MapUtils.isEmpty(data)) {
                // 进行下一个
                logger.warn("当前txHash={}数据为空 dump2", txHash);
                return;
            }
            // 提交解析列表入库任务
            parseOrigin(data);
        }
    }

    private void parseOrigin(JSONObject origin) {
        NFTInfo nftInfo = new NFTInfo();
        // 解析msgs
        JSONObject msgs = (JSONObject) origin.getJSONArray(BSNConstants.MSGS).get(0);
        nftInfo.setTradingType(msgs.getString(BSNConstants.TYPE));
        nftInfo.setTradingHash(origin.getString(BSNConstants.TX_HASH));
        JSONObject msg = msgs.getJSONObject(BSNConstants.MSG);
        nftInfo.setSender(msg.getString(BSNConstants.SENDER));
        nftInfo.setRecipient(msg.getString(BSNConstants.RECIPIENT));
        String nftName = msg.getString(BSNConstants.NFT_NAME);
        if (StringUtils.isBlank(nftName)) {
            nftName = msg.getString(BSNConstants.NAME);
        }
        nftInfo.setNtfName(nftName);
        nftInfo.setNtfId(msg.getString(BSNConstants.ID));
        nftInfo.setCategoryId(msg.getString(BSNConstants.DENOM));
        nftInfo.setCategoryName(msg.getString(BSNConstants.DENOM_NAME));
        String url = msg.getString(BSNConstants.URI);

        downloadImg(url, origin.getString(BSNConstants.TX_HASH));

        nftInfo.setChainLink(url);
        nftInfo.setChainData(msg.getString(BSNConstants.DATA));
        nftInfo.setChainLinkHash(msg.getString(BSNConstants.URI_HASH));
        nftInfo.setOccurTime(new Timestamp(origin.getInteger(BSNConstants.TIME) * 1000L));
        nftInfo.setHeight(origin.getInteger(BSNConstants.HEIGHT));
        JSONArray eventsNew = origin.getJSONArray(BSNConstants.EVENTS_NEW);
        nftInfo.setEvents(eventsNew.toJSONString());
        JSONObject fee = origin.getJSONObject(BSNConstants.FEE);
        nftInfo.setTradingStatus(origin.getInteger(BSNConstants.STATUS));
        nftInfo.setRemark(origin.getString(BSNConstants.MEMO));
        JSONObject code = new JSONObject();
        code.put(BSNConstants.EVENTS, origin.getJSONArray(BSNConstants.EVENTS_NEW));
        code.put(BSNConstants.MSGS, origin.getJSONArray(BSNConstants.MSGS));
        nftInfo.setCode(code.toJSONString());
        nftInfo.setEnergyValue(fee.getInteger(BSNConstants.GAS));
        nftInfo.setDataOrigin(BSNConstants.WC_TZ);
        // nftInfo入库
        nftInfoDao.insertNftInfo(nftInfo);

        if (BSNConstants.TRANSFER_NFT.equals(msgs.getString(BSNConstants.TYPE))) {
            NFTTradingInfo nftTradingInfo = new NFTTradingInfo();
            nftTradingInfo.setSender(msg.getString(BSNConstants.SENDER));
            nftTradingInfo.setRecipient(msg.getString(BSNConstants.RECIPIENT));
            nftTradingInfo.setNtfName(nftName);
            nftTradingInfo.setTradingHash(origin.getString(BSNConstants.TX_HASH));
            nftTradingInfo.setTradingType(msgs.getString(BSNConstants.TYPE));
            nftTradingInfo.setNtfId(msg.getString(BSNConstants.ID));
            nftTradingInfo.setCategoryId(msg.getString(BSNConstants.DENOM));
            nftTradingInfo.setHeight(origin.getInteger(BSNConstants.HEIGHT));
            nftTradingInfo.setEnergyValue(fee.getInteger(BSNConstants.GAS));
            nftTradingInfo.setOccurTime(new Timestamp(origin.getInteger(BSNConstants.TIME) * 1000L));
            nftTradingInfo.setDataOrigin(BSNConstants.WC_TZ);
            nftTradingInfo.setAmount(getRandomRedPacketBetweenMinAndMax());
            // nftTradingInfo 入库
            nfttaringInfoDao.insertNftTradingInfo(nftTradingInfo);
        }

        logger.info("当前txHash={},处理完成", origin.getString(BSNConstants.TX_HASH));
    }

    private void downloadImg(String url, String txHash) {
        // 下载图像
        if (StringUtils.isNotBlank(url)) {
            ThreadPoolUtils.NFT_IMG_DOWNLOAD_POOL.submit(() -> ImageHttpClientUtils.imageDownload(txHash, url, 1500));
        }
    }

    /**
     * 获取金额
     *
     * @return
     */
    private static BigDecimal getRandomRedPacketBetweenMinAndMax() {
        BigDecimal min = new BigDecimal(1);
        BigDecimal max = new BigDecimal(1000);
        float minF = min.floatValue();
        float maxF = max.floatValue();
        //生成随机数
        BigDecimal db = new BigDecimal(Math.random() * (maxF - minF) + minF);

        //返回保留两位小数的随机数。不进行四舍五入
        return db.setScale(2, BigDecimal.ROUND_DOWN);
    }
}
