package org.libra.bsn.entity;

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
     * 交易hash
     */
    private String tradingHash;
    /**
     * 交易状态 1成功 0失败
     */
    private Integer tradingStatus;
    /**
     * 区块高度
     */
    private Integer height;
    /**
     * 备注
     */
    private String remark;
    /**
     * 能量值
     */
    private Integer energyValue;
    /**
     * 交易类型
     */
    private String tradingType;
    /**
     * 类别名称
     */
    private String categoryName;
    /**
     * NFT名称
     */
    private String ntfName;
    /**
     * 发送者
     */
    private String sender;
    /**
     * 接收者
     */
    private String recipient;

    /**
     * 源码
     */
    private String code;

    /**
     * '链上link'
     */
    private String chainLink;

    /**
     * link hash
     */
    private String chainLinkHash;

    /**
     * '链上数据'
     */
    private String chainData;

    /**
     * '数据源'
     */
    private String dataOrigin;

    /**
     * '事件'
     */
    private String events;

    /**
     * 图片hash
     */
    private String imgHash;

}
