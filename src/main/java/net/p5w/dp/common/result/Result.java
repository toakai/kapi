package net.p5w.dp.common.result;

import lombok.Data;

/**
 * 统一 API 响应体
 * <p>
 * 所有接口返回值均包装为此对象，字段含义：
 * <ul>
 *   <li>requestId：请求唯一标识，由 {@link net.p5w.dp.interceptor.LogInterceptor} 生成，用于日志链路追踪</li>
 *   <li>code：业务状态码，见 {@link ResultCode}</li>
 *   <li>msg：状态描述</li>
 *   <li>data：业务数据，失败时为 null</li>
 * </ul>
 * </p>
 */
@Data
public class Result<T> {

    private String requestId;
    private Integer code;
    private String msg;
    private T data;

    // ===================== 成功 =====================

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        r.setData(data);
        return r;
    }

    // ===================== 失败（带 requestId） =====================

    public static <T> Result<T> fail(String requestId, int code, String msg) {
        Result<T> r = new Result<>();
        r.setRequestId(requestId);
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    // ===================== 失败（不带 requestId，由 GlobalResponseHandler 自动填充） =====================

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static <T> Result<T> fail(ResultCode code) {
        Result<T> r = new Result<>();
        r.setCode(code.getCode());
        r.setMsg(code.getMsg());
        return r;
    }
}
