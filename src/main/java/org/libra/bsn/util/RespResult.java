package org.libra.bsn.util;

/**
 * @author xianhu.wang
 * @date 2022年10月14日 10:34 上午
 */
public class RespResult<T> {

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
