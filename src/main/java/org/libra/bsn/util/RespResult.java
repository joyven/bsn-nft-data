package org.libra.bsn.util;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author xianhu.wang
 * @date 2022年10月14日 10:34 上午
 */
@Data
@ToString
public class RespResult<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    public RespResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> RespResult<T> success(Integer code, String message, T data) {
        return new RespResult<T>(200, "success", data);
    }

    public static <T> RespResult<T> success() {
        return new RespResult<T>(200, "success", null);
    }

    public static <T> RespResult<T> error(Integer code, String message) {
        return new RespResult<T>(code, message, null);
    }

    public static <T> RespResult<T> error(Integer code, String message, T data) {
        return new RespResult<T>(code, message, data);
    }


}
