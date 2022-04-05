package com.zhu.service;

import com.zhu.dao.FollowingGroupDao;
import com.zhu.dao.UserFollowingDao;
import com.zhu.domain.JsonResponse;
import com.zhu.domain.User;
import com.zhu.domain.UserFollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingGroupService {
    @Autowired
    private FollowingGroupDao followingGroupDao;

    /**
     * 查询用户
     * getByType
     */
    UserFollowingGroup getByType(String type){
        return followingGroupDao.getByType(type);
    }

    /**
     * 查询用户
     * getById
     */
    UserFollowingGroup getById(Long id){
        return followingGroupDao.getById(id);
    }

    public List<UserFollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }
}
