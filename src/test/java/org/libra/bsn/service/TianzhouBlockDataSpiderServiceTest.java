package org.libra.bsn.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.MalformedURLException;

/**
 * @author zhoujunwen
 * @date 2022-10-2022/10/19 下午12:06
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class TianzhouBlockDataSpiderServiceTest {
    @Resource
    private TianzhouBlockDataSpiderService tianzhouBlockDataSpiderService;
    @Test
    public void test() {
        String url = "https://backend.tianzhou.wenchang.bianjie.ai/nodejs/txs";

        try {
            tianzhouBlockDataSpiderService.spider(url, 1, 1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
