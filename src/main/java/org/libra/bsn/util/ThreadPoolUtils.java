package org.libra.bsn.util;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 *
 * @author xianhu.wang
 * @date 2022年10月20日 4:23 下午
 */
public class ThreadPoolUtils {


    /**
     * nft解析线程池
     */
    public static final ThreadPoolExecutor NFT_PARSE_POOL = new ThreadPoolExecutor(
            20, 20, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000),
            new CustomizableThreadFactory("NFT_PARSE_POOL"));


    /**
     * nft详情线程池
     */
    public static final ThreadPoolExecutor NFT_PARSE_INFO_POOL = new ThreadPoolExecutor(
            2, 2, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000),
            new CustomizableThreadFactory("NFT_INFO_POOL"));

}


