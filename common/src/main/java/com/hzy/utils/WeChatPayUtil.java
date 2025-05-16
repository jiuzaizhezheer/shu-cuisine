/*
//TODO微信支付工具类
package com.hzy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.hzy.properties.WeChatProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;




 */
/* 微信支付工具类*//*




@Component
public class WeChatPayUtil {

    // 接口地址
    public static final String JSAPI = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    public static final String REFUNDS = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    // 常量定义
    private static final String CURRENCY_CNY = "CNY";
    private static final String SIGN_TYPE_RSA = "RSA";
    private static final String SHA256_WITH_RSA = "SHA256withRSA"
    private static final String PREPAY_ID_PREFIX = "prepay_id=";

    @Autowired
    private WeChatProperties weChatProperties;

    private PrivateKey merchantPrivateKey;

*/
/*
     * 初始化方法，加载商户私钥

 *//*



    @PostConstruct
    private void init() throws Exception {
        merchantPrivateKey = PemUtil.loadPrivateKey(new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath())));
    }

*/
/*
     * 获取调用微信接口的客户端工具对象
     * @return CloseableHttpClient
 *//*



    private CloseableHttpClient getClient() {
        try {
            X509Certificate x509Certificate = PemUtil.loadCertificate(new FileInputStream(new File(weChatProperties.getWeChatPayCertFilePath())));
            List<X509Certificate> wechatPayCertificates = Collections.singletonList(x509Certificate);

            return WechatPayHttpClientBuilder.create()
                    .withMerchant(weChatProperties.getMchid(), weChatProperties.getMchSerialNo(), merchantPrivateKey)
                    .withWechatPay(wechatPayCertificates)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("初始化微信支付客户端失败", e);
        }
    }

*/
/*
     * 发送POST请求

 *//*



    private String post(String url, String body) throws Exception {
        try (CloseableHttpClient httpClient = getClient();
             CloseableHttpResponse response = executePost(httpClient, url, body)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

*/
/*
     * 执行POST请求

 *//*



    private CloseableHttpResponse executePost(CloseableHttpClient client, String url, String body) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpPost.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());
        httpPost.setEntity(new StringEntity(body, UTF_8));
        return client.execute(httpPost);
    }

*/
/*
     * 发送GET请求

 *//*



    private String get(String url) throws Exception {
        try (CloseableHttpClient httpClient = getClient();
             CloseableHttpResponse response = executeGet(httpClient, url)) {
            return EntityUtils.toString(response.getEntity());
        }
    }

*/
/*
     * 执行GET请求

 *//*



    private CloseableHttpResponse executeGet(CloseableHttpClient client, String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        httpGet.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());
        return client.execute(httpGet);
    }

*/
/*
     * 构造金额JSON对象

 *//*



    private JSONObject buildAmount(BigDecimal total) {
        int amountInCents = total.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).intValue();
        JSONObject amount = new JSONObject();
        amount.put("total", amountInCents);
        amount.put("currency", CURRENCY_CNY);
        return amount;
    }

*/
/*
     * JSAPI下单

 *//*



    private String jsapi(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", weChatProperties.getAppid());
        jsonObject.put("mchid", weChatProperties.getMchid());
        jsonObject.put("description", description);
        jsonObject.put("out_trade_no", orderNum);
        jsonObject.put("notify_url", weChatProperties.getNotifyUrl());
        jsonObject.put("amount", buildAmount(total));

        JSONObject payer = new JSONObject();
        payer.put("openid", openid);
        jsonObject.put("payer", payer);

        String body = jsonObject.toJSONString();
        return post(JSAPI, body);
    }

*/
/*
     * 小程序支付
     *
 *//*



    public JSONObject pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        String bodyAsString = jsapi(orderNum, total, description, openid);
        JSONObject jsonObject = JSON.parseObject(bodyAsString);

        String prepayId = jsonObject.getString("prepay_id");
        if (prepayId != null) {
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = RandomStringUtils.randomNumeric(32);

            String signMessage = String.join("\n",
                    weChatProperties.getAppid(),
                    timeStamp,
                    nonceStr,
                    PREPAY_ID_PREFIX + prepayId,
                    ""
            );

            Signature signature = Signature.getInstance(SHA256_WITH_RSA);
            signature.initSign(merchantPrivateKey);
            signature.update(signMessage.getBytes(UTF_8));
            String packageSign = Base64.getEncoder().encodeToString(signature.sign());

            JSONObject jo = new JSONObject();
            jo.put("timeStamp", timeStamp);
            jo.put("nonceStr", nonceStr);
            jo.put("package", PREPAY_ID_PREFIX + prepayId);
            jo.put("signType", SIGN_TYPE_RSA);
            jo.put("paySign", packageSign);

            return jo;
        }
        return jsonObject;
    }

*/
/*
     * 申请退款
     * @param outTradeNo
 *//*



    public String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", outTradeNo);
        jsonObject.put("out_refund_no", outRefundNo);
        jsonObject.put("amount", buildRefundAmount(refund, total));
        jsonObject.put("notify_url", weChatProperties.getRefundNotifyUrl());

        String body = jsonObject.toJSONString();
        return post(REFUNDS, body);
    }

*/
/*
     * 构造退款金额JSON对象
     * @param refund
 *//*



    private JSONObject buildRefundAmount(BigDecimal refund, BigDecimal total) {
        JSONObject amount = new JSONObject();
        amount.put("refund", refund.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).intValue());
        amount.put("total", total.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).intValue());
        amount.put("currency", CURRENCY_CNY);
        return amount;
    }
}




*/
