package net.p5w.dp.module.wcm.controller;

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
import net.p5w.dp.module.wcm.entity.XwcmHotwords;
import net.p5w.dp.module.wcm.query.XwcmHotwordsQuery;
import net.p5w.dp.module.wcm.service.XwcmHotwordsService;

/**
 * wcm 数据源 REST API
 * <p>管理 xwcmhotwords 表</p>
 *
 * @author dpa
 */
@Slf4j
@RestController
@RequestMapping("/api/wcm")
@RequiredArgsConstructor
public class WcmController {

    private final XwcmHotwordsService xwcmHotwordsService;

    // ==================== xwcmhotwords ====================

    /**
     * 分页查询
     */
    @GetMapping("/hotwords")
    public Result<PageResult<XwcmHotwords>> page(XwcmHotwordsQuery query) {
        log.debug("请求分页查询 xwcmhotwords");
        return Result.success(xwcmHotwordsService.page(query));
    }

    /**
     * 根据 ID 查询
     */
    @GetMapping("/hotwords/{id}")
    public Result<XwcmHotwords> getById(@PathVariable Integer id) {
        log.debug("请求查询 xwcmhotwords，id={}", id);
        return Result.success(xwcmHotwordsService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping("/hotwords")
    public Result<XwcmHotwords> add(@RequestBody XwcmHotwords record) {
        log.info("请求新增 xwcmhotwords，hotwords={}", record.getHotwords());
        return Result.success(xwcmHotwordsService.add(record));
    }

    /**
     * 更新
     */
    @PutMapping("/hotwords/{id}")
    public Result<XwcmHotwords> update(@PathVariable Integer id, @RequestBody XwcmHotwords record) {
        record.setHotwordsid(id);
        log.info("请求更新 xwcmhotwords，id={}", id);
        return Result.success(xwcmHotwordsService.update(record));
    }

    /**
     * 删除
     */
    @DeleteMapping("/hotwords/{id}")
    public Result<Void> deleteById(@PathVariable Integer id) {
        log.info("请求删除 xwcmhotwords，id={}", id);
        xwcmHotwordsService.deleteById(id);
        return Result.success();
    }
}
