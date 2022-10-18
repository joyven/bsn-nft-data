package org.libra.util;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * httpClient  4.5.5
 *
 * @author xianhu.wang
 * @date 2022/6/6 10:53 上午
 */
public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final int PM_MAX_CONN = 100;

    private static final int PM_MAX_PER_ROUTE = 50;

    private static final int CONNECT_TIMEOUT = 500;

    private static final int CONNECT_REQUEST_TIMEOUT = 500;

    private static final int SO_TIMEOUT = 500;

    private static final long MAX_IDLE_TIMEOUT = 60;

    private static final int SO_LINGER = 60;

    private static final int IDLE_TIMEOUT = 60;

    private static CloseableHttpClient httpClient;

    private static final int DEFAULT_SO_TIMEOUT = 200;

    // 池化管理

    private static PoolingHttpClientConnectionManager POOL_CONN_MANAGER = null;

    /**
     * 获取CloseableHttpClient
     *
     * @param soTimeout
     * @return
     */
    static {

        try {
            MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200).build();

            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(UTF_8)
                    .setMessageConstraints(messageConstraints)
                    .build();

            //socketConfig
            SocketConfig socketConfig2 = SocketConfig.custom()
                    .setSoLinger(SO_LINGER)
                    .setSoReuseAddress(true)
                    .setTcpNoDelay(true)
                    .setSoKeepAlive(true)
                    .setSoTimeout(DEFAULT_SO_TIMEOUT).build();


            SSLContextBuilder builder = new SSLContextBuilder();

            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

            // 配置同时支持 HTTP 和 HTPPS

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf).build();

            // 初始化连接管理器
            POOL_CONN_MANAGER = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 同时最多连接数
            POOL_CONN_MANAGER.setMaxTotal(PM_MAX_CONN);
            // 设置最大路由
            POOL_CONN_MANAGER.setDefaultMaxPerRoute(PM_MAX_PER_ROUTE);
            POOL_CONN_MANAGER.setDefaultSocketConfig(socketConfig2);
            POOL_CONN_MANAGER.setDefaultConnectionConfig(connectionConfig);
            POOL_CONN_MANAGER.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.SECONDS);
            POOL_CONN_MANAGER.closeExpiredConnections();

            // 此处解释下MaxtTotal和DefaultMaxPerRoute的区别：

            // 1、MaxtTotal是整个池子的大小；

            // 2、DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：

            // MaxtTotal=400 DefaultMaxPerRoute=200

            // 而我只连接到http://www.abc.com时，到这个主机的并发最多只有200；而不是400；

            // 而我连接到http://www.bac.com 和

            // http://www.ccd.com时，到每个主机的并发最多只有200；即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute

            // 初始化httpClient

            httpClient = getConnection();

        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

    }

    public static CloseableHttpClient getConnection() {

        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(POOL_CONN_MANAGER)
                .setDefaultRequestConfig(buildRequestConfig(SO_TIMEOUT))
                // 定期回收空闲连接
                .evictExpiredConnections()
                // 定期回收空闲连接
                .evictIdleConnections(MAX_IDLE_TIMEOUT, TimeUnit.SECONDS)
                // 设置默认请求配置
                .setDefaultRequestConfig(buildRequestConfig(DEFAULT_SO_TIMEOUT))
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

        return httpClient;

    }

    /**
     * default request config
     *
     * @param soTimeout
     *
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
     * customize socketTimeout
     *
     * @param post
     * @param socketTimeout
     */
    private static void customizeConfig(HttpPost post, int socketTimeout) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(socketTimeout)
                .build();
        post.setConfig(requestConfig);
    }

    /**
     * post  request
     *
     * @param url
     * @param params
     *
     * @return
     */
    public static String sendPost(String url, Map<String, Object> params) {
        return sendPost(url, params, 0);
    }

    /**
     * post  request
     *
     * @param url
     * @param params
     * @param socketTimeout
     *
     * @return
     */
    public static String sendPost(String url, Map<String, Object> params, int socketTimeout) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value = (null == entry.getValue()) ? null : String.valueOf(entry.getValue());
                pairs.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }
        return sendPost(url, pairs, socketTimeout);
    }

    /**
     * post  request
     *
     * @param url
     * @param params
     * @param socketTimeout
     *
     * @return
     */
    public static String sendPost(String url, List<NameValuePair> params, int socketTimeout) {

        HttpPost post = new HttpPost(url);

        try {
            post.setEntity(new UrlEncodedFormEntity(params, UTF_8));
            if (socketTimeout > 0) {
                customizeConfig(post, socketTimeout);
            }
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return httpClient.execute(post, responseHandler);
        } catch (Exception e) {
            logger.error("url:{},error={}", url, e);
            post.abort();
        }
        return "";
    }

    /**
     * json request
     *
     * @param url
     * @param params
     * @param socketTimeout
     *
     * @return
     */
    public static String sendPostWithJSON(String url, String params, Integer socketTimeout) {
        HttpPost post = new HttpPost(url);

        try {
            post.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON));
            if (socketTimeout > 0) {
                customizeConfig(post, socketTimeout);
            }

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return httpClient.execute(post, responseHandler);

        } catch (Exception e) {
            logger.error("url:{},error={}", url, e.getMessage(), e);
            post.abort();
        }
        return "";
    }

    public static String sendGet(String url) {
        return sendGet(url, null, null);
    }

    public static String sendGet(String url, Map<String, Object> params) {
        return sendGet(url, null, params);
    }

    public static String sendGet(String url, Map<String, String> headers, Map<String, Object> params) {

        // *) 构建GET请求头
        String apiUrl = getUrlWithParams(url, params);
        HttpGet httpGet = new HttpGet(apiUrl);
        httpGet.setProtocolVersion(new HttpVersion(2, 0));

        // *) 设置header信息
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getUrlWithParams(String url, Map<String, Object> params) {
        boolean first = true;
        StringBuilder sb = new StringBuilder(url);
        for (String key : params.keySet()) {
            char ch = '&';
            if (first) {
                ch = '?';
                first = false;
            }
            String value = params.get(key).toString();
            try {
                String sval = URLEncoder.encode(value, "UTF-8");
                sb.append(ch).append(key).append("=").append(sval);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
