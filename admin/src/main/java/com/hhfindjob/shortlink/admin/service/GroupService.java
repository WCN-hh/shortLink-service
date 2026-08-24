package com.hhfindjob.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhfindjob.shortlink.admin.dao.entity.GroupDO;
import com.hhfindjob.shortlink.admin.dto.request.group.GroupSortDTO;
import com.hhfindjob.shortlink.admin.dto.request.group.GroupUpdateRequestDTO;
import com.hhfindjob.shortlink.admin.dto.response.group.GroupResponseDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {

    Boolean saveGruop(String name);

    Boolean saveGruop(String userName, String groupName);

    List<GroupResponseDTO> getGroup();

    Boolean updateGroup(GroupUpdateRequestDTO dto);

    Boolean delete(String gid);

    Boolean sortGroup(List<GroupSortDTO> list);
}
