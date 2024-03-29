package org.libra.bsn.util;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.sql.Timestamp;
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
    public static final ThreadPoolExecutor NFT_SUBMIT_POOL = new ThreadPoolExecutor(
            5, 5, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000),
            new CustomizableThreadFactory("NFT_SUBMIT_POOL"));


    /**
     * nft图片下载
     */
    public static final ThreadPoolExecutor NFT_IMG_DOWNLOAD_POOL = new ThreadPoolExecutor(
            5, 5, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(5000),
            new CustomizableThreadFactory("NFT_IMG_DOWNLOAD_POOL"));
}


