package com.hhfindjob.shortlink.admin.controller;


import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.common.convention.result.Results;
import com.hhfindjob.shortlink.admin.dto.request.group.GroupNameRequestDTO;
import com.hhfindjob.shortlink.admin.dto.request.group.GroupSortDTO;
import com.hhfindjob.shortlink.admin.dto.request.group.GroupUpdateRequestDTO;
import com.hhfindjob.shortlink.admin.dto.response.group.GroupResponseDTO;
import com.hhfindjob.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 短链接分组控制层
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1/group")
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增分组
     */
    @PostMapping()
    public Result<Boolean> newGroup(@RequestBody GroupNameRequestDTO dto){
        return Results.success(groupService.saveGruop(dto.getGroupName()));
    }

    /**
     * 查询分组集合
     */
    @GetMapping()
    public Result<List<GroupResponseDTO>> getGroup(){
        return Results.success(groupService.getGroup());
    }

    /**
     * 修改分组
     */
    @PutMapping()
    public Result<Boolean> updateGroup(@RequestBody GroupUpdateRequestDTO dto){
        return Results.success(groupService.updateGroup(dto));
    }

    /**
     * 删除分组
     */
    @DeleteMapping()
    public Result<Boolean> deleteGroup(@RequestParam("gid") String gid){
        return Results.success(groupService.delete(gid));
    }

    /**
     * 排序功能
     */
    @PostMapping("/sort")
    public Result<Boolean> sortGroup(@RequestBody List<GroupSortDTO> list){
        return Results.success(groupService.sortGroup(list));
    }

}
