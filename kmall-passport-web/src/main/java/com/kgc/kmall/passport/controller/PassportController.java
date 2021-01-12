package com.kgc.kmall.passport.controller;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {
    @Reference
    MemberService memberService;
    @RequestMapping("index")
    public String index(String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "index";
    }
    @RequestMapping("login")
    @ResponseBody
    public String login(Member member, HttpServletRequest request){
     String token = "";
        //验证用户名和密码
        Member umsMember = memberService.login(member);
        //制作token
if (umsMember!=null){
    System.out.println("成功");
    Long memberId = umsMember.getId();
    String nickname = umsMember.getNickname();
    Map<String,Object> userMap = new HashMap<>();
    userMap.put("memberId",memberId);
    userMap.put("nickname",nickname);
    // 如果使用了nginx，则需要如此获取客户端ip
//            String ip = request.getHeader("x-forwarded-for");
    //如果没有使用nginx
    String ip = request.getRemoteAddr();// 从request中获取ip
    if(StringUtils.isBlank(ip)||ip.equals("0:0:0:0:0:0:0:1")){
        ip = "127.0.0.1";
    }
    // 按照设计的算法对参数进行加密后，生成token
    token= JwtUtil.encode("2020kamll077",userMap,ip);
    // 将token存入redis一份
    memberService.addUserToken(token,memberId);
}else {
    System.out.println("失败");
    //验证不成功
    // 登录失败
    token = "fail";
}
        return token;
    }
}