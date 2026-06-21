package net.p5w.dp.module.wcm.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * xwcmhotwords 实体（wcm 数据源 → trswcm 库）
 *
 * @author dpa
 */
@Data
public class XwcmHotwords {

    private Integer hotwordsid;
    /** 热词 */
    private String hotwords;
    /** 替换 URI */
    private String substituteuri;
    /** 说明 */
    private String instrut;
    /** 热词类型，默认 0 */
    private Integer wordstype;
    /** 创建人 */
    private String crUser;
    /** 创建时间 */
    private LocalDateTime crTime;
    /** 是否热词组，默认 0 */
    private Integer iswordsgroup;
    /** 租户 ID */
    private Integer tenantid;
    /** 是否启用 */
    private Integer isok;
    /** 股票代码 */
    private String stockcode;
    private String reservedthree;
    /** a 标签属性 */
    private String atagattribute;
}
