package org.libra.bsn.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.libra.bsn.AppMainTest;
import org.libra.bsn.util.HeaderUtils;
import org.libra.bsn.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.net.MalformedURLException;
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

    @Test
    public void test() {
        String url = "https://backend.tianzhou.wenchang.bianjie.ai/nodejs/nfts";

        tianZhouBlockDataSpiderService.spider(url, 1, 1);
    }

    @Test
    public void test2() {

        Map<String, Object> parameter = new HashMap<String, Object>(2);
        parameter.put("denomId", "opf39123ca5db24838220");
        parameter.put("nftId", "avata6tmrdhvgz7ciss5yyg9fhh6yq7i");

        Map<String, String> headers = HeaderUtils.assemblyFinalHead("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/nfts/details", parameter, "get", "/nodejs/nfts/details?denomId=opf39123ca5db24838220&nftId=avata6tmrdhvgz7ciss5yyg9fhh6yq7i");

        String response = HttpClientUtil.sendGet("https://backend.tianzhou.wenchang.bianjie.ai/nodejs/nfts/details", headers, parameter);

        System.out.println(response);
    }
}
