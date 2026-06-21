package net.p5w.dp.module.wcm.service;

import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.module.wcm.entity.XwcmHotwords;
import net.p5w.dp.module.wcm.query.XwcmHotwordsQuery;

/**
 * xwcmhotwords 业务接口
 *
 * @author dpa
 */
public interface XwcmHotwordsService {

    /**
     * 条件分页查询
     */
    PageResult<XwcmHotwords> page(XwcmHotwordsQuery query);

    /**
     * 根据 ID 查询
     */
    XwcmHotwords getById(Integer hotwordsid);

    /**
     * 新增
     */
    XwcmHotwords add(XwcmHotwords record);

    /**
     * 更新
     */
    XwcmHotwords update(XwcmHotwords record);

    /**
     * 根据 ID 删除
     */
    void deleteById(Integer hotwordsid);
}
