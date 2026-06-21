package net.p5w.dp.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import lombok.extern.slf4j.Slf4j;
import net.p5w.dp.common.result.Result;
import net.p5w.dp.common.result.ResultCode;

/**
 * 全局统一异常处理器
 * <p>
 * 捕获顺序（Spring 按最精确匹配优先）：
 * <ol>
 *   <li>{@link BizException}：业务主动抛出的可预期异常，返回对应业务码</li>
 *   <li>{@link MaxUploadSizeExceededException}：文件超过 multipart 大小限制</li>
 *   <li>{@link org.springframework.validation.BindException}：参数绑定异常（PageQuery setter 中抛出）</li>
 *   <li>{@link IllegalArgumentException}：参数校验异常</li>
 *   <li>{@link Exception}：兜底，返回 500</li>
 * </ol>
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ======================== 1. 业务异常（鉴权失败、资源不存在等） ========================
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常：code={}, msg={}", e.getCode().getCode(), e.getMessage());
        return Result.fail(e.getCode().getCode(), e.getMessage());
    }

    // ======================== 2. 文件超过 multipart 大小限制 ========================
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("上传文件超过大小限制：{}", e.getMessage());
        return Result.fail(ResultCode.FILE_TOO_LARGE.getCode(), ResultCode.FILE_TOO_LARGE.getMsg());
    }

    // ======================== 3. 分页 / 表单参数绑定异常（PageQuery setter 校验） ========================
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public Result<Void> handleBindException(org.springframework.validation.BindException e) {
        String defaultMessage = e.getFieldError().getDefaultMessage();

        // BindException 包裹了 setter 中抛出的 IllegalArgumentException，需提取真实消息
        String realMsg = defaultMessage;
        if (defaultMessage.contains("IllegalArgumentException")) {
            realMsg = defaultMessage.split("java.lang.IllegalArgumentException: ")[1].trim();
        }

        log.warn("参数绑定异常：{}", realMsg);
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), realMsg);
    }

    // ======================== 4. 普通业务参数校验异常 ========================
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("业务参数异常：{}", e.getMessage());
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), e.getMessage());
    }

    // ======================== 5. 全局兜底异常（未预期错误，返回 500） ========================
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("服务器未知异常", e);
        return Result.fail(ResultCode.SERVER_ERROR.getCode(), ResultCode.SERVER_ERROR.getMsg());
    }
}