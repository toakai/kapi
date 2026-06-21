package net.p5w.dp.module.spriderx.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * tb_stockstar_document 实体（spriderx 主库）
 *
 * @author dpa
 */
@Data
public class StockstarDocument {

    private Integer id;
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
    /** 关联 tb_company_baseinfo.company_code 的股票代码（可能多个，逗号分隔） */
    private String stockcode;
    private Integer status;
    private String remark;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
