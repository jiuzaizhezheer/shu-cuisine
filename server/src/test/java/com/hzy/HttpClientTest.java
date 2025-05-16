package com.hzy;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


//@SpringBootTest
public class HttpClientTest {

    @Test
    public void testGetHttpClient() {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpGet请求
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");
        // 执行发送请求，获取响应
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 获取响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("响应状态码：" + statusCode);
            // 获取响应数据
            String responseData = EntityUtils.toString(response.getEntity());
            System.out.println("响应数据：" + responseData);
            // 关闭资源
            response.close();
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testPostHttpClient() throws UnsupportedEncodingException {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建post对象
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");
        //创建json字符串
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username","admin");
        jsonObject.put("password","123456");
        String json = jsonObject.toJSONString();
        StringEntity stringEntity = new StringEntity(json);
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        //发送
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("响应状态码：" + statusCode);
            String responseData = EntityUtils.toString(response.getEntity());
            System.out.println("响应数据：" + responseData);
            response.close();
            httpClient.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    }



