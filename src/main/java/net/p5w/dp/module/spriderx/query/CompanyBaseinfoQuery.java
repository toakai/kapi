package net.p5w.dp.module.spriderx.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.p5w.dp.common.query.PageQuery;

/**
 * tb_company_baseinfo 查询参数
 *
 * @author dpa
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyBaseinfoQuery extends PageQuery {

    /** 公司名称，模糊匹配 */
    private String companyName;

    /** 公司代码 */
    private String companyCode;
}
