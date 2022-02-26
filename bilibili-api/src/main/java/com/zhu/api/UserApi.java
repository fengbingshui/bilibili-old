package com.zhu.api;

import com.mysql.cj.util.StringUtils;
import com.zhu.domain.JsonResponse;
import com.zhu.domain.User;
import com.zhu.exception.ConditionException;
import com.zhu.service.UserService;
import com.zhu.service.util.MD5Util;
import com.zhu.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
public class UserApi {

    @Autowired
    private UserService userService;

    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        String key = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(key);
    }

    @PostMapping("/user")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();
    }

    @PostMapping("/user-token")
    public JsonResponse<String> login(@RequestBody User user){
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }

}
