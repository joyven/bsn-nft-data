package org.libra.bsn.dao;


import org.apache.ibatis.annotations.Param;
import org.libra.bsn.entity.NFTInfo;

import java.util.List;

/**
 * @author xianhu.wang
 * @date 2022年10月14日 10:32 上午
 */
public interface NftInfoDao {

    void insertNftInfo(NFTInfo nftInfo);

    void updateNftInfo(@Param("txHash") String txHash,
                       @Param("featureValue") String featureValue);

    List<String> selectData();

    void delete(Integer id);

    Integer select(String txhash);
}
