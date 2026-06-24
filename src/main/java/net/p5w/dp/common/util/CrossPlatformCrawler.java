package net.p5w.dp.common.util;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CrossPlatformCrawler {
    private static final String DEFAULT_WIN_CHROME_PATH = "D:\\Program Files\\Google\\Chrome\\App\\chrome1.exe";

    @Value("${chrome.path:${chrome.path.win:D:\\Program Files\\Google\\Chrome\\App\\chrome2.exe}}")
    private String chromePath;

    public CrossPlatformCrawler() {
    }

    public CrossPlatformCrawler(String chromePath) {
        this.chromePath = chromePath != null && !chromePath.trim().isEmpty()
                ? chromePath : DEFAULT_WIN_CHROME_PATH;
    }

    /**
     * 从资源目录获取chromedriver路径（与cert目录逻辑一致：生产JAR同级>开发target>classpath）
     */
    private String getDriverPath() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String arch = System.getProperty("os.arch").toLowerCase();
            log.debug("当前系统: OS={}, 架构={}", os, arch);

            // 1. 构建驱动相对路径（与resources/drivers下的实际结构一致）
            String driverResourcePath;
            if (os.contains("win")) {
                driverResourcePath = "drivers/chromedriver-win64/chromedriver.exe";
            } else if (os.contains("mac")) {
                if (arch.contains("x86") || arch.contains("amd64")) {
                    driverResourcePath = "drivers/chromedriver-mac-x64/chromedriver";
                } else if (arch.contains("aarch64") || arch.contains("arm")) {
                    driverResourcePath = "drivers/chromedriver-mac-arm64/chromedriver";
                } else {
                    throw new RuntimeException("不支持的Mac架构: " + arch);
                }
            } else if (os.contains("nux") || os.contains("nix")) {
                driverResourcePath = "drivers/chromedriver-linux64/chromedriver";
            } else {
                throw new RuntimeException("不支持的操作系统: " + os);
            }

            // 2. 优先加载生产环境：JAR同级的 ./resources 目录（与cert逻辑一致）
            String currentWorkDir = System.getProperty("user.dir"); // 执行java -jar的目录
            String prodResourceRoot = currentWorkDir + File.separator + "resources";
            File prodDriverFile = new File(prodResourceRoot, driverResourcePath.replace("/", File.separator));
            if (prodDriverFile.exists() && prodDriverFile.isFile()) {
                String absolutePath = prodDriverFile.getAbsolutePath();
                log.info("✅ 从生产环境（JAR同级）加载驱动: {}", absolutePath);
                // 确保Linux/Mac下有执行权限
                setExecutablePermissionIfNeeded(absolutePath, os);
                return absolutePath;
            }

            // 3. 加载开发环境：target/resources 目录
            String devResourceRoot = System.getProperty("user.dir") + File.separator + "target" + File.separator + "resources";
            File devDriverFile = new File(devResourceRoot, driverResourcePath.replace("/", File.separator));
            if (devDriverFile.exists() && devDriverFile.isFile()) {
                String absolutePath = devDriverFile.getAbsolutePath();
                log.info("🔧 从开发环境加载驱动: {}", absolutePath);
                // 确保Linux/Mac下有执行权限
                setExecutablePermissionIfNeeded(absolutePath, os);
                return absolutePath;
            }

            // 4. Fallback：从classpath加载（备用）
            ClassLoader classLoader = getClass().getClassLoader();
            URL resourceUrl = classLoader.getResource(driverResourcePath);
            if (resourceUrl == null) {
                // 详细打印所有可能路径，方便排查
                String prodPath = prodDriverFile.getAbsolutePath();
                String devPath = devDriverFile.getAbsolutePath();
                String classpathPath = "classpath:" + driverResourcePath;
                throw new RuntimeException("未找到驱动文件！\n"
                        + "1. 生产环境（JAR同级）：" + prodPath + "\n"
                        + "2. 开发环境（target）：" + devPath + "\n"
                        + "3. classpath路径：" + classpathPath + "\n"
                        + "请检查resources/drivers目录下是否存在该文件");
            }

            // 处理classpath路径的解码和系统适配
            String decodedPath = URLDecoder.decode(resourceUrl.getPath(), "UTF-8");
            // 修复Windows路径开头的斜杠问题
            if (os.contains("win") && decodedPath.startsWith("/")) {
                decodedPath = decodedPath.substring(1);
            }

            // 验证classpath路径的文件存在性
            File driverFile = new File(decodedPath);
            if (!driverFile.exists() || !driverFile.isFile()) {
                throw new RuntimeException("classpath驱动文件不存在: " + decodedPath);
            }

            // 确保Linux/Mac下有执行权限
            setExecutablePermissionIfNeeded(decodedPath, os);

            log.info("📦 从classpath加载驱动: {}", decodedPath);
            return decodedPath;

        } catch (Exception e) {
            log.error("获取驱动路径失败", e);
            throw new RuntimeException("获取chromedriver路径失败", e);
        }
    }

    /**
     * 工具方法：为Linux/Mac系统的驱动文件添加执行权限（提取重复逻辑）
     */
    private void setExecutablePermissionIfNeeded(String driverPath, String os) {
        if ((os.contains("mac") || os.contains("nux") || os.contains("nix"))) {
            try {
                if (!Files.isExecutable(Paths.get(driverPath))) {
                    Files.setPosixFilePermissions(Paths.get(driverPath),
                            java.nio.file.attribute.PosixFilePermissions.fromString("rwxr-xr-x"));
                    log.debug("已为驱动文件添加执行权限: {}", driverPath);
                }
            } catch (IOException e) {
                log.warn("为驱动文件添加执行权限失败，可能导致无法运行: {}", driverPath, e);
            }
        }
    }

    private String getHostOS() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (os.contains("win")) {
            return "win64";
        } else if (os.contains("mac")) {
            if (arch.contains("x86") || arch.contains("amd64")) {
                return "mac-x64";
            } else if (arch.contains("aarch64") || arch.contains("arm")) {
                return "mac-arm64";
            } else {
                throw new RuntimeException("Unsupported Mac architecture: " + arch);
            }
        } else if (os.contains("nux") || os.contains("nix")) {
            return "linux64";
        } else {
            throw new RuntimeException("Unsupported OS: " + os);
        }
    }

    private int getHttpStatusCode(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.connect();
            int code = conn.getResponseCode();
            conn.disconnect();
            return code;
        } catch (Exception e) {
            log.error("获取HTTP状态码失败", e);
            return -1;
        }
    }

    /**
     * 爬取网页数据并返回提取的内容
     */
    public String crawl(String url) {
        ensureChromePathInitialized();

        int statusCode = getHttpStatusCode(url);
        log.info("HTTP Status Code: " + statusCode);

        if (statusCode != 200) {
            log.error("请求失败，状态码: " + statusCode);
            return null;
        }

        // 设置驱动路径
        System.setProperty("webdriver.chrome.driver", getDriverPath());
        ChromeOptions options = createChromeOptions();
        WebDriver driver = new ChromeDriver(options);
        List<String> result = new ArrayList<>();

        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));

            driver.get(url);
            log.info("成功加载页面: " + url);

            // 获取渲染后的 HTML
            String pageSource = driver.getPageSource();
            log.info("成功提取页面内容，长度: {}", pageSource.length());
            return pageSource;

        } catch (Exception e) {
            log.error("爬取过程中发生异常", e);
            return null;
        } finally {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("关闭浏览器时发生异常", e);
            }
        }
    }

    /**
     * 创建Chrome浏览器配置
     */
    private ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        String os = getHostOS();
        log.debug("OS: " + os + ", Chrome Path: " + chromePath);

        // 仅当路径存在时才设置Chrome二进制文件路径
        if (chromePath != null && !chromePath.trim().isEmpty()) {
            File chromeFile = new File(chromePath);
            if (chromeFile.exists() && chromeFile.isFile()) {
                options.setBinary(chromePath);
                log.debug("已设置Chrome二进制路径: {}", chromePath);
            } else {
                log.warn("Chrome路径不存在或不是文件: {}", chromePath);
            }
        }

        // 无头模式配置（适配各系统）
        options.addArguments("--headless=new"); // 推荐使用new模式，兼容新版Chrome
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }

    private void ensureChromePathInitialized() {
        if (chromePath == null || chromePath.trim().isEmpty()) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                chromePath = DEFAULT_WIN_CHROME_PATH;
                log.info("使用Windows默认Chrome路径: " + chromePath);
            } else if (os.contains("mac")) {
                // Mac默认Chrome路径
                chromePath = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
                log.info("使用Mac默认Chrome路径: " + chromePath);
            } else if (os.contains("linux")) {
                // Linux默认Chrome路径
                chromePath = "/usr/bin/google-chrome";
                log.info("使用Linux默认Chrome路径: " + chromePath);
            }
        }
    }

    public static void main(String[] args) {
        String url = "https://www.baidu.com/s?wd=selenium";
        CrossPlatformCrawler crawler = new CrossPlatformCrawler();
        String pageSource = crawler.crawl(url);
        System.out.println("pageSource:\n" + pageSource);

        // Jsoup 解析并提取内容
        if (pageSource != null) {
            Document doc = Jsoup.parse(pageSource);
            Elements elements = doc.select("h3 a");
            for (Element e : elements) {
                System.out.println("提取内容: " + e.text());
            }
        }
    }
}