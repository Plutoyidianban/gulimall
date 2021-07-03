package com.atguigu.testssoclient.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
@RequestMapping("sso/hello")
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/employees")
   public String employees(Model model, HttpSession session, @RequestParam(value = "token",required = false) String token){

        if(!StringUtils.isEmpty(token)){
            RestTemplate restTemplate=new RestTemplate();
            ResponseEntity<String> entity = restTemplate.getForEntity("http://ssoserver.com:8080/userInfo?token=" + token, String.class);
            String body = entity.getBody();
            session.setAttribute("logUser",body);
        }


        Object logUser = session.getAttribute("logUser");
        if(logUser==null){

            return "redirect:"+ssoServerUrl+"?redirect_url=http://client1.com:8081/sso/hello/employees";
        }else {
            ArrayList<String> emps = new ArrayList<>();
            emps.add("zhangsan");
            emps.add("lisi");

            model.addAttribute("emps",emps);
            return "list";
        }


   }

}
