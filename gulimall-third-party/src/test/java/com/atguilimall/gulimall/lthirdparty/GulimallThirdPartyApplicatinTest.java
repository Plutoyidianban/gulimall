package com.atguilimall.gulimall.lthirdparty;

import com.aliyun.oss.OSSClient;
import com.atguilimall.gulimall.lthirdparty.component.SmsComponent;
import com.atguilimall.gulimall.lthirdparty.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallThirdPartyApplicatinTest {


    @Autowired
    @Resource
    private OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void SendSmsCode(){
        smsComponent.SendSmsCode("18371995757","789456121676454");
    }

//    @Test
//    public void sendSms(){
//        String host = "http://dingxin.market.alicloudapi.com";
//        String path = "/dx/sendSms";
//        String method = "POST";
//        String appcode = "84e56a91e853423d88518a203c52ad8d";
//        Map<String, String> headers = new HashMap<String, String>();
//        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
//        headers.put("Authorization", "APPCODE " + appcode);
//        Map<String, String> querys = new HashMap<String, String>();
//        querys.put("mobile", "18371995757");
//        querys.put("param", "code:1234");
//        querys.put("tpl_id", "TP1711063");
//        Map<String, String> bodys = new HashMap<String, String>();
//
//
//        try {
//            /**
//             * 重要提示如下:
//             * HttpUtils请从
//             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
//             * 下载
//             *
//             * 相应的依赖请参照
//             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
//             */
//            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//            System.out.println(response.toString());
//            //获取response的body
//            //System.out.println(EntityUtils.toString(response.getEntity()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void testupload() throws IOException {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        String endpoint = "oss-cn-shanghai.aliyuncs.com";
//// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5t7tg1qi7geCkFqbrttw";
//        String accessKeySecret = "ohTYIZw9ewopdFS8EVO7nM0P65uO0n";

// 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\Pluto\\Pictures\\2.png");
// 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("gulimall-fig", "hahaha.png", inputStream);

// 关闭OSSClient。
        ossClient.shutdown();
//        ossClient.getObject(new GetObjectRequest("gulimall-fig", "2.png"), new File("C:\\Users\\Pluto\\Pictures\\2.png"));
        System.out.println("上传完成");
    }
}
