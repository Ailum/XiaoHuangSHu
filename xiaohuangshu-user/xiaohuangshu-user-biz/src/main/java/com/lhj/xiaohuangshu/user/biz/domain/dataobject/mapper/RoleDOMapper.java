package com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper;

import com.lhj.xiaohuangshu.user.biz.domain.dataobject.RoleDO;

import java.util.List;

public interface RoleDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleDO row);

    int insertSelective(RoleDO row);

    RoleDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleDO row);

    int updateByPrimaryKey(RoleDO row);

    /**
     * 鏌ヨ鎵€鏈夎鍚敤鐨勮鑹?
     *
     * @return
     */
    List<RoleDO> selectEnabledList();
}