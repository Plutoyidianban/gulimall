//package com.atguilimall.gulimall.lthirdparty;
//
//import com.aliyun.oss.OSSClient;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//class GulimallThirdPartyApplicationTests {
//
//
//    @Autowired
//    @Resource
//    private OSSClient ossClient;
//
//    @Test
//    public void testupload() throws IOException {
//        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
////        String endpoint = "oss-cn-shanghai.aliyuncs.com";
////// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
////        String accessKeyId = "LTAI5t7tg1qi7geCkFqbrttw";
////        String accessKeySecret = "ohTYIZw9ewopdFS8EVO7nM0P65uO0n";
//
//// 创建OSSClient实例。
////        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//// 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\Pluto\\Pictures\\2.png");
//// 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
//        ossClient.putObject("gulimall-fig", "hahaha.png", inputStream);
//
//// 关闭OSSClient。
//        ossClient.shutdown();
////        ossClient.getObject(new GetObjectRequest("gulimall-fig", "2.png"), new File("C:\\Users\\Pluto\\Pictures\\2.png"));
//        System.out.println("上传完成");
//    }
//}
