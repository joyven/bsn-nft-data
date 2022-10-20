package org.libra.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author xianhu.wang
 * @date 2022年10月20日 5:49 下午
 */
@Data
@ToString
public class NFTInfo extends BaseEntity {
    /**
     * '藏品地址
     */
    private String nftUrl;
    /**
     * '藏品发行者'
     */
    private String nftPublisher;
    /**
     * '藏品发行数量'
     */
    private Integer nftNum;
    /**
     * '类别名称'
     */
    private String categoryName;
}
