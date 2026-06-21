package net.p5w.dp.module.spriderx.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.common.result.Result;
import net.p5w.dp.module.spriderx.entity.CompanyBaseinfo;
import net.p5w.dp.module.spriderx.entity.StockstarDocument;
import net.p5w.dp.module.spriderx.entity.StockstarDocumentVO;
import net.p5w.dp.module.spriderx.query.CompanyBaseinfoQuery;
import net.p5w.dp.module.spriderx.query.StockstarDocumentQuery;
import net.p5w.dp.module.spriderx.service.CompanyBaseinfoService;
import net.p5w.dp.module.spriderx.service.StockstarDocumentService;

/**
 * spriderx 数据源 REST API
 * <p>管理 tb_stockstar_document 和 tb_company_baseinfo 两张表</p>
 *
 * @author dpa
 */
@Slf4j
@RestController
@RequestMapping("/api/spriderx")
@RequiredArgsConstructor
public class SpriderxController {

    private final StockstarDocumentService stockstarDocumentService;
    private final CompanyBaseinfoService companyBaseinfoService;

    // ==================== tb_stockstar_document ====================

    /**
     * 分页查询文档
     */
    @GetMapping("/stockstar-document")
    public Result<PageResult<StockstarDocument>> pageDocument(StockstarDocumentQuery query) {
        log.debug("请求分页查询 tb_stockstar_document");
        return Result.success(stockstarDocumentService.page(query));
    }

    /**
     * 根据 ID 查询文档
     */
    @GetMapping("/stockstar-document/{id}")
    public Result<StockstarDocument> getDocument(@PathVariable Integer id) {
        log.debug("请求查询 tb_stockstar_document，id={}", id);
        return Result.success(stockstarDocumentService.getById(id));
    }

    /**
     * 新增文档
     */
    @PostMapping("/stockstar-document")
    public Result<StockstarDocument> addDocument(@RequestBody StockstarDocument record) {
        log.info("请求新增 tb_stockstar_document，title={}", record.getTitle());
        return Result.success(stockstarDocumentService.add(record));
    }

    /**
     * 更新文档
     */
    @PutMapping("/stockstar-document/{id}")
    public Result<StockstarDocument> updateDocument(@PathVariable Integer id, @RequestBody StockstarDocument record) {
        record.setId(id);
        log.info("请求更新 tb_stockstar_document，id={}", id);
        return Result.success(stockstarDocumentService.update(record));
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/stockstar-document/{id}")
    public Result<Void> deleteDocument(@PathVariable Integer id) {
        log.info("请求删除 tb_stockstar_document，id={}", id);
        stockstarDocumentService.deleteById(id);
        return Result.success();
    }

    /**
     * 关联查询（JOIN tb_company_baseinfo）
     */
    @GetMapping("/stockstar-document/join")
    public Result<PageResult<StockstarDocumentVO>> pageDocumentJoin(StockstarDocumentQuery query) {
        log.debug("请求关联查询 tb_stockstar_document + tb_company_baseinfo");
        return Result.success(stockstarDocumentService.pageJoin(query));
    }

    // ==================== tb_company_baseinfo ====================

    /**
     * 分页查询公司基础信息
     */
    @GetMapping("/company-baseinfo")
    public Result<PageResult<CompanyBaseinfo>> pageCompany(CompanyBaseinfoQuery query) {
        log.debug("请求分页查询 tb_company_baseinfo");
        return Result.success(companyBaseinfoService.page(query));
    }

    /**
     * 根据 ID 查询公司基础信息
     */
    @GetMapping("/company-baseinfo/{id}")
    public Result<CompanyBaseinfo> getCompany(@PathVariable Integer id) {
        log.debug("请求查询 tb_company_baseinfo，id={}", id);
        return Result.success(companyBaseinfoService.getById(id));
    }

    /**
     * 新增公司基础信息
     */
    @PostMapping("/company-baseinfo")
    public Result<CompanyBaseinfo> addCompany(@RequestBody CompanyBaseinfo record) {
        log.info("请求新增 tb_company_baseinfo，companyName={}", record.getCompanyName());
        return Result.success(companyBaseinfoService.add(record));
    }

    /**
     * 更新公司基础信息
     */
    @PutMapping("/company-baseinfo/{id}")
    public Result<CompanyBaseinfo> updateCompany(@PathVariable Integer id, @RequestBody CompanyBaseinfo record) {
        record.setId(id);
        log.info("请求更新 tb_company_baseinfo，id={}", id);
        return Result.success(companyBaseinfoService.update(record));
    }

    /**
     * 删除公司基础信息
     */
    @DeleteMapping("/company-baseinfo/{id}")
    public Result<Void> deleteCompany(@PathVariable Integer id) {
        log.info("请求删除 tb_company_baseinfo，id={}", id);
        companyBaseinfoService.deleteById(id);
        return Result.success();
    }
}
