package com.lhj.xiaohuangshuauth.domain.mapper;

import com.lhj.xiaohuangshuauth.domain.dataobject.RoleDO;

public interface RoleDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleDO row);

    int insertSelective(RoleDO row);

    RoleDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleDO row);

    int updateByPrimaryKey(RoleDO row);
}