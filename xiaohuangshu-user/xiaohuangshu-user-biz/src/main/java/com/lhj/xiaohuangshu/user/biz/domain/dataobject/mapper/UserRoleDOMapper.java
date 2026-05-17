package com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper;

import com.lhj.xiaohuangshu.user.biz.domain.dataobject.UserRoleDO;

public interface UserRoleDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRoleDO row);

    int insertSelective(UserRoleDO row);

    UserRoleDO selectByPrimaryKey(Long id);

    UserRoleDO selectByUserIdAndRoleId(Long userId, Long roleId);

    int updateByPrimaryKeySelective(UserRoleDO row);

    int updateByPrimaryKey(UserRoleDO row);
}
