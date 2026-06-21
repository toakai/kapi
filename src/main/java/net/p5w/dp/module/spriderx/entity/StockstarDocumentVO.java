package net.p5w.dp.module.spriderx.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * tb_stockstar_document 与 tb_company_baseinfo 关联查询结果 VO
 * <p>
 * 通过 FIND_IN_SET(tb_company_baseinfo.company_code, tb_stockstar_document.stockcode)
 * 关联，同一篇文档可能匹配多条公司记录。
 * </p>
 *
 * @author dpa
 */
@Data
public class StockstarDocumentVO {

    // ==================== tb_stockstar_document 字段 ====================
    private Integer docId;
    private Integer channelid;
    private String title;
    private String guid;
    private String link;
    private String category;
    private String content;
    private String source;
    private String author;
    private String keywords;
    private LocalDateTime pubDate;
    private String stockcode;
    private Integer status;
    private String remark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    // ==================== tb_company_baseinfo 字段 ====================
    private Integer companyId;
    private String pid;
    private String companyCode;
    private String companyName;
    private String jurisdiction;
    private String platformName;
}
