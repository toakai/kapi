package net.p5w.dp.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Component
@Slf4j
public class HttpClientUtil {

    private final OkHttpClient client;

    private static final long DEFAULT_TIMEOUT = 30000L;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static int FORBIDDEN_COUNT = 0;
    private static final int MAX_FORBIDDEN = 3;
    private static final long FORBIDDEN_SLEEP = 60 * 60 * 1000L; // 60分钟

    /**
     * 默认构造方法，使用默认超时时间，不使用证书
     */
    public HttpClientUtil() {
        this.client = createDefaultClient(DEFAULT_TIMEOUT);
    }

    /**
     * 带超时时间的构造方法，不使用证书
     *
     * @param timeoutMillis 超时时间(毫秒)
     */
    public HttpClientUtil(long timeoutMillis) {
        this.client = createDefaultClient(timeoutMillis);
    }

    /**
     * 带超时时间和证书路径的构造方法
     *
     * @param timeoutMillis 超时时间(毫秒)
     * @param certPath      证书路径（相对于src/main/resources目录）
     */
    public HttpClientUtil(long timeoutMillis, String certPath) {
        OkHttpClient.Builder builder = createBuilderWithTimeout(timeoutMillis);

        if (StringUtils.isNotBlank(certPath)) {
            try {
                SSLSocketFactory sslSocketFactory = loadCertificate(certPath);
                X509TrustManager trustManager = getX509TrustManager();
                builder.sslSocketFactory(sslSocketFactory, trustManager)
                        .hostnameVerifier((hostname, session) -> true);
                log.info("已加载证书: {}", certPath);
            } catch (Exception e) {
                log.error("配置证书失败，将使用无证书配置", e);
            }
        } else {
            log.info("未传入证书路径，将使用无证书配置");
        }

        this.client = builder.build();
    }

    /**
     * 支持直接传入证书输入流的构造方法（用于测试环境）
     *
     * @param timeoutMillis 超时时间(毫秒)
     * @param certStream    证书输入流
     */
    public HttpClientUtil(long timeoutMillis, InputStream certStream) {
        OkHttpClient.Builder builder = createBuilderWithTimeout(timeoutMillis);

        if (certStream != null) {
            try {
                SSLSocketFactory sslSocketFactory = loadCertificateFromStream(certStream);
                X509TrustManager trustManager = getX509TrustManager();
                builder.sslSocketFactory(sslSocketFactory, trustManager)
                        .hostnameVerifier((hostname, session) -> true);
                log.info("已通过输入流加载证书");
            } catch (Exception e) {
                log.error("通过输入流配置证书失败，将使用无证书配置", e);
            }
        } else {
            log.info("未传入证书输入流，将使用无证书配置");
        }

        this.client = builder.build();
    }

    /**
     * 创建带超时设置的构建器
     */
    private OkHttpClient.Builder createBuilderWithTimeout(long timeoutMillis) {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofMillis(timeoutMillis))
                .readTimeout(Duration.ofMillis(timeoutMillis))
                .writeTimeout(Duration.ofMillis(timeoutMillis));
    }

    /**
     * 创建默认客户端（无证书配置）
     */
    private OkHttpClient createDefaultClient(long timeoutMillis) {
        return createBuilderWithTimeout(timeoutMillis).build();
    }

    // ==================== GET方法重载 ====================
    public String get(String url) {
        return get(url, null, null);
    }

    public String get(String url, Map<String, Object> params) {
        return get(url, params, null);
    }

    public String get(String url, Map<String, Object> params, Map<String, String> headers) {
        return get(url, params, headers, 1); // 只允许重试一次
    }

    public String get(String url, Map<String, Object> params, Map<String, String> headers, int retry) {
        Request.Builder requestBuilder = new Request.Builder();

        try {
            HttpUrl httpUrl = HttpUrl.parse(url);
            if (httpUrl == null) {
                log.error("URL格式错误: {}", url);
                return "[]";
            }

            HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
            if (params != null) {
                params.forEach((key, value) ->
                        urlBuilder.addQueryParameter(key, value != null ? value.toString() : ""));
            }

            requestBuilder.url(urlBuilder.build());

            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }

            try (Response response = client.newCall(requestBuilder.build()).execute()) {
                // 新增：记录正常返回的状态码
                int statusCode = response.code();

                if (statusCode == 429) {
                    log.error("HTTP 429 限流，URL: {}", url);
                    throw new RuntimeException("HTTP 429 Too Many Requests");

                    // log.error("HTTP 429 限流，休眠5分钟再继续，URL: {}", url);
                    //
                    // sleepQuietly(5 * 60 * 1000);
                    //
                    // return "[]";
                }

                // 403处理
                if (statusCode == 403) {

                    log.error("GET请求返回403，URL: {}", url);
                    throw new RuntimeException("HTTP 403 Forbidden");

                    // FORBIDDEN_COUNT++;
                    //
                    // log.error("GET请求返回403，第{}次，URL: {}", FORBIDDEN_COUNT, url);
                    //
                    // if (FORBIDDEN_COUNT >= MAX_FORBIDDEN) {
                    //     log.error("连续{}次403，暂停30分钟后继续爬取...", MAX_FORBIDDEN);
                    //
                    //     sleepQuietly(FORBIDDEN_SLEEP);
                    //
                    //     FORBIDDEN_COUNT = 0; // 清零
                    // }
                    //
                    // return "[]";
                }

                // 请求成功则清零
                // FORBIDDEN_COUNT = 0;

                if (response.isSuccessful()) {
                    log.info("GET请求成功，HTTP状态码：{}, URL: {}", statusCode, url);
                } else {
                    log.warn("GET请求失败，HTTP状态码：{}, URL: {}", statusCode, url);
                    return "[]";
                }

                ResponseBody body = response.body();
                if (body == null) {
                    log.warn("响应体为空, URL: {}", url);
                    return "[]";
                }

                String result = body.string().trim();

                log.debug("请求URL：{}", url);
                log.debug("请求参数：{}", params);

                if (StringUtils.isNotBlank(result) && result.startsWith("<")) {
                    log.warn("返回数据是HTML，URL: {}, 长度={}, 内容前50字符={}",
                            url, result.length(), result.substring(0, Math.min(50, result.length())));
                    // return "[]";
                }

                return result;
            }
        } catch (SocketTimeoutException e) {// ✅ 单独捕获超时异常
            if (retry > 0) {
                log.warn("GET请求超时(Read timed out)，准备3分钟后重试一次，URL: {}", url);

                sleepQuietly(3 * 60 * 1000);

                return get(url, params, headers, retry - 1);
            }

            log.error("GET请求超时，重试后仍失败，URL: {}", url);
            return "[]";

        } catch (ProtocolException e) {

            if (retry > 0) {
                log.warn("GET请求异常(ProtocolException: unexpected end of stream)，准备1分钟后重试，URL: {}", url);

                sleepQuietly(60 * 1000);

                return get(url, params, headers, retry - 1);
            }

            log.error("ProtocolException重试失败，URL: {}", url);
            return "[]";

        } catch (UnknownHostException e) {// ✅ 单独捕获UnknownHost异常
            if (retry > 0) {
                log.warn("GET请求异常(UnknownHost)，准备5分钟后重试一次，URL: {}", url);

                sleepQuietly(5 * 60 * 1000);

                return get(url, params, headers, retry - 1);
            }

            log.error("GET请求异常(UnknownHost)，重试后仍失败，URL: {}", url);
            return "[]";

        } catch (IOException e) {// ✅ 其他 IO 异常
            log.error("HTTP GET请求异常, URL: {}, 异常信息: {}", url, e.getMessage(), e);
            throw new RuntimeException("HTTP请求异常", e);
        }
    }

    // ==================== POST JSON方法重载 ====================
    public String post(String url, String jsonBody) {
        return post(url, jsonBody, null);
    }

    public String post(String url, String jsonBody, Map<String, String> headers) {
        try {
            RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(body);

            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }

            return executeRequest(requestBuilder, "POST", url);

        } catch (Exception e) {
            log.error("HTTP POST请求异常, URL: {}, 异常信息: {}", url, e.getMessage(), e);
            return "[]";
        }
    }

    public String postJson(String url, Object data) {
        return postJson(url, data, null);
    }

    public String postJson(String url, Object data, Map<String, String> headers) {
        try {
            String jsonBody = new ObjectMapper().writeValueAsString(data);
            return post(url, jsonBody, headers);
        } catch (Exception e) {
            log.error("对象转JSON异常, URL: {}", url, e);
            return "[]";
        }
    }

    // ==================== 表单POST方法重载 ====================
    public String postForm(String url, Map<String, String> formData) {
        return postForm(url, formData, null);
    }

    public String postForm(String url, Map<String, String> formData, Map<String, String> headers) {
        try {
            FormBody.Builder formBuilder = new FormBody.Builder();
            if (formData != null) {
                formData.forEach(formBuilder::add);
            }

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(formBuilder.build());

            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }

            return executeRequest(requestBuilder, "POST FORM", url);

        } catch (Exception e) {
            log.error("表单POST请求异常, URL: {}", url, e);
            return "[]";
        }
    }

    // ==================== 其他HTTP方法 ====================
    public String put(String url, String jsonBody) {
        return put(url, jsonBody, null);
    }

    public String put(String url, String jsonBody, Map<String, String> headers) {
        try {
            RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .put(body);

            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }

            return executeRequest(requestBuilder, "PUT", url);

        } catch (Exception e) {
            log.error("HTTP PUT请求异常, URL: {}", url, e);
            return "[]";
        }
    }

    public String delete(String url) {
        return delete(url, null);
    }

    public String delete(String url, Map<String, String> headers) {
        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .delete();

            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }

            return executeRequest(requestBuilder, "DELETE", url);

        } catch (Exception e) {
            log.error("HTTP DELETE请求异常, URL: {}", url, e);
            return "[]";
        }
    }

    // ==================== 私有方法 ====================
    private String executeRequest(Request.Builder requestBuilder, String method, String url) throws IOException {
        try (Response response = client.newCall(requestBuilder.build()).execute()) {
            int statusCode = response.code();
            // 新增：记录正常返回的状态码
            if (response.isSuccessful()) {
                log.info("{}请求成功，HTTP状态码：{}, URL: {}", method, statusCode, url);
            } else {
                log.warn("{}请求失败，HTTP状态码：{}, URL: {}", method, statusCode, url);
                return "[]";
            }

            ResponseBody body = response.body();
            if (body == null) {
                log.warn("{}响应体为空, URL: {}", method, url);
                return "[]";
            }

            String result = body.string().trim();

            log.debug("{}响应结果长度：{}", method, result.length());

            if (StringUtils.isNotBlank(result) && result.startsWith("<")) {
                log.warn("{}返回数据是HTML，URL: {}, 长度={}", method, url, result.length());
                // return "[]";
            }

            return result;
        }
    }

    // private SSLSocketFactory loadCertificate(String certPath) throws Exception {
    //     InputStream is = getClass().getClassLoader().getResourceAsStream(certPath);
    //     if (is != null) {
    //         URL resourceUrl = getClass().getClassLoader().getResource(certPath);
    //         if (resourceUrl != null) {
    //             log.info("成功定位证书文件: {}", resourceUrl.getPath());
    //         }
    //     } else {
    //         log.error("预期证书路径: {}",
    //                 getClass().getClassLoader().getResource("").getPath() + certPath);
    //     }
    //     if (is == null) {
    //         log.error("找不到证书文件: {}", certPath);
    //         log.error("请确保证书文件位于: src/main/resources/{}", certPath);
    //         throw new IOException("证书文件不存在: " + certPath);
    //     }
    //
    //     log.info("成功加载证书: {}", certPath);
    //     KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    //     keyStore.load(null, null);
    //
    //     java.security.cert.Certificate certificate = java.security.cert.CertificateFactory
    //             .getInstance("X.509")
    //             .generateCertificate(is);
    //     keyStore.setCertificateEntry("custom-cert", certificate);
    //
    //     TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
    //             TrustManagerFactory.getDefaultAlgorithm());
    //     trustManagerFactory.init(keyStore);
    //
    //     SSLContext sslContext = SSLContext.getInstance("TLS");
    //     sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
    //
    //     return sslContext.getSocketFactory();
    // }

    // private SSLSocketFactory loadCertificate(String certPath) throws Exception {
    //     // 优先从外部resources目录加载（IDEA运行和生产环境通用）
    //     String externalResourcePath = System.getProperty("user.dir") + "/target/resources/" + certPath;
    //     File externalCertFile = new File(externalResourcePath);
    //     InputStream is = null;
    //
    //     // 1. 尝试加载外部资源（IDEA运行时）
    //     if (externalCertFile.exists()) {
    //         log.info("从外部资源目录加载证书: {}", externalResourcePath);
    //         is = new FileInputStream(externalCertFile);
    //     }
    //     // 2. 尝试从类路径加载（兼容打包后的备用方案）
    //     else {
    //         log.info("尝试从类路径加载证书: {}", certPath);
    //         is = getClass().getClassLoader().getResourceAsStream(certPath);
    //     }
    //
    //     // 3. 验证证书是否存在
    //     if (is == null) {
    //         log.error("找不到证书文件！");
    //         log.error("外部路径: {}", externalResourcePath);
    //         log.error("类路径: {}", certPath);
    //         throw new IOException("证书文件不存在: " + certPath);
    //     }
    //
    //     // 后续证书加载逻辑不变...
    //     log.info("成功加载证书: {}", certPath);
    //     KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    //     keyStore.load(null, null);
    //
    //     java.security.cert.Certificate certificate = java.security.cert.CertificateFactory
    //             .getInstance("X.509")
    //             .generateCertificate(is);
    //     keyStore.setCertificateEntry("custom-cert", certificate);
    //
    //     TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
    //             TrustManagerFactory.getDefaultAlgorithm());
    //     trustManagerFactory.init(keyStore);
    //
    //     SSLContext sslContext = SSLContext.getInstance("TLS");
    //     sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
    //
    //     return sslContext.getSocketFactory();
    // }

    // private SSLSocketFactory loadCertificate(String certPath) throws Exception {
    //     // 1. 优先加载生产环境：JAR同级的 ./resources 目录（与启动命令中的资源路径对齐）
    //     // 可通过启动参数 -Dexternal.resource.path 显式指定，默认是 ./resources
    //     String externalResourceRoot = System.getProperty("external.resource.path", "./resources");
    //     File externalCertFile = new File(externalResourceRoot, certPath); // 拼接外部证书路径
    //
    //     // 检查外部证书是否存在（生产环境核心逻辑）
    //     if (externalCertFile.exists() && externalCertFile.isFile()) {
    //         String absolutePath = externalCertFile.getAbsolutePath();
    //         log.info("✅ 从生产环境外部资源目录加载证书: {}", absolutePath);
    //         try (InputStream is = new FileInputStream(externalCertFile)) {
    //             return loadCertificateFromStream(is); // 复用已有流加载逻辑
    //         }
    //     }
    //
    //     // 2. 适配开发环境：从 target/resources 目录加载（保留开发便捷性）
    //     String devResourceRoot = System.getProperty("user.dir") + "/target/resources";
    //     File devCertFile = new File(devResourceRoot, certPath);
    //     if (devCertFile.exists() && devCertFile.isFile()) {
    //         log.info("🔧 从开发环境外部资源目录加载证书: {}", devCertFile.getAbsolutePath());
    //         try (InputStream is = new FileInputStream(devCertFile)) {
    //             return loadCertificateFromStream(is);
    //         }
    //     }
    //
    //     // 3. 最后 fallback：从classpath加载（备用，防止极端情况）
    //     InputStream classpathIs = getClass().getClassLoader().getResourceAsStream(certPath);
    //     if (classpathIs != null) {
    //         log.info("📦 从classpath加载证书: {}", certPath);
    //         return loadCertificateFromStream(classpathIs);
    //     }
    //
    //     // 4. 所有路径都找不到，抛出明确错误（方便排查）
    //     throw new IOException(
    //             "❌ 找不到证书文件！请检查以下路径：\n" +
    //                     "1. 生产环境外部路径：" + externalCertFile.getAbsolutePath() + "\n" +
    //                     "2. 开发环境路径：" + devCertFile.getAbsolutePath() + "\n" +
    //                     "3. classpath路径：" + certPath + "\n" +
    //                     "提示：确保启动命令在JAR与resources同级目录执行，且cert目录存在于resources下"
    //     );
    // }

    private SSLSocketFactory loadCertificate(String certPath) throws Exception {
        // 新增：1. 优先加载生产环境：JAR同级的 ./resources 目录（解决打包后找不到的问题）
        // 获取当前工作目录（即执行java -jar命令时的目录，需确保在JAR所在目录执行）
        String currentWorkDir = System.getProperty("user.dir");
        String prodResourceRoot = currentWorkDir + File.separator + "resources";
        File prodCertFile = new File(prodResourceRoot, certPath);

        if (prodCertFile.exists() && prodCertFile.isFile()) {
            String absolutePath = prodCertFile.getAbsolutePath();
            log.info("✅ 从生产环境加载证书: {}", absolutePath);
            try (InputStream is = new FileInputStream(prodCertFile)) {
                return loadCertificateFromStream(is);
            }
        }

        // 原有逻辑：2. 加载开发环境：target/resources 目录
        String devResourceRoot = System.getProperty("user.dir") + "/target/resources";
        File devCertFile = new File(devResourceRoot, certPath);
        if (devCertFile.exists() && devCertFile.isFile()) {
            log.info("🔧 从开发环境加载证书: {}", devCertFile.getAbsolutePath());
            try (InputStream is = new FileInputStream(devCertFile)) {
                return loadCertificateFromStream(is);
            }
        }

        // 原有逻辑：3. Fallback：从classpath加载（备用）
        InputStream classpathIs = getClass().getClassLoader().getResourceAsStream(certPath);
        if (classpathIs != null) {
            log.info("📦 从classpath加载证书: {}", certPath);
            return loadCertificateFromStream(classpathIs);
        }

        // 原有逻辑：4. 所有路径找不到，抛出错误（补充生产环境路径提示）
        throw new IOException(
                "❌ 找不到证书文件！请检查以下路径：\n" +
                        "1. 生产环境（JAR同级）：" + prodCertFile.getAbsolutePath() + "\n" +  // 新增生产路径提示
                        "2. 开发环境：" + devCertFile.getAbsolutePath() + "\n" +
                        "3. classpath路径：" + certPath + "\n" +
                        "提示：生产环境需在JAR所在目录执行命令，确保resources与JAR同级"
        );
    }

    private SSLSocketFactory loadCertificateFromStream(InputStream is) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);

        java.security.cert.Certificate certificate = java.security.cert.CertificateFactory
                .getInstance("X.509")
                .generateCertificate(is);
        keyStore.setCertificateEntry("custom-cert", certificate);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

        return sslContext.getSocketFactory();
    }

    private X509TrustManager getX509TrustManager() throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new Exception("未找到X509TrustManager");
    }

    // ==================== 便捷的构建方法 ====================
    public static Map<String, Object> buildParams(Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("参数必须是键值对形式");
        }

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            params.put(keyValues[i].toString(), keyValues[i + 1]);
        }
        return params;
    }

    public static Map<String, String> buildHeaders(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("参数必须是键值对形式");
        }

        Map<String, String> headers = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            headers.put(keyValues[i], keyValues[i + 1]);
        }
        return headers;
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        // 测试无证书请求
        try {
            HttpClientUtil normalClient = new HttpClientUtil();
            String result1 = normalClient.get("https://httpbin.org/get");
            System.out.println("无证书请求结果: " + result1);
        } catch (Exception e) {
            log.error("无证书客户端测试失败", e);
        }

        // 测试带证书的请求
        try {
            HttpClientUtil certClient = new HttpClientUtil(30000, "cert/www.csrc.gov.cn.pem");
            String api = "https://www.csrc.gov.cn/searchList/a1a078ee0bc54721ab6b148884c784a8?_isAgg=true&_isJson=true&_pageSize=18&_template=index&_rangeTimeGte=&_channelName=&page=1";
            String result2 = certClient.get(api);
            System.out.println("带证书请求结果: " + result2);
        } catch (Exception e) {
            log.error("带证书客户端测试失败", e);
        }
    }
}
