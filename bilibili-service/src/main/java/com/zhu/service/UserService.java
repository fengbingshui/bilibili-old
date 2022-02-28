package com.zhu.service;

import com.mysql.cj.util.StringUtils;
import com.zhu.dao.UserDao;
import com.zhu.domain.Constant;
import com.zhu.domain.User;
import com.zhu.domain.UserInfo;
import com.zhu.exception.ConditionException;
import com.zhu.service.util.MD5Util;
import com.zhu.service.util.RSAUtil;
import com.zhu.service.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    public void addUser(User user) {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空");
        }
        User userByPhone = getUserByPhone(phone);
        if(userByPhone != null){
            throw new ConditionException("该手机号已注册");
        }
        //生成盐值
        Date now = new Date();
        String sale = String.valueOf(now.getTime());
        String password = user.getPassword();
        String decryptPassword = null;
        try {
            decryptPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码加密失败");
        }
        String md5Password = MD5Util.sign(decryptPassword, sale, "UTF-8");
        user.setCreateTime(now);
        user.setPassword(md5Password);
        user.setSalt(sale);
        userDao.addUser(user);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(Constant.DEFAULT_NICK);
        userInfo.setBirth(Constant.DEFAULT_BIRTH);
        userInfo.setCreateTime(now);
        userInfo.setGender(Constant.GENDER_MALE);
        userDao.addUserInfo(userInfo);

    }
    public User getUserByPhone(String phone){
        return userDao.getUserByPhone(phone);
    }

    public String login(User user) throws Exception {
        String phone = user.getPhone();
        User userByPhone = userDao.getUserByPhone(phone);
        if(userByPhone == null){
            throw new ConditionException("当前用户不存在");
        }
        String password = user.getPassword();
        String rowPassword;
        try {
            rowPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }

        String salt = userByPhone.getSalt();
        String MD5Password = MD5Util.sign(rowPassword, salt, "UTF-8");
        if(!Objects.equals(MD5Password,userByPhone.getPassword())){
            throw new ConditionException("密码错误");
        }
        return TokenUtil.generateToken(userByPhone.getId());
    }

    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    public void updateUserInfo(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        Integer i = userDao.updateUserInfo(userInfo);
    }
}
