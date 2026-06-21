package net.p5w.dp.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局统一响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端参数错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "登录已失效，请重新登录"),
    FORBIDDEN(403, "暂无权限访问"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方式不支持"),

    // 分页参数专属提示
    PAGE_NUM_ERROR(400, "当前页码不能小于1"),
    PAGE_SIZE_ERROR(400, "每页条数不能小于1"),
    PAGE_SIZE_MAX_ERROR(400, "每页最大支持100条数据"),

    // 文件上传专属提示
    FILE_TOO_LARGE(400, "上传文件超过大小限制"),
    FILE_TYPE_NOT_ALLOWED(400, "不支持的文件类型"),

    // 服务端异常 5xx
    SERVER_ERROR(500, "服务器繁忙，请稍后重试"),
    DB_ERROR(500, "数据库操作异常"),

    // 业务自定义状态码 6xx/7xx自行扩展
    DATA_NOT_EXIST(601, "数据不存在"),
    DATA_EXIST(602, "数据已存在");

    private final Integer code;
    private final String msg;
}