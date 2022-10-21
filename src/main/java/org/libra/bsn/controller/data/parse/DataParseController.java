package org.libra.bsn.controller.data.parse;

import org.libra.bsn.service.WenChangDataSpiderService;
import org.libra.bsn.util.RespResult;
import org.libra.bsn.util.ThreadPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xianhu.wang
 * @date 2022年07月20日 11:38 上午
 */
@RestController
public class DataParseController {

    @Autowired
    private WenChangDataSpiderService tianZhouBlockDataSpiderService;

    /**
     * @param url   抓去url
     * @param start 开始
     * @param end   结束
     * @return
     */
    @RequestMapping("/get/origin/source")
    public RespResult<String> getOriginSource(@RequestParam("url") String url,
                                              @RequestParam("start") Integer start,
                                              @RequestParam("end") Integer end) {

        boolean flag = tianZhouBlockDataSpiderService.spider(url, start, end);

        return RespResult.success();
    }

    /**
     * 指定抓取天舟块数据
     *
     * @param heightStart 开始
     * @param heightEnd   结束
     * @return
     */
    @RequestMapping("/spider/source")
    public RespResult spiderSourceTZBlock(@RequestParam("heightStart") Integer heightStart,
                                                  @RequestParam("heightEnd") Integer heightEnd) {

        ThreadPoolUtils.NFT_SUBMIT_POOL.submit(() -> {
            tianZhouBlockDataSpiderService.spiderSourceBlock(heightStart, heightEnd);
        });

        return RespResult.success();
    }
}
