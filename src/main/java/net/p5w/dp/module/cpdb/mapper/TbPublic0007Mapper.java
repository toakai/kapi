package net.p5w.dp.module.cpdb.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.dynamic.datasource.annotation.DS;

import net.p5w.dp.common.db.DataSourceName;
import net.p5w.dp.module.cpdb.entity.TbPublic0007;
import net.p5w.dp.module.cpdb.query.TbPublic0007Query;

/**
 * tb_public_0007 Mapper（cpdb 数据源）
 * <p>通过方法级 @DataSource 注解自动切换数据源，Service 层无需关心</p>
 *
 * @author dpa
 */
@DS(DataSourceName.CPDB)
public interface TbPublic0007Mapper {

    /**
     * 条件分页查询
     */
    List<TbPublic0007> selectPage(@Param("query") TbPublic0007Query query);

    /**
     * 根据复合主键查询
     */
    TbPublic0007 selectByKey(@Param("obSecid0007") String obSecid0007,
                             @Param("obSeccode0007") String obSeccode0007,
                             @Param("f002v0007") String f002v0007,
                             @Param("f005v0007") String f005v0007);

    /**
     * 新增
     */
    int insert(TbPublic0007 record);

    /**
     * 根据复合主键更新
     */
    int updateByKey(TbPublic0007 record);

    /**
     * 根据复合主键删除
     */
    int deleteByKey(@Param("obSecid0007") String obSecid0007,
                    @Param("obSeccode0007") String obSeccode0007,
                    @Param("f002v0007") String f002v0007,
                    @Param("f005v0007") String f005v0007);
}
