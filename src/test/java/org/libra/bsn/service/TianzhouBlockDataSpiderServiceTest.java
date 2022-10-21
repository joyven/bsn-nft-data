package org.libra.bsn.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.libra.bsn.AppMainTest;
import org.libra.bsn.dao.NftInfoDao;
import org.libra.bsn.dao.NftTaringInfoDao;
import org.libra.bsn.entity.NFTInfo;
import org.libra.bsn.entity.NFTTradingInfo;
import org.libra.bsn.util.HeaderUtils;
import org.libra.bsn.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.libra.bsn.constant.BSNConstants.CODE;
import static org.libra.bsn.constant.BSNConstants.DATA;

/**
 * @author zhoujunwen
 * @date 2022-10-2022/10/19 下午12:06
 **/
public class TianzhouBlockDataSpiderServiceTest extends AppMainTest {

    @Autowired
    private WenChangDataSpiderService tianZhouBlockDataSpiderService;

    @Autowired
    private NftInfoDao nftInfoDao;
    @Autowired
    private NftTaringInfoDao nfttaringInfoDao;

    @Test
    public void  test4(){

        NFTInfo nftInfo = new NFTInfo();
        NFTTradingInfo nftTradingInfo = new NFTTradingInfo();
        nftTradingInfo.setNtfName("1");
//        nftInfo.setEvents("1");
//        nftInfo.setOccurTime(new Timestamp(1666312218*1000L));
//        nftInfoDao.insertNftInfo(nftInfo);
        nfttaringInfoDao.insertNftTradingInfo(nftTradingInfo);
    }
    @Test
    public void test() {
        String url = "https://backend.tianzhou.wenchang.bianjie.ai/nodejs/nfts";

        tianZhouBlockDataSpiderService.spider(url, 1, 1);
    }


    @Test
    public void test3() {

        tianZhouBlockDataSpiderService.spiderSourceBlock(2653307, 2653307);
    }

    @Test
    public void test2() {

//        Map<String, Object> parameter = new HashMap<String, Object>(2);
//        parameter.put("denomId", "opf39123ca5db24838220");
//        parameter.put("nftId", "avata6tmrdhvgz7ciss5yyg9fhh6yq7i");
//
//        Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/nfts/details", parameter, "get", "/nodejs/nfts/details?denomId=opf39123ca5db24838220&nftId=avata6tmrdhvgz7ciss5yyg9fhh6yq7i");
//
//        String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/nfts/details", headers, parameter);
        String txHash = "3E51B5B6A9DAC54B8723B73B5DA6E9261714C59DD702C15C5CDF96B3F3C7541D";

        Map<String, Object> parameter = new HashMap<String, Object>(4);

        Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/" + txHash + "",
                parameter, "get", "/nodejs/txs/" + txHash + "");

        String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs/" + txHash + "", headers, parameter);

        System.out.println(response);
    }


}
