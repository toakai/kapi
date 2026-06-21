package net.p5w.dp.module.spriderx.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import net.p5w.dp.module.spriderx.entity.StockstarDocument;
import net.p5w.dp.module.spriderx.entity.StockstarDocumentVO;
import net.p5w.dp.module.spriderx.query.StockstarDocumentQuery;

/**
 * tb_stockstar_document Mapper（spriderx 主库，无需 @DataSource 注解）
 *
 * @author dpa
 */
public interface StockstarDocumentMapper {

    /**
     * 条件分页查询
     */
    List<StockstarDocument> selectPage(@Param("query") StockstarDocumentQuery query);

    /**
     * 根据 ID 查询
     */
    StockstarDocument selectById(@Param("id") Integer id);

    /**
     * 新增
     */
    int insert(StockstarDocument record);

    /**
     * 根据 ID 更新（仅更新非 null 字段）
     */
    int updateById(StockstarDocument record);

    /**
     * 根据 ID 删除
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 与 tb_company_baseinfo 关联查询，带分页
     * <p>通过 FIND_IN_SET 匹配 stockcode 中的逗号分隔代码</p>
     */
    List<StockstarDocumentVO> selectJoinPage(@Param("query") StockstarDocumentQuery query);
}
