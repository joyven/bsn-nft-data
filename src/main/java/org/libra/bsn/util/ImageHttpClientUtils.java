package org.libra.bsn.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.libra.bsn.dao.NftInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

/**
 * @author xianhu.wang
 * @date 2022年10月18日 5:19 下午
 */
public class ImageHttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageHttpClientUtils.class);

    /**
     * 整个连接池连接的最大值
     */
    private static final int PM_MAX_CONN = 1000;

    /**
     * 路由的默认最大连接
     */
    private static final int PM_MAX_PER_ROUTE = 200;

    /**
     * 客户端与服务器建立连接的超时时间
     */
    private static final int CONNECT_TIMEOUT = 1000;

    /**
     * 客户端从连接池中获取连接的超时时间
     */
    private static final int CONNECT_REQUEST_TIMEOUT = 1000;


    private static final int SO_TIMEOUT = 5000;

    private static final long MAX_IDLE_TIMEOUT = 60;


    private static final int IDLE_TIMEOUT = 60;

    private static CloseableHttpClient httpClient;

    private static PoolingHttpClientConnectionManager POOL_CONN_MANAGER = null;

    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";

    static {
        try {
            //socketConfig
            SocketConfig socketConfig = SocketConfig.custom()
                    //接收数据的等待超时时间
                    .setSoTimeout(SO_TIMEOUT).build();

            // 配置同时支持 HTTP 和 HTPPS
            SSLContext sslcontext = null;
            try {
                sslcontext = createIgnoreVerifySSL();
            } catch (Exception e) {
                logger.warn("createIgnoreVerifySSL error", e);
            }

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"},
                    null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslsf).build();

            // 初始化连接管理器
            POOL_CONN_MANAGER = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 同时最多连接数
            POOL_CONN_MANAGER.setMaxTotal(PM_MAX_CONN);
            // 设置最大路由
            POOL_CONN_MANAGER.setDefaultMaxPerRoute(PM_MAX_PER_ROUTE);
            POOL_CONN_MANAGER.setDefaultSocketConfig(socketConfig);
            POOL_CONN_MANAGER.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.SECONDS);
            POOL_CONN_MANAGER.closeExpiredConnections();

            httpClient = getConnection();
        } catch (Exception e) {
            logger.error("init httpclient error={}", e.getMessage(), e);
        }

    }

    private static CloseableHttpClient getConnection() {

        return HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(POOL_CONN_MANAGER)
                .setDefaultRequestConfig(buildRequestConfig(SO_TIMEOUT))
                // 定期回收空闲连接
                .evictExpiredConnections()
                // 定期回收空闲连接
                .evictIdleConnections(MAX_IDLE_TIMEOUT, TimeUnit.SECONDS)
                /**
                 * 默认的话，是从response里头读timeout参数的，没有读到则设置为-1，这个代表无穷，这样设置是有点问题了，
                 * 如果是https链接的话，则可能会经常报 Read Time out 因此重新设定时间
                 * HTTP规范没有确定一个持久连接可能或应该保持活动多长时间。
                 * 一些HTTP服务器使用非标准的头部信息Keep-Alive来告诉客户端它们想在服务器端保持连接活动的周期秒数。
                 * 如果这个信息可用，HttClient就会利用这个它。如果头部信息Keep-Alive在响应中不存在，HttpClient假设连接无限期的保持活动。
                 * 然而许多现实中的HTTP服务器配置了在特定不活动周期之后丢掉持久连接来保存系统资源，往往这是不通知客户端的。
                 */
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
                    @Override
                    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                        long keepAliveDuration = super.getKeepAliveDuration(response, context);
                        if (keepAliveDuration == -1) {
                            keepAliveDuration = 5000;
                        }
                        return keepAliveDuration;
                    }
                })
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
    }

    /**
     * default request config
     *
     * @param soTimeout
     * @return
     */
    public static RequestConfig buildRequestConfig(int soTimeout) {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(soTimeout)
                .build();
    }

    /**
     * customize HttpGet socketTimeout
     *
     * @param get
     * @param socketTimeout
     */
    private static void customizeConfig(HttpGet get, int socketTimeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(socketTimeout)
                .build();
        get.setConfig(requestConfig);
    }


    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLSv1.2");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    public static void main(String[] args) {
        imageDownload("11", "https://menghuanart.cn/uploads/cover/416b3d1e7a278a23124ee4609826e189.png", 1500);
    }

    /**
     * 图像下载专
     *
     * @param url
     * @param socketTimeout
     */
    public static void imageDownload(String txHash, String url, int socketTimeout) {

        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse httpResponse = null;
        try {
            httpGet.addHeader("User-Agent", DEFAULT_USER_AGENT);
            customizeConfig(httpGet, socketTimeout);
            httpResponse = httpClient.execute(httpGet);

            if (httpResponse.containsHeader("Content-Type")) {
                Header contentType = httpResponse.getFirstHeader("Content-Type");
                String type = contentType.getValue();
                if (!StringUtils.containsIgnoreCase(type, "application") && !StringUtils.startsWithIgnoreCase(type, "image")) {
                    logger.warn("[download image error] 图像源异常 txHash={} ", txHash);
                    return;
                }
            }

            InputStream content = httpResponse.getEntity().getContent();
            String featureValue = PHash.getFeatureValue(content);
            if (StringUtils.isBlank(featureValue)) {
                logger.warn("txHas{} get image  featureValue is null", txHash);
                return;
            }
            // 更新 txHash
            NftInfoDao nftInfoDao = SpringContextUtil.getBean("nftInfoDao", NftInfoDao.class);
            if (nftInfoDao != null) {
                nftInfoDao.updateNftInfo(txHash, featureValue);
                logger.info("txHas{} update nftInfo  success", txHash);
            }
            logger.info("txHas{} update nftInfo  fail", txHash);
        } catch (Exception e) {
            logger.warn("[download image error] err={} url={} txHash={}", e.getMessage(), url, txHash);
        } finally {
            if (null != httpResponse) {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
                try {
                    httpResponse.close();
                } catch (Exception e) {
                    logger.error("httpResponse close  error={}", e.getMessage());
                }
            }
        }
    }
}
