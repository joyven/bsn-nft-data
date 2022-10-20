package org.libra.bsn.service.impl;

import org.libra.bsn.service.WenChangDataSpiderService;
import org.springframework.stereotype.Service;

/**
 * 文昌链-天和
 * @author xianhu.wang
 * @date 2022年10月20日 1:06 下午
 */
@Service("tianHeBlockDataSpiderService")
public class TianHeBlockDataSpiderServiceImpl implements WenChangDataSpiderService {
    @Override
    public boolean spider(String url, Integer start, Integer end) {
        return false;
    }
}
