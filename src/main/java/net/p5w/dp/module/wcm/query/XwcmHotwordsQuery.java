package net.p5w.dp.module.wcm.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.p5w.dp.common.query.PageQuery;

/**
 * xwcmhotwords 查询参数
 *
 * @author dpa
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class XwcmHotwordsQuery extends PageQuery {

    /** 热词，模糊匹配 */
    private String hotwords;

    /** 股票代码 */
    private String stockcode;

    /** 是否启用 */
    private Integer isok;
}
