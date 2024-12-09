package com.calcifer.weight.handler;

import com.alibaba.fastjson.JSON;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoleilu.hutool.date.DatePattern;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
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
        syncSendMessage(session, new TextMessage("{\"reply msg\":\"" + message.getPayload() + "\"}"));
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
        syncSendMessage(session, new TextMessage(objectMapper.writeValueAsString(wsRespWrapper)));
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
                    syncSendMessage(session, new TextMessage(message));
                }
            } catch (IOException e) {
                log.error("sendJsonToAllUser message exception", e);
            }
        }
    }

    public void sendJsonToAllUser(Object o) {
        log.debug("send json to all user...");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TextMessage message = new TextMessage(objectMapper.writeValueAsString(o));
            for (WebSocketSession session : sessionMap.values()) {
                if (session.isOpen()) {
                    log.debug("sendMessageTo: {}", session.getId());
                    syncSendMessage(session, message);
                }
            }
        } catch (IOException e) {
            log.error("sendJsonToAllUser object exception", e);
        } catch (Exception e) {
            log.error("sendJsonToAllUser exception", e);
        }
    }

    public void sendWSJsonToAllUser(WSCodeEnum wsCodeEnum, Object o) {
        sendJsonToAllUser(new WSRespWrapper<>(o, wsCodeEnum));
    }

    /**
     * 给某个用户发送消息
     */
    public void sendMessageToUser(String sessionId, TextMessage message) {
        WebSocketSession session = sessionMap.get(sessionId);
        try {
            if (session.isOpen()) {
                syncSendMessage(session, message);
            }
        } catch (IOException e) {
            log.error("sendMessageToUser exception", e);
        } catch (Exception e) {
            log.error("WS exception", e);
        }
    }

    public void sendMessageToUser(String sessionId, Object o) {
        TextMessage textMessage = new TextMessage(JSON.toJSONString(o));
        sendMessageToUser(sessionId, textMessage);
    }

    public void sendWeightLogToAllUser(String msg) {
        sendJsonToAllUser(new WSRespWrapper<>(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN) + ": " + msg, WSCodeEnum.WEIGH_LOG));
    }

    private void syncSendMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        if (session.isOpen()) {
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            }
        }
    }
}
