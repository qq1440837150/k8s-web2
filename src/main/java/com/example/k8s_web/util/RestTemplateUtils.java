package com.example.k8s_web.util;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * RestTemplate 远程调用工具类
 * @author gf
 * @date 2022/10/24
 */
@Slf4j
public class RestTemplateUtils {

    /**
     * 读取时间，自定义默认8s，0表示没有超时时间
     */
    public static final int READ_TIMEOUT = 1000*8;

    /**
     * 连接时间，自定义默认8s，0表示没有超时时间
     */
    public static final int CONNEC_TIMEOUT = 1000*8;

    /**
     * 重试次数，自定义默认1
     */
    public static final int RETRY_COUNT = 1;


    /**
     * http 请求 GET
     *
     * @param url           地址
     * @param params        参数
     * @return String 类型
     */
    public static String getHttp(String url, JSONObject params) {
        String result = getHttp(url, params, READ_TIMEOUT, CONNEC_TIMEOUT, RETRY_COUNT);
        return result;
    }
    /**
     * http 请求 GET
     *
     * @param url           地址
     * @param params        参数
     * @param connecTimeout 连接时间
     * @param readTimeout   读取时间
     * @param retryCount    重试机制
     * @return String 类型
     */
    public static String getHttp(String url, JSONObject params, int connecTimeout, int readTimeout, int retryCount) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connecTimeout);
        requestFactory.setReadTimeout(readTimeout);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 设置编码集
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler()); // 异常处理
        url = expandURL(url, params);
        String result = null; // 返回值类型;
        for (int i = 1; i <= retryCount; i++) {
            try {
                log.info("【GET/HTTP请求信息】,请求地址:{},请求参数:{}", url, params);
                result = restTemplate.getForObject(url, String.class, params);
                log.info("【GET/HTTP请求信息】,请求地址:{},请求参数:{},返回结果:{}", url, params,result);
                return result;
            } catch (Exception e) {
                log.error("【GET/HTTP请求信息】异常,重试count:{}，请求地址:{},请求参数:{},异常信息:{}", i, url, params,e);
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * https 请求 GET
     *
     * @param url           地址
     * @param params        参数
     * @return String 类型
     */
    public static String getHttps(String url, JSONObject params) {
        String result = getHttps(url, params, READ_TIMEOUT, CONNEC_TIMEOUT, RETRY_COUNT);
        return result;
    }

    /**
     * https 请求 GET
     *
     * @param url           地址
     * @param params        参数
     * @param connecTimeout 连接时间
     * @param readTimeout   读取时间
     * @param retryCount    重试机制
     * @return String 类型
     */
    public static String getHttps(String url, JSONObject params, int connecTimeout, int readTimeout, int retryCount) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connecTimeout);
        requestFactory.setReadTimeout(readTimeout);
        RestTemplate restTemplate = restTemplate();
        RestTemplateUtils.clientHttpRequestFactory();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // 设置编码集
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler()); //error处理
        restTemplate.setRequestFactory(clientHttpRequestFactory()); // 绕过https
        url = expandURL(url, params);
        String result = null; // 返回值类型;
        for (int i = 1; i <= retryCount; i++) {

            try {
                log.info("【GET/HTTPS请求信息】,请求地址:{},请求参数:{}", url, params);
                result = restTemplate.getForObject(url, String.class, params);
                log.info("【GET/HTTPS请求信息】,请求地址:{},请求参数:{},返回结果:{}", url, params,result);
                return result;
            } catch (Exception e) {
                log.error("【GET/HTTPS请求信息】异常,重试count:{}，请求地址:{},请求参数:{},异常信息:{}", i, url, params,e);
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * @Title: URL拼接
     * @MethodName:  expandURL
     * @param url
     * @param jsonObject
     * @Return java.lang.String
     * @Exception
     * @Description:
     */
    private static String expandURL(String url,JSONObject jsonObject) {

        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        Set<String> keys = jsonObject.keySet();
        for (String key : keys) {

            sb.append(key).append("=").append(jsonObject.getStr(key)).append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }




    /**
     * 获取RestTemplate实例对象，可自由调用其方法
     *
     * @return RestTemplate实例对象
     */
    public static HttpClient httpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        try {
            //设置信任ssl访问
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
            httpClientBuilder.setSSLContext(sslContext);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    // 注册http和https请求
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();

            //使用Httpclient连接池的方式配置(推荐)，同时支持netty，okHttp以及其他http框架
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 最大连接数
            poolingHttpClientConnectionManager.setMaxTotal(1000);
            // 同路由并发数
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100);
            //配置连接池
            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
            // 重试次数
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(1, true));
            //设置默认请求头
            List<Header> headers = new ArrayList<>();
            httpClientBuilder.setDefaultHeaders(headers);
            return httpClientBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
        // 连接超时(毫秒)，这里设置10秒
        clientHttpRequestFactory.setConnectTimeout(10 * 1000);
        // 数据读取超时时间(毫秒)，这里设置60秒
        clientHttpRequestFactory.setReadTimeout(60 * 1000);
        // 从连接池获取请求连接的超时时间(毫秒)，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        clientHttpRequestFactory.setConnectionRequestTimeout(10 * 1000);
        return clientHttpRequestFactory;
    }

    public static RestTemplate restTemplate(){
        //创建RestTemplate的时候，指定ClientHttpRequestFactory
        return new RestTemplate(clientHttpRequestFactory());
    }


}
