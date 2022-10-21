package org.libra.bsn.entity;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * @author xianhu.wang
 * @date 2022年10月20日 5:38 下午
 */
@Data
@ToString
public class BaseEntity {

    private Integer id;
    /**
     * 类别标识
     */
    private String categoryId;
    /**
     * nft标识
     */
    private String ntfId;

    private Timestamp gmtModify;
    private Integer occurTime;
    private Timestamp gmtCreate;

}
