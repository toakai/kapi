package net.p5w.dp.module.spriderx;

import net.p5w.dp.kapi.KapiApplication;
import net.p5w.dp.module.spriderx.entity.CompanyBaseinfo;
import net.p5w.dp.module.spriderx.mapper.CompanyBaseinfoMapper;
import net.p5w.dp.module.spriderx.query.CompanyBaseinfoQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CompanyBaseinfoMapper 增/改/查测试（spriderx 主库）
 */
@SpringBootTest(classes = KapiApplication.class)
class CompanyBaseinfoMapperTest {

    @Autowired
    private CompanyBaseinfoMapper mapper;

    /** 测试数据的唯一标识 */
    private static final String UNIQUE_TAG = UUID.randomUUID().toString().substring(0, 8);
    private static final String TEST_COMPANY_NAME = "Test_Spriderx_" + UNIQUE_TAG;
    private static final String TEST_COMPANY_CODE = "TSC" + UNIQUE_TAG;
    private static final String UPDATED_COMPANY_NAME = "Test_Spriderx_Updated_" + UNIQUE_TAG;

    /** 插入后由 useGeneratedKeys 回填 */
    private Integer testId;

    @BeforeEach
    void setUp() {
        // 先清理残留数据
        CompanyBaseinfoQuery cleanQuery = new CompanyBaseinfoQuery();
        cleanQuery.setCompanyCode(TEST_COMPANY_CODE);
        List<CompanyBaseinfo> existing = mapper.selectPage(cleanQuery);
        for (CompanyBaseinfo e : existing) {
            mapper.deleteById(e.getId());
        }
    }

    @AfterEach
    void tearDown() {
        if (testId != null) {
            mapper.deleteById(testId);
        }
    }

    @Test
    void testInsert() {
        // 插入一条记录
        CompanyBaseinfo record = new CompanyBaseinfo();
        record.setCompanyCode(TEST_COMPANY_CODE);
        record.setCompanyName(TEST_COMPANY_NAME);
        record.setJurisdiction("中国");
        record.setPlatformName("测试平台");
        record.setPid("TEST_PID");

        int inserted = mapper.insert(record);
        assertThat(inserted).isEqualTo(1);
        assertThat(record.getId()).isNotNull(); // useGeneratedKeys 生效
        testId = record.getId();
        System.out.println(">>> spriderx insert 成功: id=" + testId);
    }

    @Test
    void testSelectById() {
        // 先插入
        CompanyBaseinfo record = buildTestRecord();
        mapper.insert(record);
        testId = record.getId();

        // 按 ID 查询
        CompanyBaseinfo result = mapper.selectById(testId);
        assertThat(result).isNotNull();
        assertThat(result.getCompanyName()).isEqualTo(TEST_COMPANY_NAME);
        assertThat(result.getCompanyCode()).isEqualTo(TEST_COMPANY_CODE);
        System.out.println(">>> spriderx selectById 结果: name=" + result.getCompanyName());
    }

    @Test
    void testUpdateById() {
        // 先插入
        CompanyBaseinfo record = buildTestRecord();
        mapper.insert(record);
        testId = record.getId();

        // 更新名称和管辖区
        record.setCompanyName(UPDATED_COMPANY_NAME);
        record.setJurisdiction("上海市");
        int updated = mapper.updateById(record);
        assertThat(updated).isEqualTo(1);

        // 验证更新
        CompanyBaseinfo result = mapper.selectById(testId);
        assertThat(result.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(result.getJurisdiction()).isEqualTo("上海市");
        System.out.println(">>> spriderx updateById 成功: name=" + result.getCompanyName());
    }

    @Test
    void testSelectPage() {
        // 先插入测试数据
        CompanyBaseinfo record = buildTestRecord();
        mapper.insert(record);
        testId = record.getId();

        // 按公司代码精确查询
        CompanyBaseinfoQuery query = new CompanyBaseinfoQuery();
        query.setCompanyCode(TEST_COMPANY_CODE);
        List<CompanyBaseinfo> list = mapper.selectPage(query);
        assertThat(list).isNotEmpty();
        assertThat(list).allMatch(item -> TEST_COMPANY_CODE.equals(item.getCompanyCode()));
        System.out.println(">>> spriderx selectPage 按公司代码查询: 命中 " + list.size() + " 条");
    }

    @Test
    void testSelectPage_withFuzzyName() {
        // 先插入
        CompanyBaseinfo record = buildTestRecord();
        mapper.insert(record);
        testId = record.getId();

        // 按公司名称模糊查询
        CompanyBaseinfoQuery query = new CompanyBaseinfoQuery();
        query.setCompanyName(TEST_COMPANY_NAME);
        List<CompanyBaseinfo> list = mapper.selectPage(query);
        assertThat(list).isNotEmpty();
        assertThat(list).allMatch(item -> item.getCompanyName() != null
                && item.getCompanyName().contains(TEST_COMPANY_NAME));
        System.out.println(">>> spriderx selectPage 按名称模糊查询: 命中 " + list.size() + " 条");
    }

    /** 构建测试记录 */
    private CompanyBaseinfo buildTestRecord() {
        CompanyBaseinfo record = new CompanyBaseinfo();
        record.setCompanyCode(TEST_COMPANY_CODE);
        record.setCompanyName(TEST_COMPANY_NAME);
        record.setJurisdiction("中国");
        record.setPlatformName("测试平台");
        record.setPid("TEST_PID");
        return record;
    }
}
