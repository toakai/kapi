package net.p5w.dp.module.cpdb.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.p5w.dp.common.result.PageResult;
import net.p5w.dp.common.result.Result;
import net.p5w.dp.module.cpdb.entity.TbPublic0007;
import net.p5w.dp.module.cpdb.query.TbPublic0007Query;
import net.p5w.dp.module.cpdb.service.TbPublic0007Service;

/**
 * cpdb 数据源 REST API
 * <p>管理 tb_public_0007 表（复合主键：OB_SECID_0007 + OB_SECCODE_0007 + F002V_0007 + F005V_0007）</p>
 *
 * @author dpa
 */
@Slf4j
@RestController
@RequestMapping("/api/cpdb")
@RequiredArgsConstructor
public class CpdbController {

    private final TbPublic0007Service tbPublic0007Service;

    // ==================== tb_public_0007 ====================

    /**
     * 分页查询
     */
    @GetMapping("/public-0007")
    public Result<PageResult<TbPublic0007>> page(TbPublic0007Query query) {
        log.debug("请求分页查询 tb_public_0007");
        return Result.success(tbPublic0007Service.page(query));
    }

    /**
     * 根据复合主键查询
     */
    @GetMapping("/public-0007/detail")
    public Result<TbPublic0007> getByKey(@RequestParam String secid,
                                          @RequestParam String seccode,
                                          @RequestParam String f002v,
                                          @RequestParam String f005v) {
        log.debug("请求查询 tb_public_0007，key={}-{}-{}-{}", secid, seccode, f002v, f005v);
        return Result.success(tbPublic0007Service.getByKey(secid, seccode, f002v, f005v));
    }

    /**
     * 新增
     */
    @PostMapping("/public-0007")
    public Result<TbPublic0007> add(@RequestBody TbPublic0007 record) {
        log.info("请求新增 tb_public_0007，SECNAME={}", record.getObSecname0007());
        return Result.success(tbPublic0007Service.add(record));
    }

    /**
     * 更新（复合主键从请求体中读取）
     */
    @PutMapping("/public-0007")
    public Result<TbPublic0007> update(@RequestBody TbPublic0007 record) {
        log.info("请求更新 tb_public_0007");
        return Result.success(tbPublic0007Service.update(record));
    }

    /**
     * 根据复合主键删除
     */
    @DeleteMapping("/public-0007")
    public Result<Void> deleteByKey(@RequestParam String secid,
                                     @RequestParam String seccode,
                                     @RequestParam String f002v,
                                     @RequestParam String f005v) {
        log.info("请求删除 tb_public_0007，key={}-{}-{}-{}", secid, seccode, f002v, f005v);
        tbPublic0007Service.deleteByKey(secid, seccode, f002v, f005v);
        return Result.success();
    }
}
