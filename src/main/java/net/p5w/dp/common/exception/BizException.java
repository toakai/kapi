package net.p5w.dp.common.exception;

import lombok.Getter;
import net.p5w.dp.common.result.ResultCode;

/**
 * 业务异常
 * <p>
 * 用于主动抛出可预期的业务错误（如鉴权失败、资源不存在等）。
 * 由 {@link GlobalExceptionHandler} 统一捕获并返回对应 HTTP 响应码与提示信息，
 * 而非兜底 500 错误。
 * </p>
 */
@Getter
public class BizException extends RuntimeException {

    /** 业务状态码枚举，携带 code 和 msg */
    private final ResultCode code;

    public BizException(ResultCode code) {
        super(code.getMsg());
        this.code = code;
    }
}
