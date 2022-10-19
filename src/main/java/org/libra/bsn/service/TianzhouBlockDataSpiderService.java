package org.libra.bsn.service;

import java.net.MalformedURLException;

/**
 * @author xianhu.wang
 * @date 2022年07月20日 11:51 上午
 */
public interface TianzhouBlockDataSpiderService {

    boolean spider(String url, Integer start, Integer end) throws MalformedURLException;

    boolean spiderByPagation();
}
