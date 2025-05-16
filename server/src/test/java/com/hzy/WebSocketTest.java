package com.hzy;

import com.hzy.websocket.WebSocketServer;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

//@SpringBootTest
public class WebSocketTest {
    //@Autowired
    private WebSocketServer webSocketServer;

    /**
     * 通过WebSocket每隔5秒向客户端发送消息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendMessageToClient() {
        webSocketServer.sendToAllClient("这是来自服务端的消息：" + LocalDateTime.now());
        /*
        //用websocket通知来单提醒
        Map map = new HashMap();
        map.put("type", 1);
        map.put("orderId", 5);
        map.put("content", "来单提醒");
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
        */
    }
}
