package net.p5w.dp.module.cpdb.service.impl;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.p5w.dp.common.exception.BizException;
import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.common.result.ResultCode;
import net.p5w.dp.module.cpdb.entity.TbPublic0007;
import net.p5w.dp.module.cpdb.mapper.TbPublic0007Mapper;
import net.p5w.dp.module.cpdb.query.TbPublic0007Query;
import net.p5w.dp.module.cpdb.service.TbPublic0007Service;

/**
 * tb_public_0007 业务实现
 * <p>数据源切换由 Mapper 层 @DataSource 注解控制，Service 层无需关心</p>
 *
 * @author dpa
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TbPublic0007ServiceImpl implements TbPublic0007Service {

    private final TbPublic0007Mapper mapper;

    @Override
    public PageResult<TbPublic0007> page(TbPublic0007Query query) {
        log.debug("分页查询 tb_public_0007，page={}, size={}", query.getPage(), query.getSize());
        PageHelper.startPage(query.getPage(), query.getSize());
        PageInfo<TbPublic0007> pageInfo = new PageInfo<>(mapper.selectPage(query));
        log.debug("查询结果：total={}, pageNum={}", pageInfo.getTotal(), pageInfo.getPageNum());
        return PageResult.from(pageInfo);
    }

    @Override
    public TbPublic0007 getByKey(String obSecid0007, String obSeccode0007, String f002v0007, String f005v0007) {
        log.debug("查询 tb_public_0007，key={}-{}-{}-{}", obSecid0007, obSeccode0007, f002v0007, f005v0007);
        TbPublic0007 record = mapper.selectByKey(obSecid0007, obSeccode0007, f002v0007, f005v0007);
        if (record == null) {
            log.warn("tb_public_0007 数据不存在");
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        return record;
    }

    @Override
    public TbPublic0007 add(TbPublic0007 record) {
        log.info("新增 tb_public_0007，SECNAME={}", record.getObSecname0007());
        int rows = mapper.insert(record);
        if (rows <= 0) {
            log.error("新增 tb_public_0007 失败");
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("新增成功");
        return mapper.selectByKey(record.getObSecid0007(), record.getObSeccode0007(),
                record.getF002v0007(), record.getF005v0007());
    }

    @Override
    public TbPublic0007 update(TbPublic0007 record) {
        log.info("更新 tb_public_0007，key={}-{}-{}-{}",
                record.getObSecid0007(), record.getObSeccode0007(), record.getF002v0007(), record.getF005v0007());
        TbPublic0007 existing = mapper.selectByKey(
                record.getObSecid0007(), record.getObSeccode0007(), record.getF002v0007(), record.getF005v0007());
        if (existing == null) {
            log.warn("更新失败，数据不存在");
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        int rows = mapper.updateByKey(record);
        if (rows <= 0) {
            log.error("更新 tb_public_0007 失败");
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("更新成功");
        return mapper.selectByKey(record.getObSecid0007(), record.getObSeccode0007(),
                record.getF002v0007(), record.getF005v0007());
    }

    @Override
    public void deleteByKey(String obSecid0007, String obSeccode0007, String f002v0007, String f005v0007) {
        log.info("删除 tb_public_0007，key={}-{}-{}-{}", obSecid0007, obSeccode0007, f002v0007, f005v0007);
        TbPublic0007 existing = mapper.selectByKey(obSecid0007, obSeccode0007, f002v0007, f005v0007);
        if (existing == null) {
            log.warn("删除失败，数据不存在");
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        int rows = mapper.deleteByKey(obSecid0007, obSeccode0007, f002v0007, f005v0007);
        if (rows <= 0) {
            log.error("删除 tb_public_0007 失败");
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("删除成功");
    }
}
