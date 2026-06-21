package net.p5w.dp.module.spriderx.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.p5w.dp.common.query.PageQuery;

/**
 * tb_stockstar_document 查询参数
 *
 * @author dpa
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StockstarDocumentQuery extends PageQuery {

    /** 标题，模糊匹配 */
    private String title;

    /** 来源 */
    private String source;

    /** 作者 */
    private String author;

    /** 股票代码 */
    private String stockcode;

    /** 状态 */
    private Integer status;
}
