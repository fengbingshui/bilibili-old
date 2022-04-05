package com.zhu.service;

import com.zhu.dao.FollowingGroupDao;
import com.zhu.dao.UserFollowingDao;
import com.zhu.domain.*;
import com.zhu.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {
    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    /**
     * 添加用户关注
     * 提供一个分组
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, RuntimeException.class})
    public void addUserFollowings(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            //添加到默认分组
            UserFollowingGroup followingGroup = followingGroupService.getByType(Constant.DEFAULT_FOLLOWING_GROUP_NAME);
            Long groupIdDefault = followingGroup.getId();
            userFollowing.setGroupId(groupIdDefault);
        } else {
            UserFollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在");
            }
        }
        //查询关注用户
        Long followingId = userFollowing.getFollowingId();
        User followingUser = userService.getUserById(followingId);
        if (followingUser == null) {
            throw new ConditionException("关注的用户不存在");
        }
        //更新关注表
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }

    /**
     * 获取用户关注的用户列表
     * 根据用户的id查询用户基本信息
     * 将关注的用户按照分组进行分类
     */
    public List<UserFollowingGroup> getUserFollowings(Long UserId) {
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowing(UserId);
        Set<Long> userFollowingSet = userFollowingList.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = null;
        if (userFollowingSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(userFollowingSet);
        }
        for (UserFollowing userFollowing : userFollowingList) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        //把用户关注的所有分组查询出来
        List<UserFollowingGroup> userFollowingGroupList = followingGroupService.getByUserId(UserId);

        UserFollowingGroup allUserFollowingGroup = new UserFollowingGroup();
        allUserFollowingGroup.setName(Constant.DEFAULT_ALL_FOLLOWING_GROUP_NAME);
        allUserFollowingGroup.setUserFollowingList(userInfoList);

        List<UserFollowingGroup> result = new ArrayList<>();
        result.add(allUserFollowingGroup);

        for (UserFollowingGroup userFollowingGroup : userFollowingGroupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : userFollowingList) {
                if(userFollowingGroup.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            userFollowingGroup.setUserFollowingList(infoList);
            result.add(userFollowingGroup);
        }
        return result;

    }

    /**
     * 获取当前用户的粉丝列表
     * 根据粉丝的用户id查询基本信息
     * 查询当前用户是否已经关注该粉丝
     */
//    public List<UserFollowing> getUserFans(Long userId){
//        List<UserFollowing> list = userFollowingDao.getUserFans(userId);
//
//    }
}
