package net.p5w.dp.module.spriderx.service;

import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.module.spriderx.entity.CompanyBaseinfo;
import net.p5w.dp.module.spriderx.query.CompanyBaseinfoQuery;

/**
 * tb_company_baseinfo 业务接口
 *
 * @author dpa
 */
public interface CompanyBaseinfoService {

    /**
     * 条件分页查询
     */
    PageResult<CompanyBaseinfo> page(CompanyBaseinfoQuery query);

    /**
     * 根据 ID 查询
     */
    CompanyBaseinfo getById(Integer id);

    /**
     * 新增
     */
    CompanyBaseinfo add(CompanyBaseinfo record);

    /**
     * 更新
     */
    CompanyBaseinfo update(CompanyBaseinfo record);

    /**
     * 根据 ID 删除
     */
    void deleteById(Integer id);
}
