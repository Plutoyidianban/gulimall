package com.atguigu.gulimall.member;

import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.gulimall.member.vo.MemberRegisterVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;


public class test {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberLevelDao memberLevelDao;

    @Test
    public void test(){
        MemberRegisterVo registerVo = new MemberRegisterVo();
        registerVo.setPhone("18371995757");
        registerVo.setUserName("weihui");
        registerVo.setPassword("123456");
        System.out.println(registerVo);


//        if(registerVo!=null){
//            memberService.register(registerVo);
//        }
//        System.out.println(registerVo==null);
        memberService.register(registerVo);

    }
}
