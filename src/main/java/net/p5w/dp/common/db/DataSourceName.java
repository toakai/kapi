package net.p5w.dp.common.db;

/**
 * 数据源名称常量
 *
 * <p>供 {@link com.baomidou.dynamic.datasource.annotation.DS} 注解引用。</p>
 *
 * @see com.baomidou.dynamic.datasource.annotation.DS
 */
public final class DataSourceName {

    private DataSourceName() {
        // 工具类，禁止实例化
    }

    /** 主库（spriderx） */
    public static final String SPRIDERX = "spriderx-data-source";

    /** cpdb 数据源 */
    public static final String CPDB = "cpdb-data-source";

    /** wcm 数据源 */
    public static final String WCM = "wcm-data-source";

    // 如需新增数据源，在 YAML 中添加配置后，可在此处追加对应常量（可选）
    // public static final String NEW_DS = "new-data-source";
}
