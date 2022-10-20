package org.libra.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.ToString;

/**
 * @author xianhu.wang
 * @date 2022年10月20日 5:45 下午
 */
@Data
@ToString
public class NFTList extends BaseEntity {

    /**
     * 拥有者
     */
    private String owner;

    /**
     * '类别名称'
     */
    private String categoryName;
    /**
     * nft名称
     */
    private String nftName;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
