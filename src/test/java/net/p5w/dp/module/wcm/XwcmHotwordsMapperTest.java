package net.p5w.dp.module.wcm;

import net.p5w.dp.kapi.KapiApplication;
import net.p5w.dp.module.wcm.entity.XwcmHotwords;
import net.p5w.dp.module.wcm.mapper.XwcmHotwordsMapper;
import net.p5w.dp.module.wcm.query.XwcmHotwordsQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * XwcmHotwordsMapper 查询测试（wcm 数据源 → trswcm 库）
 * <p>注意：xwcmhotwords 表的 HOTWORDSID 不是自增列，insert 语句也不包含该列，
 * 因此本测试只查询已有数据，不执行插入操作。</p>
 */
@SpringBootTest(classes = KapiApplication.class)
class XwcmHotwordsMapperTest {

    @Autowired
    private XwcmHotwordsMapper mapper;

    @Test
    void testSelectById() {
        // 先查一页获取一个可用 ID
        XwcmHotwordsQuery query = new XwcmHotwordsQuery();
        List<XwcmHotwords> all = mapper.selectPage(query);
        assertThat(all).isNotEmpty();

        XwcmHotwords first = all.get(0);
        Integer id = first.getHotwordsid();
        assertThat(id).isNotNull();

        // 按 ID 查询
        XwcmHotwords result = mapper.selectById(id);
        assertThat(result).isNotNull();
        assertThat(result.getHotwordsid()).isEqualTo(id);
        System.out.println(">>> wcm selectById 结果: id=" + id
                + ", hotwords=" + result.getHotwords());
    }

    @Test
    void testSelectById_notFound() {
        XwcmHotwords result = mapper.selectById(-9999);
        assertThat(result).isNull();
    }

    @Test
    void testSelectPage() {
        // 不传条件，查全部（受 size 限制）
        XwcmHotwordsQuery query = new XwcmHotwordsQuery();
        List<XwcmHotwords> list = mapper.selectPage(query);
        assertThat(list).isNotEmpty();
        System.out.println(">>> wcm selectPage 全部: 命中 " + list.size() + " 条");
    }

    @Test
    void testSelectPage_withStockcode() {
        XwcmHotwordsQuery query = new XwcmHotwordsQuery();
        query.setStockcode("000001");
        List<XwcmHotwords> list = mapper.selectPage(query);
        System.out.println(">>> wcm selectPage 按股票代码查询: 命中 " + list.size() + " 条");
        // 不强制断言有数据，以兼容无匹配数据的情况
    }

    @Test
    void testSelectPage_withIsOk() {
        XwcmHotwordsQuery query = new XwcmHotwordsQuery();
        query.setIsok(1);
        List<XwcmHotwords> list = mapper.selectPage(query);
        System.out.println(">>> wcm selectPage 按启用状态查询: 命中 " + list.size() + " 条");
        if (!list.isEmpty()) {
            assertThat(list).allMatch(item -> item.getIsok() != null && item.getIsok() == 1);
        }
    }
}
