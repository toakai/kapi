package net.p5w.dp.module.cpdb.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.p5w.dp.common.query.PageQuery;

/**
 * tb_public_0007 查询参数
 *
 * @author dpa
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TbPublic0007Query extends PageQuery {

    /** 证券名称，模糊匹配 */
    private String obSecname0007;

    /** 证券代码 */
    private String obSeccode0007;

    /** 是否有效 */
    private String obIsvalid0007;
}
