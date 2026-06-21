package net.p5w.dp.module.spriderx.entity;

import lombok.Data;

/**
 * tb_company_baseinfo 实体（spriderx 主库）
 *
 * @author dpa
 */
@Data
public class CompanyBaseinfo {

    private Integer id;
    private String pid;
    /** 公司代码，与 tb_stockstar_document.stockcode 关联 */
    private String companyCode;
    private String companyName;
    private String jurisdiction;
    private String platformName;
}
