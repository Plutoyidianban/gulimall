package com.atguilimall.gulimall.lthirdparty.component;

import com.atguilimall.gulimall.lthirdparty.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
@Component
public class SmsComponent {

    private String host;
    private String path;
    private String appcode;
    private String code;


    public void SendSmsCode(String phone,String code){

//        String host = "http://dingxin.market.alicloudapi.com";
//        String path = "/dx/sendSms";
        String method = "POST";
        code="code"+":"+code;
//        String appcode = "84e56a91e853423d88518a203c52ad8d";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param", code);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            System.out.println("测试通过=============");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
