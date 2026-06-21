package net.p5w.dp.module.spriderx.service;

import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.module.spriderx.entity.StockstarDocument;
import net.p5w.dp.module.spriderx.entity.StockstarDocumentVO;
import net.p5w.dp.module.spriderx.query.StockstarDocumentQuery;

/**
 * tb_stockstar_document 业务接口
 *
 * @author dpa
 */
public interface StockstarDocumentService {

    /**
     * 条件分页查询
     */
    PageResult<StockstarDocument> page(StockstarDocumentQuery query);

    /**
     * 根据 ID 查询
     */
    StockstarDocument getById(Integer id);

    /**
     * 新增
     */
    StockstarDocument add(StockstarDocument record);

    /**
     * 更新
     */
    StockstarDocument update(StockstarDocument record);

    /**
     * 根据 ID 删除
     */
    void deleteById(Integer id);

    /**
     * 与 tb_company_baseinfo 关联分页查询
     */
    PageResult<StockstarDocumentVO> pageJoin(StockstarDocumentQuery query);
}
