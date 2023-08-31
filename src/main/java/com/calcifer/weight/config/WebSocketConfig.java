package com.calcifer.weight.config;

import com.calcifer.weight.handler.WeightWebSocketHandler;
import com.calcifer.weight.interceptor.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Spring WebSocket的配置
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private WeightWebSocketHandler webSocketHandler;
    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //1.注册WebSocket
        String websocket_url = "/weightproject/websocket/socketServer.do";                        //设置websocket的地址
        registry.addHandler(webSocketHandler, websocket_url).                          //注册Handler
                addInterceptors(webSocketHandshakeInterceptor);                   //注册Interceptor

        //2.注册SockJS，提供SockJS支持(主要是兼容ie8)
        String sockjs_url = "/weightproject/sockjs/socketServer.do";                              //设置sockjs的地址
        registry.addHandler(webSocketHandler, sockjs_url).                            //注册Handler
                addInterceptors(webSocketHandshakeInterceptor).                   //注册Interceptor
                withSockJS();                                                           //支持sockjs协议
    }
}
