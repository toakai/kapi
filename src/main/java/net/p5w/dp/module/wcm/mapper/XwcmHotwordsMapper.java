package net.p5w.dp.module.wcm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.dynamic.datasource.annotation.DS;

import net.p5w.dp.common.db.DataSourceName;
import net.p5w.dp.module.wcm.entity.XwcmHotwords;
import net.p5w.dp.module.wcm.query.XwcmHotwordsQuery;

/**
 * xwcmhotwords Mapper（wcm 数据源）
 * <p>通过方法级 @DataSource 注解自动切换数据源，Service 层无需关心</p>
 *
 * @author dpa
 */
@DS(DataSourceName.WCM)
public interface XwcmHotwordsMapper {

    /**
     * 条件分页查询
     */
    List<XwcmHotwords> selectPage(@Param("query") XwcmHotwordsQuery query);

    /**
     * 根据 ID 查询
     */
    XwcmHotwords selectById(@Param("hotwordsid") Integer hotwordsid);

    /**
     * 新增
     */
    int insert(XwcmHotwords record);

    /**
     * 根据 ID 更新
     */
    int updateById(XwcmHotwords record);

    /**
     * 根据 ID 删除
     */
    int deleteById(@Param("hotwordsid") Integer hotwordsid);
}
