package com.calcifer.weight.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.calcifer.weight.service.ModbusDeviceService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @RequestMapping("barrier")
    public Integer barrier(Integer serialPort) throws InterruptedException {
        modbusDeviceService.controlModBusDevice(serialPort, true);
        Thread.sleep(200);
        modbusDeviceService.controlModBusDevice(serialPort, false);
        return serialPort;
    }

    @RequestMapping("light")
    public String light(Integer serialPort, boolean status) {
        modbusDeviceService.controlModBusDevice(serialPort, status);
        return serialPort + ":" + (status ? "红" : "绿");
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
