package com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper;

import com.lhj.xiaohuangshu.user.biz.domain.dataobject.UserDO;

public interface UserDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO row);

    int insertSelective(UserDO row);

    UserDO selectByPrimaryKey(Long id);

    UserDO selectByPhone(String phone);

    int updateByPrimaryKeySelective(UserDO row);

    int updateByPrimaryKey(UserDO row);
}
