package net.p5w.dp.module.spriderx.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import net.p5w.dp.module.spriderx.entity.CompanyBaseinfo;
import net.p5w.dp.module.spriderx.query.CompanyBaseinfoQuery;

/**
 * tb_company_baseinfo Mapper（spriderx 主库，无需 @DataSource 注解）
 *
 * @author dpa
 */
public interface CompanyBaseinfoMapper {

    /**
     * 条件分页查询
     */
    List<CompanyBaseinfo> selectPage(@Param("query") CompanyBaseinfoQuery query);

    /**
     * 根据 ID 查询
     */
    CompanyBaseinfo selectById(@Param("id") Integer id);

    /**
     * 新增
     */
    int insert(CompanyBaseinfo record);

    /**
     * 根据 ID 更新（仅更新非 null 字段）
     */
    int updateById(CompanyBaseinfo record);

    /**
     * 根据 ID 删除
     */
    int deleteById(@Param("id") Integer id);
}
