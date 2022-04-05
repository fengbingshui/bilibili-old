package com.zhu.dao;

import com.zhu.domain.UserFollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FollowingGroupDao {
    UserFollowingGroup getByType(String type);

    UserFollowingGroup getById(Long id);

    List<UserFollowingGroup> getByUserId(Long userId);
}
