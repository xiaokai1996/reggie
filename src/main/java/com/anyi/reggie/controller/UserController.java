package com.anyi.reggie.controller;


import cn.hutool.core.util.RandomUtil;
import com.anyi.reggie.common.CommentRedis;
import com.anyi.reggie.common.R;
import com.anyi.reggie.dto.LoginDto;
import com.anyi.reggie.entity.User;
import com.anyi.reggie.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

// user controller主要是给前端用的
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private RedisTemplate myRedisTemplate;

    @Resource
    private UserService userService;

    @GetMapping("/code")
    public R<String> getCode(String phone) {
        String code = RandomUtil.randomNumbers(6);
        String phoneRedisKey = CommentRedis.PHONE_PREFIX + phone;
        myRedisTemplate.opsForValue().set(phoneRedisKey, code, 5, TimeUnit.MINUTES);
        log.info("手机{}的验证码为{},存入到redis里面的key为{}", phone, code, phoneRedisKey);
        return R.success("发送验证码成功！");
    }

    @PostMapping("/login")
    // 这里的入参需要检验session里面的内容
    public R<User> login(@RequestBody LoginDto loginDto, HttpSession session) {
        // 1. 检查登录的code是否正确
        String phoneRedisKey = CommentRedis.PHONE_PREFIX + loginDto.getPhone();
        String codeInRedis = (String) myRedisTemplate.opsForValue().get(phoneRedisKey);
        if (!loginDto.getCode().equals(codeInRedis)) {
            return R.error("验证码错误!");
        }

        // 2. 如果是已有的用户,直接登录
        User userInDb = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, loginDto.getPhone()));
        if (userInDb != null) {
            session.setAttribute("user", userInDb);
            return R.success(userInDb);
        }

        // 3. 如果是新的用户,自动新建然后登录
        User newUser = new User();
        newUser.setPhone(loginDto.getPhone());
        newUser.setStatus(1);
        userService.save(newUser);
        session.setAttribute("user", newUser);
        return R.success(newUser);
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        // 注意这个session的信息是存放在后端服务器的
        log.info("before logout, user session info = {}", session.getAttribute("user"));
        session.removeAttribute("user");
        // 为什么后端把这个属性remove掉,前端就会自动登出呢
        return R.success("成功登出");
    }

}

