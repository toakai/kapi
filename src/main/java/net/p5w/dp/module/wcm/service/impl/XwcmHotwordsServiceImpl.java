package net.p5w.dp.module.wcm.service.impl;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.p5w.dp.common.exception.BizException;
import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.common.result.ResultCode;
import net.p5w.dp.module.wcm.entity.XwcmHotwords;
import net.p5w.dp.module.wcm.mapper.XwcmHotwordsMapper;
import net.p5w.dp.module.wcm.query.XwcmHotwordsQuery;
import net.p5w.dp.module.wcm.service.XwcmHotwordsService;

/**
 * xwcmhotwords 业务实现
 * <p>数据源切换由 Mapper 层 @DataSource 注解控制，Service 层无需关心</p>
 *
 * @author dpa
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XwcmHotwordsServiceImpl implements XwcmHotwordsService {

    private final XwcmHotwordsMapper mapper;

    @Override
    public PageResult<XwcmHotwords> page(XwcmHotwordsQuery query) {
        log.debug("分页查询 xwcmhotwords，page={}, size={}", query.getPage(), query.getSize());
        PageHelper.startPage(query.getPage(), query.getSize());
        PageInfo<XwcmHotwords> pageInfo = new PageInfo<>(mapper.selectPage(query));
        log.debug("查询结果：total={}, pageNum={}", pageInfo.getTotal(), pageInfo.getPageNum());
        return PageResult.from(pageInfo);
    }

    @Override
    public XwcmHotwords getById(Integer hotwordsid) {
        log.debug("查询 xwcmhotwords，id={}", hotwordsid);
        XwcmHotwords record = mapper.selectById(hotwordsid);
        if (record == null) {
            log.warn("xwcmhotwords 数据不存在，id={}", hotwordsid);
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        return record;
    }

    @Override
    public XwcmHotwords add(XwcmHotwords record) {
        log.info("新增 xwcmhotwords，hotwords={}", record.getHotwords());
        int rows = mapper.insert(record);
        if (rows <= 0) {
            log.error("新增 xwcmhotwords 失败");
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("新增成功，id={}", record.getHotwordsid());
        return mapper.selectById(record.getHotwordsid());
    }

    @Override
    public XwcmHotwords update(XwcmHotwords record) {
        log.info("更新 xwcmhotwords，id={}", record.getHotwordsid());
        XwcmHotwords existing = mapper.selectById(record.getHotwordsid());
        if (existing == null) {
            log.warn("更新失败，数据不存在，id={}", record.getHotwordsid());
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        int rows = mapper.updateById(record);
        if (rows <= 0) {
            log.error("更新 xwcmhotwords 失败，id={}", record.getHotwordsid());
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("更新成功，id={}", record.getHotwordsid());
        return mapper.selectById(record.getHotwordsid());
    }

    @Override
    public void deleteById(Integer hotwordsid) {
        log.info("删除 xwcmhotwords，id={}", hotwordsid);
        XwcmHotwords existing = mapper.selectById(hotwordsid);
        if (existing == null) {
            log.warn("删除失败，数据不存在，id={}", hotwordsid);
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        int rows = mapper.deleteById(hotwordsid);
        if (rows <= 0) {
            log.error("删除 xwcmhotwords 失败，id={}", hotwordsid);
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("删除成功，id={}", hotwordsid);
    }
}
