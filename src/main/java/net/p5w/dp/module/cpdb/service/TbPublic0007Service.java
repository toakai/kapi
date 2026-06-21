package net.p5w.dp.module.cpdb.service;

import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.module.cpdb.entity.TbPublic0007;
import net.p5w.dp.module.cpdb.query.TbPublic0007Query;

/**
 * tb_public_0007 业务接口
 *
 * @author dpa
 */
public interface TbPublic0007Service {

    /**
     * 条件分页查询
     */
    PageResult<TbPublic0007> page(TbPublic0007Query query);

    /**
     * 根据复合主键查询
     */
    TbPublic0007 getByKey(String obSecid0007, String obSeccode0007, String f002v0007, String f005v0007);

    /**
     * 新增
     */
    TbPublic0007 add(TbPublic0007 record);

    /**
     * 更新
     */
    TbPublic0007 update(TbPublic0007 record);

    /**
     * 根据复合主键删除
     */
    void deleteByKey(String obSecid0007, String obSeccode0007, String f002v0007, String f005v0007);
}
