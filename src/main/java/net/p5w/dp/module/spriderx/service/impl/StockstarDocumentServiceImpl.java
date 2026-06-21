package net.p5w.dp.module.spriderx.service.impl;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.p5w.dp.common.exception.BizException;
import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.common.result.ResultCode;
import net.p5w.dp.module.spriderx.entity.StockstarDocument;
import net.p5w.dp.module.spriderx.entity.StockstarDocumentVO;
import net.p5w.dp.module.spriderx.mapper.StockstarDocumentMapper;
import net.p5w.dp.module.spriderx.query.StockstarDocumentQuery;
import net.p5w.dp.module.spriderx.service.StockstarDocumentService;

/**
 * tb_stockstar_document 业务实现
 *
 * @author dpa
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockstarDocumentServiceImpl implements StockstarDocumentService {

    private final StockstarDocumentMapper mapper;

    @Override
    public PageResult<StockstarDocument> page(StockstarDocumentQuery query) {
        log.debug("分页查询 tb_stockstar_document，page={}, size={}", query.getPage(), query.getSize());
        PageHelper.startPage(query.getPage(), query.getSize());
        PageInfo<StockstarDocument> pageInfo = new PageInfo<>(mapper.selectPage(query));
        log.debug("查询结果：total={}, pageNum={}", pageInfo.getTotal(), pageInfo.getPageNum());
        return PageResult.from(pageInfo);
    }

    @Override
    public StockstarDocument getById(Integer id) {
        log.debug("查询 tb_stockstar_document，id={}", id);
        StockstarDocument record = mapper.selectById(id);
        if (record == null) {
            log.warn("tb_stockstar_document 数据不存在，id={}", id);
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        return record;
    }

    @Override
    public StockstarDocument add(StockstarDocument record) {
        log.info("新增 tb_stockstar_document，title={}", record.getTitle());
        int rows = mapper.insert(record);
        if (rows <= 0) {
            log.error("新增 tb_stockstar_document 失败，title={}", record.getTitle());
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("新增成功，id={}", record.getId());
        return mapper.selectById(record.getId());
    }

    @Override
    public StockstarDocument update(StockstarDocument record) {
        log.info("更新 tb_stockstar_document，id={}", record.getId());
        StockstarDocument existing = mapper.selectById(record.getId());
        if (existing == null) {
            log.warn("更新失败，数据不存在，id={}", record.getId());
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        int rows = mapper.updateById(record);
        if (rows <= 0) {
            log.error("更新 tb_stockstar_document 失败，id={}", record.getId());
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("更新成功，id={}", record.getId());
        return mapper.selectById(record.getId());
    }

    @Override
    public void deleteById(Integer id) {
        log.info("删除 tb_stockstar_document，id={}", id);
        StockstarDocument existing = mapper.selectById(id);
        if (existing == null) {
            log.warn("删除失败，数据不存在，id={}", id);
            throw new BizException(ResultCode.DATA_NOT_EXIST);
        }
        int rows = mapper.deleteById(id);
        if (rows <= 0) {
            log.error("删除 tb_stockstar_document 失败，id={}", id);
            throw new BizException(ResultCode.DB_ERROR);
        }
        log.info("删除成功，id={}", id);
    }

    @Override
    public PageResult<StockstarDocumentVO> pageJoin(StockstarDocumentQuery query) {
        log.debug("关联查询 tb_stockstar_document + tb_company_baseinfo，page={}, size={}", query.getPage(), query.getSize());
        PageHelper.startPage(query.getPage(), query.getSize());
        PageInfo<StockstarDocumentVO> pageInfo = new PageInfo<>(mapper.selectJoinPage(query));
        log.debug("关联查询结果：total={}, pageNum={}", pageInfo.getTotal(), pageInfo.getPageNum());
        return PageResult.from(pageInfo);
    }
}
