package com.lhj.xiaohuangshuauth.domain.mapper;

import com.lhj.xiaohuangshuauth.domain.dataobject.RoleDO;

import java.util.List;

public interface RoleDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleDO row);

    int insertSelective(RoleDO row);

    RoleDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleDO row);

    int updateByPrimaryKey(RoleDO row);

    /**
     * 查询所有被启用的角色
     *
     * @return
     */
    List<RoleDO> selectEnabledList();
}