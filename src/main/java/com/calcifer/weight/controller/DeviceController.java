package com.calcifer.weight.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.calcifer.weight.common.WeightContext;
import com.calcifer.weight.entity.enums.RespCodeEnum;
import com.calcifer.weight.entity.vo.RespWrapper;
import com.calcifer.weight.service.ModbusDeviceService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("device")
public class DeviceController {
    @Autowired
    private ModbusDeviceService modbusDeviceService;

    @Value("${calcifer.weight.plate-reader-address}")
    private List<String> plateReaderAddressArray;

    @RequestMapping("modbus/port")
    public Integer controlModbusByPort(int serialPort, boolean status) {
        modbusDeviceService.controlModBusDevice(serialPort, status);
        return serialPort;
    }

    @RequestMapping("modbus/name")
    public RespWrapper<?> controlModbusByName(String deviceName) {
        if (!StringUtils.hasText(deviceName)) return new RespWrapper<>(RespCodeEnum.ERROR);
        switch (deviceName) {
            case "FRONT_BARRIER_ON":
                modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), true);
                modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOn(), false);
                break;
            case "FRONT_BARRIER_OFF":
                modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), true);
                modbusDeviceService.controlModBusDevice(WeightContext.front.getBarrierGateOff(), false);
            case "BACK_BARRIER_ON":
                modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOn(), true);
                modbusDeviceService.controlModBusDevice(WeightContext.back.getBarrierGateOn(), false);
                break;
            case "TRAFFIC_LIGHT_RED":
                modbusDeviceService.controlModBusDevice(WeightContext.front.getTrafficLight(), true);
                modbusDeviceService.controlModBusDevice(WeightContext.back.getTrafficLight(), true);
                break;
            case "TRAFFIC_LIGHT_GREEN":
                modbusDeviceService.controlModBusDevice(WeightContext.front.getTrafficLight(), false);
                modbusDeviceService.controlModBusDevice(WeightContext.back.getTrafficLight(), false);
                break;
        }
        return new RespWrapper<>(RespCodeEnum.SUCCESS);
    }

    @RequestMapping("plateReaderAddress")
    public List<String> getPlateReaderAddress() {
        return plateReaderAddressArray;
    }

    @RequestMapping("wsURL")
    public List<String> getWsURLs() {
        return plateReaderAddressArray.stream().map(address -> {
            try {
                String url = "http://" + address + "/request.php";
                String body = "{\"type\":\"get_live_stream_type\",\"module\":\"BUS_WEB_REQUEST\"}";
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new StringEntity(body));
                try (CloseableHttpClient client = HttpClients.createDefault()) {
                    try (CloseableHttpResponse response = client.execute(httpPost)) {
                        String res = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                        JSONObject resBody = JSON.parseObject(res).getJSONObject("body");
                        String port = resBody.getString("port");
                        String token = resBody.getString("token");
                        return "ws://" + address + ":" + port + "/ws.flv?token=" + token + "&channel=0";
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
