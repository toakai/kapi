package net.p5w.dp.module.cpdb;

import net.p5w.dp.kapi.KapiApplication;
import net.p5w.dp.module.cpdb.entity.TbPublic0007;
import net.p5w.dp.module.cpdb.mapper.TbPublic0007Mapper;
import net.p5w.dp.module.cpdb.query.TbPublic0007Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TbPublic0007Mapper 查询测试（cpdb 数据源）
 * <p>测试条件分页查询和复合主键查询</p>
 */
@SpringBootTest(classes = KapiApplication.class)
class TbPublic0007MapperTest {

    @Autowired
    private TbPublic0007Mapper mapper;

    /** 测试数据的复合主键值 */
    private static final String TEST_SEC_ID = "T" + UUID.randomUUID().toString().substring(0, 8);
    private static final String TEST_SEC_CODE = "TEST_CODE";
    private static final String TEST_F002 = "TEST_F002";
    private static final String TEST_F005 = "TEST_F005";

    @BeforeEach
    void setUp() {
        // 清理可能残留的旧测试数据
        mapper.deleteByKey(TEST_SEC_ID, TEST_SEC_CODE, TEST_F002, TEST_F005);

        // 插入一条测试数据（填充所有 NOT NULL 字段）
        TbPublic0007 record = new TbPublic0007();
        record.setObSecid0007(TEST_SEC_ID);
        record.setObSeccode0007(TEST_SEC_CODE);
        record.setObSecname0007("测试证券名称");
        record.setF001v0007("测试F001");
        record.setF002v0007(TEST_F002);
        record.setF003v0007("测试F003");
        record.setF005v0007(TEST_F005);
        record.setF006v0007("测试F006");
        record.setF012v0007("F012值");
        record.setF013v0007("F013值");
        record.setObRectime0007(LocalDateTime.now());
        record.setObIsvalid0007("1");
        record.setObObjectId(BigDecimal.valueOf(100));
        record.setObSeqId(BigDecimal.valueOf(1));
        int inserted = mapper.insert(record);
        assertThat(inserted).isEqualTo(1);
    }

    @AfterEach
    void tearDown() {
        mapper.deleteByKey(TEST_SEC_ID, TEST_SEC_CODE, TEST_F002, TEST_F005);
    }

    @Test
    void testSelectByKey() {
        // 按复合主键查询
        TbPublic0007 result = mapper.selectByKey(TEST_SEC_ID, TEST_SEC_CODE, TEST_F002, TEST_F005);
        assertThat(result).isNotNull();
        assertThat(result.getObSecname0007()).isEqualTo("测试证券名称");
        assertThat(result.getObIsvalid0007()).isEqualTo("1");
        System.out.println(">>> cpdb selectByKey 结果: secName=" + result.getObSecname0007());
    }

    @Test
    void testSelectByKey_notFound() {
        TbPublic0007 result = mapper.selectByKey("NOT_EXIST", "NOT_EXIST", "NOT_EXIST", "NOT_EXIST");
        assertThat(result).isNull();
    }

    @Test
    void testSelectPage_withCondition() {
        // 按证券代码精确查询
        TbPublic0007Query query = new TbPublic0007Query();
        query.setObSeccode0007(TEST_SEC_CODE);
        List<TbPublic0007> list = mapper.selectPage(query);
        assertThat(list).isNotEmpty();
        assertThat(list).allMatch(item -> TEST_SEC_CODE.equals(item.getObSeccode0007()));
        System.out.println(">>> cpdb selectPage 按证券代码查询: 命中 " + list.size() + " 条");
    }

    @Test
    void testSelectPage_withFuzzyName() {
        // 按证券名称模糊查询
        TbPublic0007Query query = new TbPublic0007Query();
        query.setObSecname0007("测试证券");
        List<TbPublic0007> list = mapper.selectPage(query);
        assertThat(list).isNotEmpty();
        assertThat(list).allMatch(item -> item.getObSecname0007() != null
                && item.getObSecname0007().contains("测试证券"));
        System.out.println(">>> cpdb selectPage 按名称模糊查询: 命中 " + list.size() + " 条");
    }

    @Test
    void testSelectPage_withIsValid() {
        TbPublic0007Query query = new TbPublic0007Query();
        query.setObIsvalid0007("1");
        List<TbPublic0007> list = mapper.selectPage(query);
        assertThat(list).isNotEmpty();
        System.out.println(">>> cpdb selectPage 按有效标志查询: 命中 " + list.size() + " 条");
    }
}
