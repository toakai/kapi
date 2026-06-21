package net.p5w.dp.common.result;

import java.util.List;

import com.github.pagehelper.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用分页响应体
 * <p>
 * 与 {@link com.github.pagehelper.PageHelper} 配合使用：Service 层调用
 * {@link PageResult#from(PageInfo)} 一行即可将 PageHelper 的分页结果转为统一出参。
 * </p>
 *
 * <pre>{@code
 * PageHelper.startPage(query.getPage(), query.getSize());
 * return PageResult.from(new PageInfo<>(mapper.selectPage(query)));
 * }</pre>
 *
 * @see net.p5w.dp.common.query.PageQuery
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 总记录数（使用 Long，防止大数据量溢出） */
    private Long total;

    /** 每页条数 */
    private Integer pageSize;

    /** 当前页码（从 1 开始） */
    private Integer pageNum;

    /** 总页数 */
    private Integer totalPage;

    /** 当前页数据列表 */
    private List<T> list;

    // ======================== 工厂方法 ========================

    /**
     * 从 PageHelper 的 PageInfo 直接构建，推荐用法。
     *
     * @param pageInfo PageHelper 分页结果
     * @param <T>      数据类型
     * @return 统一分页响应体
     */
    public static <T> PageResult<T> from(PageInfo<T> pageInfo) {
        return build(pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum(), pageInfo.getList());
    }

    /**
     * 手动构建分页结果（非 PageHelper 场景）。
     *
     * @param total    总记录数
     * @param pageSize 每页条数
     * @param pageNum  当前页码
     * @param list     当前页数据
     * @param <T>      数据类型
     * @return 统一分页响应体
     * @throws ArithmeticException 若总页数超出 int 范围（极端情况）
     */
    public static <T> PageResult<T> build(Long total, Integer pageSize, Integer pageNum, List<T> list) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPageSize(pageSize);
        pageResult.setPageNum(pageNum);
        pageResult.setList(list);

        // 向上取整计算总页数；Math.toIntExact 在超出 int 范围时抛出异常，而非静默截断
        long totalPageLong = (total + pageSize - 1) / pageSize;
        pageResult.setTotalPage(Math.toIntExact(totalPageLong));

        return pageResult;
    }
}
