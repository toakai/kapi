package net.p5w.dp.common.query;

import lombok.Data;
import net.p5w.dp.common.result.ResultCode;

/**
 * 通用分页查询入参
 * <p>
 * 所有分页查询接口统一使用此类作为入参（或继承此类扩展）。
 * {@code page} 从 1 开始，{@code size} 上限 100。
 * setter 中带有参数校验，非法值时直接抛出 {@link IllegalArgumentException}，
 * 由 {@link net.p5w.dp.common.exception.GlobalExceptionHandler} 统一捕获返回友好提示。
 * </p>
 *
 * @see net.p5w.dp.common.result.PageResult
 */
@Data
public class PageQuery {

    /** 当前页码，从 1 开始，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 10，最大 100 */
    private Integer size = 10;

    public void setPage(Integer page) {
        if (page == null || page < 1) {
            throw new IllegalArgumentException(ResultCode.PAGE_NUM_ERROR.getMsg());
        }
        this.page = page;
    }

    public void setSize(Integer size) {
        if (size == null || size < 1) {
            throw new IllegalArgumentException(ResultCode.PAGE_SIZE_ERROR.getMsg());
        }
        if (size > 100) {
            throw new IllegalArgumentException(ResultCode.PAGE_SIZE_MAX_ERROR.getMsg());
        }
        this.size = size;
    }
}
