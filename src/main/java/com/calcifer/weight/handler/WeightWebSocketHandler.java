package com.calcifer.weight.handler;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.entity.dto.WSMessage;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.enums.WSMessageTypeEnum;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;

/**
 * Websocket处理器
 */
@Slf4j
@Component
public class WeightWebSocketHandler extends TextWebSocketHandler {

    //已建立的连接
    private static final HashMap<String, WebSocketSession> sessionMap = new HashMap<>();

    /**
     * 处理前端发送的文本信息
     * js调用websocket.send时候，会调用该方法
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获取提交过来的消息详情
        log.info("收到用户 {} 的消息: {}", session.getId(), message.toString());
        //回复一条信息
        session.sendMessage(new TextMessage("{\"reply msg\":\"" + message.getPayload() + "\"}"));
    }


    /**
     * 当新连接建立的时候，被调用
     * 连接成功时候，会触发页面上onOpen方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("websocket sessionId: {}", session.getId());
        String sessionId = session.getUri().getQuery();
        log.info("websocket userId: {}", sessionId);
        if (sessionId == null) {
            sessionId = session.getId();
        }
        sessionMap.put(sessionId, session);
        WSRespWrapper<String> wsRespWrapper = new WSRespWrapper<>(sessionId, WSCodeEnum.MSGType_1);
        ObjectMapper objectMapper = new ObjectMapper();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsRespWrapper)));
    }

    /**
     * 当连接关闭时被调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        log.info("用户: {} Connection closed. Status: {}", sessionId, status);
        sessionMap.remove(sessionId);
    }

    /**
     * 传输错误时调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        if (session.isOpen()) {
            session.close();
        }
        log.info("用户: {} websocket connection closed", sessionId);
        sessionMap.remove(sessionId);
    }

    /**
     * 给所有在线用户发送消息
     */
    public void sendMessageToAllUser(String message) {
        for (WebSocketSession session : sessionMap.values()) {
            try {
                if (session.isOpen()) {
                    log.info("sendMessageTo: {}", session.getId());
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendJsonToAllUser(Object o) {
        for (WebSocketSession session : sessionMap.values()) {
            try {
                if (session.isOpen()) {
                    log.info("sendMessageTo: {}", session.getId());
                    session.sendMessage(new TextMessage(JSON.toJSONString(o)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendJsonToAllUser(WSMessageTypeEnum wsMessageType, Object o) {
        WSMessage wsMessage = new WSMessage(wsMessageType, o);
        sendJsonToAllUser(wsMessage);
    }

    /**
     * 给某个用户发送消息
     */
    public void sendMessageToUser(String sessionId, TextMessage message) {
        WebSocketSession user = sessionMap.get(sessionId);
        try {
            if (user.isOpen()) {
                user.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToUser(String sessionId, Object o) {
        TextMessage textMessage = new TextMessage(JSON.toJSONString(o));
        sendMessageToUser(sessionId, textMessage);
    }
}
