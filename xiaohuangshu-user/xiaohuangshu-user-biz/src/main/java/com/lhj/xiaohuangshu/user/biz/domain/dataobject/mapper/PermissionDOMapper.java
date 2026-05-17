package com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper;

import com.lhj.xiaohuangshu.user.biz.domain.dataobject.PermissionDO;

import java.util.List;

public interface PermissionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermissionDO row);

    int insertSelective(PermissionDO row);

    PermissionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PermissionDO row);

    int updateByPrimaryKey(PermissionDO row);

    List<PermissionDO> selectAppEnabledList();
}