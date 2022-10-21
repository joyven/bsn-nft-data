package org.libra.bsn.service;

/**
 * @author xianhu.wang
 * @date 2022年07月20日 11:51 上午
 */
public interface WenChangDataSpiderService {

    boolean spider(String url, Integer start,Integer end);

    boolean spiderSourceBlock(Integer heightStart, Integer heightEnd);
}
