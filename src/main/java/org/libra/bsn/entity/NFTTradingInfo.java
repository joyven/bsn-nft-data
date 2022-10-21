package org.libra.bsn.entity;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author xianhu.wang
 * @date 2022年10月20日 5:54 下午
 */
@Data
@ToString
public class NFTTradingInfo extends BaseEntity {
    /**
     * 交易hash
     */
    private String tradingHash;

    /**
     * 交易类型
     */
    private String tradingType;
    /**
     * NFT名称
     */
    private String ntfName;

    /**
     * 交易额
     */
    private BigDecimal amount;
    /**
     * 区块高度
     */
    private Integer height;
    /**
     * 能量值
     */
    private Integer energyValue;

    /**
     * 发送者
     */
    private String sender;
    /**
     * 接收者
     */
    private String recipient;

    /**
     * 数据来源
     */
    private String dataOrigin;
}
