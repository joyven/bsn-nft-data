package org.libra.bsn.orm.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhoujunwen
 * @date 2022-09-2022/9/19 下午2:24
 **/
@Data
public class BaseEntity<T> implements Serializable {
    private Date gmtCreate;
    private Date gmtModify;
}
