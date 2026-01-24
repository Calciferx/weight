package com.calcifer.weight.handler;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.calcifer.weight.entity.enums.WSCodeEnum;
import com.calcifer.weight.entity.vo.WSRespWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Websocket处理器
 */
@Slf4j
@Component
public class WeightWebSocketHandler extends TextWebSocketHandler {

    //已建立的连接
    private static final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 处理前端发送的文本信息
     * js调用websocket.send时候，会调用该方法
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获取提交过来的消息详情
        log.info("收到用户 {} 的消息: {}", session.getId(), message.toString());
        //回复一条信息
        session.sendMessage(new TextMessage("""
                {"reply msg": %s}
                """.formatted(message.getPayload())
        ));
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
        session.getAttributes().put("sessionId", sessionId);
        sessionMap.put(sessionId, session);
        WSRespWrapper<String> wsRespWrapper = new WSRespWrapper<>(sessionId, WSCodeEnum.SUCCESS);
        session.sendMessage(new TextMessage(JSON.toJSONString(wsRespWrapper)));
    }

    /**
     * 当连接关闭时被调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = (String) session.getAttributes().get("sessionId");
        log.info("用户: {} Connection closed. Status: {}", sessionId, status);
        if (sessionId != null) {
            sessionMap.remove(sessionId);
        }
    }

    /**
     * 传输错误时调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = (String) session.getAttributes().get("sessionId");
        if (session.isOpen()) {
            session.close();
        }
        log.info("用户: {} websocket connection closed", sessionId);
        if (sessionId != null) {
            sessionMap.remove(sessionId);
        }
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

    /**
     * 给所有在线用户发送消息
     */
    public void sendMessageToAllUser(String message) {
        for (WebSocketSession session : sessionMap.values()) {
            try {
                if (session.isOpen()) {
                    log.debug("sendMessageTo: {}", session.getId());
                    syncSendMessage(session, new TextMessage(message));
                }
            } catch (Exception e) {
                log.error("sendMessageToAllUser exception", e);
            }
        }
    }

    /**
     * 给所有在线用户发送json消息
     */
    public void sendJsonToAllUser(Object o) {
        log.debug("send json to all user...");
        try {
            TextMessage message = new TextMessage(JSON.toJSONString(o));
            for (WebSocketSession session : sessionMap.values()) {
                if (session.isOpen()) {
                    log.debug("sendMessageTo: {}", session.getId());
                    syncSendMessage(session, message);
                }
            }
        } catch (Exception e) {
            log.error("sendJsonToAllUser exception", e);
        }
    }

    /**
     * 按照与前端约定的格式给所有用户发送json消息
     *
     * @param wsCodeEnum 消息类型
     * @param o          数据
     */
    public void sendWSJsonToAllUser(WSCodeEnum wsCodeEnum, Object o) {
        sendJsonToAllUser(new WSRespWrapper<>(o, wsCodeEnum));
    }

    /**
     * 按照与前端约定的格式给所有用户发送json消息
     * 发送前端日志窗口显示的称重日志
     */
    public void sendWeightLogToAllUser(String msg) {
        sendJsonToAllUser(new WSRespWrapper<>(DatePattern.NORM_DATETIME_MS_FORMAT.format(new Date()) + ": " + msg, WSCodeEnum.WEIGH_LOG));
    }

    /**
     * 给某个用户发送消息
     */
    public void sendMessageToUser(String sessionId, TextMessage message) {
        WebSocketSession session = sessionMap.get(sessionId);
        try {
            if (session.isOpen()) {
                log.debug("sendMessageTo: {}", session.getId());
                syncSendMessage(session, message);
            }
        } catch (Exception e) {
            log.error("sendMessageToUser exception", e);
        }
    }

    /**
     * 给某个用户发送json消息
     */
    public void sendMessageToUser(String sessionId, Object o) {
        TextMessage textMessage = new TextMessage(JSON.toJSONString(o));
        sendMessageToUser(sessionId, textMessage);
    }
}