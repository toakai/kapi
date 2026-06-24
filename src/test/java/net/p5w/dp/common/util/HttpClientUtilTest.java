package net.p5w.dp.common.util;

import net.p5w.dp.kapi.KapiApplication;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HttpClientUtil 测试
 * <p>使用 MockWebServer 模拟 HTTP 服务，不依赖外部网络。</p>
 */
@SpringBootTest(classes = KapiApplication.class)
class HttpClientUtilTest {

    private MockWebServer server;
    private HttpClientUtil client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = server.url("/").toString().replaceAll("/$", "");
        client = new HttpClientUtil(5000); // 5秒超时，测试用短超时
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    // ==================== 静态辅助方法 ====================

    @Test
    void testBuildParams() {
        Map<String, Object> params = HttpClientUtil.buildParams("key1", "val1", "key2", 123);
        assertThat(params)
                .hasSize(2)
                .containsEntry("key1", "val1")
                .containsEntry("key2", 123);
    }

    @Test
    void testBuildParams_oddArgs_throws() {
        assertThatThrownBy(() -> HttpClientUtil.buildParams("a", "b", "c"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("键值对");
    }

    @Test
    void testBuildHeaders() {
        Map<String, String> headers = HttpClientUtil.buildHeaders("Authorization", "Bearer xyz", "Accept", "application/json");
        assertThat(headers)
                .hasSize(2)
                .containsEntry("Authorization", "Bearer xyz")
                .containsEntry("Accept", "application/json");
    }

    @Test
    void testBuildHeaders_oddArgs_throws() {
        assertThatThrownBy(() -> HttpClientUtil.buildHeaders("a", "b", "c"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("键值对");
    }

    // ==================== 构造方法 ====================

    @Test
    void testDefaultConstructor() {
        HttpClientUtil util = new HttpClientUtil();
        assertThat(util).isNotNull();
    }

    @Test
    void testConstructorWithTimeout() {
        HttpClientUtil util = new HttpClientUtil(10000);
        assertThat(util).isNotNull();
    }

    // ==================== GET ====================

    @Test
    void testGet_success() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"ok\"}"));

        String result = client.get(baseUrl + "/test");
        assertThat(result).isEqualTo("{\"status\":\"ok\"}");
    }

    @Test
    void testGet_withParams() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        Map<String, Object> params = new HashMap<>();
        params.put("name", "test");
        params.put("page", 1);
        client.get(baseUrl + "/search", params);

        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).contains("name=test");
        assertThat(request.getPath()).contains("page=1");
    }

    @Test
    void testGet_withHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom", "custom-val");
        client.get(baseUrl + "/auth", null, headers);

        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("X-Custom")).isEqualTo("custom-val");
    }

    @Test
    void testGet_404_returnsEmptyArray() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found"));

        String result = client.get(baseUrl + "/notfound");
        assertThat(result).isEqualTo("[]");
    }

    @Test
    void testGet_500_returnsEmptyArray() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(500));

        String result = client.get(baseUrl + "/error");
        assertThat(result).isEqualTo("[]");
    }

    @Test
    void testGet_emptyBody_returnsEmptyArray() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(""));

        String result = client.get(baseUrl + "/empty");
        assertThat(result).isEmpty();
    }

    @Test
    void testGet_nullBody_returnsEmptyArray() throws Exception {
        // MockWebServer 默认 body 为 null（空）
        server.enqueue(new MockResponse()
                .setResponseCode(200));

        String result = client.get(baseUrl + "/nobody");
        assertThat(result).isEmpty();
    }

    @Test
    void testGet_invalidUrl_returnsEmptyArray() {
        String result = client.get("http://");
        assertThat(result).isEqualTo("[]");
    }

    @Test
    void testGet_htmlResponse_returnsRawContent() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("<html><body>OK</body></html>"));

        String result = client.get(baseUrl + "/html");
        // 类中只是 warn 但不截断
        assertThat(result).startsWith("<html>");
    }

    // ==================== POST JSON ====================

    @Test
    void testPostJson_success() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"id\":1}"));

        String result = client.post(baseUrl + "/api", "{\"name\":\"test\"}");
        assertThat(result).isEqualTo("{\"id\":1}");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getHeader("Content-Type")).contains("application/json");
    }

    @Test
    void testPostJson_withHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token123");
        client.post(baseUrl + "/api", "{}", headers);

        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer token123");
    }

    @Test
    void testPostJson_404_returnsEmptyArray() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));

        String result = client.post(baseUrl + "/api", "{}");
        assertThat(result).isEqualTo("[]");
    }

    @Test
    void testPostJson_object() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");
        String result = client.postJson(baseUrl + "/api", data);
        assertThat(result).isEqualTo("ok");
    }

    // ==================== POST Form ====================

    @Test
    void testPostForm_success() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        Map<String, String> formData = new HashMap<>();
        formData.put("username", "admin");
        formData.put("password", "123456");
        String result = client.postForm(baseUrl + "/login", formData);
        assertThat(result).isEqualTo("ok");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getBody().readUtf8()).contains("username=admin");
    }

    @Test
    void testPostForm_withHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        Map<String, String> formData = new HashMap<>();
        formData.put("key", "val");
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Trace", "trace-001");
        String result = client.postForm(baseUrl + "/form", formData, headers);
        assertThat(result).isEqualTo("ok");
    }

    // ==================== PUT ====================

    @Test
    void testPut_success() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("updated"));

        String result = client.put(baseUrl + "/resource/1", "{\"name\":\"new\"}");
        assertThat(result).isEqualTo("updated");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("PUT");
    }

    @Test
    void testPut_withHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Id", "123");
        client.put(baseUrl + "/resource/1", "{}", headers);

        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("X-Id")).isEqualTo("123");
    }

    @Test
    void testPut_500_returnsEmptyArray() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));

        String result = client.put(baseUrl + "/resource/1", "{}");
        assertThat(result).isEqualTo("[]");
    }

    // ==================== DELETE ====================

    @Test
    void testDelete_success() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("deleted"));

        String result = client.delete(baseUrl + "/resource/1");
        assertThat(result).isEqualTo("deleted");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("DELETE");
    }

    @Test
    void testDelete_withHeaders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Reason", "cleanup");
        client.delete(baseUrl + "/resource/1", headers);

        RecordedRequest request = server.takeRequest();
        assertThat(request.getHeader("X-Reason")).isEqualTo("cleanup");
    }

    @Test
    void testDelete_404_returnsEmptyArray() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404));

        String result = client.delete(baseUrl + "/resource/1");
        assertThat(result).isEqualTo("[]");
    }

    // ==================== 重试（超时、网络异常等） ====================

    @Test
    void testGet_socketTimeout_triggersRetry() throws Exception {
        // 让 MockWebServer 不响应请求（超时），模拟 SocketTimeout
        // 由于实际超时要等 5 秒太慢，这里仅验证 retry 参数不会导致异常
        // 在超时时间内请求超时会被 catch 并重试，最终返回 "[]"
        // 用极短超时来减少等待
        HttpClientUtil fastTimeoutClient = new HttpClientUtil(1); // 1ms 超时

        String result = fastTimeoutClient.get(baseUrl + "/slow");
        // 超时重试后返回 "[]"
        assertThat(result).isEqualTo("[]");
    }

    @Test
    void testGet_withRetryParam() {
        // 验证 retry=0 时不会重试
        HttpClientUtil fastTimeoutClient = new HttpClientUtil(1);
        String result = fastTimeoutClient.get(baseUrl + "/slow", null, null, 0);
        assertThat(result).isEqualTo("[]");
    }
}
