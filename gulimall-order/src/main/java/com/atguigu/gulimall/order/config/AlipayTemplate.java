package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000117678427";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCL+fOC+ngNi1N8QGluPmw8N4B/4inqDezscX7rszqGOl5M9gdATNvspjztTIvSiHwYv5RKtlDzcGpE9qFBH+sVmjdK3t8yhwn4bNn78k/jEot26irxvF92Lvfpu6jVMChEyV1qwmJYwusOIlE5G4nkoV8nFdwa+NisUc9NDqCTcoDiNBp0NYtmErNVHcHK/HLo+U0KrXDpcFiYGOK+1ZYeoA6Bmq64869zTb+I3s4nXhGPdipm0fJ6lCCtGMnQc8vS1BeApwaFk4/Cc9mYKDtj19CNhHn3s94gX1RR6qO/rmJXIeIiyoomju58vm1xrau62mTVwFOnPaCZLM9SU7BHAgMBAAECggEAOxyG9V0Irc+UpfgdjezWkGdv4JGF2c2frAoR6z95C45CnIWZclIC3eEmAaA9u97faSQeX562eNeUSSNN3c643BqQFxIfd5sHXt25AGjHy8DdHI6vK/684Y/cKdyR49W+a64NwTPtjEZkNUfQvSkgBmkPYdYdaVaFSeAShxwSO7GS0yCj0gQ74mHVErSiOS0e7Z3OOSceWjBHys6zZpCyR0Alwpt82F9N+QrporZs3ejFW+UoohNt+QKnEPk1Oy7HlhwHFM+3D/DwTP56W66PN4GG7JSxwlfBoa0GCKngASFpnmIxNZ2qOOrcrHFlr7Q/ImmqDcW45fNBETOCX7JVgQKBgQDMzg/MIplGMakBNiVrdJaQY+/7+2num+4T/TJiPXFIefbrGG2fdNWYPA4Jisjq42oi0C9btNtr6rc2bTb7zcKQtnYEDe1mmlHnwEMzi65WGwF/5arB7LMOEeiB6akG5wvEVPy3vSf8as+FcZlgPIPv8kpHZ2Vu8ZGlZ+Q+Q/VIcQKBgQCu91xxqnIlsaP5otuUeuK4bQRSu/ccBhzCheYJnTbvHBwfpf3j52UuoiHmx6iYTR0VOHbBAy0sbUywpQlMxHV87MCrNm1Ltg0T4aRHPGHRsVErC2t5vp8+vvbqxjxtMzJyM+lN2Ci3L57MirM4oo2IN+ycDH5G+8jZl7rcukMgNwKBgQCUb4jdj/n8+QMfoKnQTsjNT9FglSB60sLj8+/vac7QE62VSLgzCM/CLUyTN+ZU74w+PRsF4XfiqFQve0qNh9zLW6L/X0S/x3szd5J9KnCpcZtNohx3vuErgmk8tyhNm2lY26vqU4tGk/diJSpfqJyLbNTPGa5XkXfZJQo04rZE0QKBgAsZypWzmLZnhN3LxV6wRT4qPIH7RvxSLvTwhd3KSbf18prXbzpMbhD9XGVF+Z5gFh5IjNBGIVGWRa1XazWhqUqZwseaFmkdTcsbH88dN5+UPnAas2DwBS538zaGCUNsYiD4xi6YLk0hClQz4JcRSSWBVtO3vzNFukKD6+khMC0VAoGAUNCaTdEwWxLVflsdn/AUERo0MaoOOMfVoAkfN3vEceX25erSe3OJ8GmtXkUR3g3TFxYg35tN9gCs+lf23B9mKPtt8xQj+qoqPtjmvvg7xJdWbb+9FgXeqfS8aHeH8uBcNCs2Z8zHFK5tRuvpUKa/Jrol/K1KjJDk78jpBpwzi8g=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg3IUN4Doravcet+bu0rKvMTu6iRkMcJWwNKHbp/X75scKZSwSsGFhntXHA3tKPCHdFQWsv9EbSzNgSV8ade7qN/dzC130vUiyp3nnN1MhtZ6wcFe8RnhqjOng1IwAVffuyTzO9/t2tWmY3fP4Of3ef/QbKKFyLl2y4yevvVfgfbFbSMrBpY+slfYyoy9g/zxnlZBO4qIdQM6qVs84nuVof0fynwzPUM3DmEF6FVy/Sg49ij5iMyFEMcriCn2OHsNDiwuMe92kOc9mnD8HjWuNtGHfZu3xFbbw37mNJFVmODmb4WvdE3z7dV3GbrehlaXeSTUMEqKmqX9Y1O80lm03QIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url="http:/tf1ehq657r.shhttp.cn/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url="http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
