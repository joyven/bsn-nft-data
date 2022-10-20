//package org.libra.bsn.orm.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//
//import java.io.Serializable;
//
//import org.libra.bsn.orm.entity.BaseEntity;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Getter;
//import lombok.Setter;
//
///**
// * <p>
// *
// * </p>
// *
// * @author zhoujunwen
// * @since 2022-10-19 11:26:08
// */
//@Getter
//@Setter
//@TableName("bsn_txs_data")
//@ApiModel(value = "BsnTxsDataEntity对象", description = "")
//public class BsnTxsDataEntity extends BaseEntity {
//
//    private static final long serialVersionUID = 1L;
//
//    @ApiModelProperty("主键")
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;
//
//    @ApiModelProperty("上链时间")
//    @TableField("`time`")
//    private Integer time;
//
//    @ApiModelProperty("区块高度")
//    @TableField("height")
//    private Long height;
//
//    @ApiModelProperty("交易哈希")
//    @TableField("tx_hash")
//    private String txHash;
//
//    @ApiModelProperty("状态")
//    @TableField("`status`")
//    private String status;
//
//    @ApiModelProperty("类型")
//    @TableField("`type`")
//    private String type;
//
//    @ApiModelProperty("签名")
//    @TableField("signers")
//    private String signers;
//
//    @ApiModelProperty("源码")
//    @TableField("msgs")
//    private String msgs;
//
//    @ApiModelProperty("费用")
//    @TableField("fee")
//    private String fee;
//
//    @TableField("monikers")
//    private String monikers;
//
//    @ApiModelProperty("地址")
//    @TableField("addrs")
//    private String addrs;
//
//    @ApiModelProperty("合约地址")
//    @TableField("contract_addrs")
//    private String contractAddrs;
//
//
//}
