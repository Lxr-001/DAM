package org.jeecg.modules.DAM.DataInfo.SpaceUser.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.DAM.DataInfo.Space.service.ISpaceService;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.entity.SpaceUser;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.service.ISpaceUserService;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.InvitableUserVo;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.SpaceUserOptionVo;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.SpaceUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 数据空间用户关联表
 * @Author: jeecg-boot
 * @Date:   2026-04-17
 * @Version: V1.0
 */
@Api(tags="数据空间用户")
@RestController
@RequestMapping("/spaceUser")
@Slf4j
public class SpaceUserController extends JeecgController<SpaceUser, ISpaceUserService> {
    @Autowired
    private ISpaceUserService spaceUserService;

    @Autowired
    private ISpaceService spaceService;

    /**
     * 分页列表查询
     *
     * @param spaceUser
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "数据空间用户-分页列表查询")
    @ApiOperation(value="数据空间用户-分页列表查询", notes="数据空间用户-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SpaceUser>> queryPageList(SpaceUser spaceUser,
                                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                  HttpServletRequest req) {
        QueryWrapper<SpaceUser> queryWrapper = QueryGenerator.initQueryWrapper(spaceUser, req.getParameterMap());
        Page<SpaceUser> page = new Page<SpaceUser>(pageNo, pageSize);
        IPage<SpaceUser> pageList = spaceUserService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过spaceId查询该空间下的所有用户（包含用户详情）
     *
     * @param spaceId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "数据空间用户-通过spaceId查询")
    @ApiOperation(value="数据空间用户-通过spaceId查询", notes="数据空间用户-通过spaceId查询")
    @GetMapping(value = "/listBySpaceId")
    public Result<IPage<SpaceUserVo>> listBySpaceId(@RequestParam(name="spaceId", required=false) String spaceId,
                                                    @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        if (spaceId == null || spaceId.trim().isEmpty()) {
            return Result.error("spaceId不能为空");
        }
        Page<SpaceUserVo> page = new Page<SpaceUserVo>(pageNo, pageSize);
        IPage<SpaceUserVo> pageList = spaceUserService.selectSpaceUserDetailPage(page, spaceId);
        return Result.OK(pageList);
    }

    /**
     * 分页查询可邀请的用户列表（包含角色信息）
     *
     * @param spaceId 空间ID（用于排除已邀请的用户）
     * @param username 用户账号（模糊查询）
     * @param realname 用户姓名（模糊查询）
     * @param departId 部门ID
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "数据空间用户-查询可邀请的用户")
    @ApiOperation(value="数据空间用户-查询可邀请的用户", notes="数据空间用户-查询可邀请的用户")
    @GetMapping(value = "/invitableUser/list")
    public Result<IPage<SpaceUserOptionVo>> listInvitableUsers(@RequestParam(name="spaceId", required=false) String spaceId,
                                                                @RequestParam(name="username", required=false) String username,
                                                                @RequestParam(name="realname", required=false) String realname,
                                                                @RequestParam(name="departId", required=false) String departId,
                                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Page<SpaceUserOptionVo> page = new Page<SpaceUserOptionVo>(pageNo, pageSize);
        // 密级上限默认设置为最高值，以便显示所有有权限看到的用户
        Integer maxDataSecurity = 1;
        IPage<SpaceUserOptionVo> pageList = spaceUserService.selectInvitableUserPage(page, spaceId, username, realname, departId, maxDataSecurity);
        return Result.OK(pageList);
    }

    /**
     * 分页查询可邀请的用户列表（包含角色信息，通过USER_ROLE关联）
     *
     * @param spaceId 空间ID（用于排除已邀请的用户）
     * @param username 用户账号（模糊查询）
     * @param realname 用户姓名（模糊查询）
     * @param departId 部门ID
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "数据空间用户-查询可邀请的用户(带角色)")
    @ApiOperation(value="数据空间用户-查询可邀请的用户(带角色)", notes="数据空间用户-查询可邀请的用户(带角色)")
    @GetMapping(value = "/invitableUserWithRoles/list")
    public Result<IPage<InvitableUserVo>> listInvitableUsersWithRoles(@RequestParam(name="spaceId", required=false) String spaceId,
                                                                      @RequestParam(name="username", required=false) String username,
                                                                      @RequestParam(name="realname", required=false) String realname,
                                                                      @RequestParam(name="departId", required=false) String departId,
                                                                      @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Page<InvitableUserVo> page = new Page<InvitableUserVo>(pageNo, pageSize);
        Integer maxDataSecurity = 1;
        IPage<InvitableUserVo> pageList = spaceUserService.selectInvitableUserWithRolesPage(page, spaceId, username, realname, departId, maxDataSecurity);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param spaceUser
     * @return
     */
    @AutoLog(value = "数据空间用户-添加")
    @ApiOperation(value="数据空间用户-添加", notes="数据空间用户-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SpaceUser spaceUser) {
        spaceUserService.save(spaceUser);
        // 更新空间成员数量
        updateSpaceUserCount(spaceUser.getSpaceId());
        return Result.OK("添加成功！");
    }

    /**
     * 批量添加
     *
     * @param spaceUsers
     * @return
     */
    @AutoLog(value = "数据空间用户-批量添加")
    @ApiOperation(value="数据空间用户-批量添加", notes="数据空间用户-批量添加")
    @PostMapping(value = "/addBatch")
    public Result<String> addBatch(@RequestBody List<SpaceUser> spaceUsers) {
        if (spaceUsers != null && !spaceUsers.isEmpty()) {
            spaceUserService.saveBatch(spaceUsers);
            // 更新空间成员数量
            String spaceId = spaceUsers.get(0).getSpaceId();
            updateSpaceUserCount(spaceId);
        }
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param spaceUser
     * @return
     */
    @AutoLog(value = "数据空间用户-编辑")
    @ApiOperation(value="数据空间用户-编辑", notes="数据空间用户-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody SpaceUser spaceUser) {
        spaceUserService.updateById(spaceUser);
        return Result.OK("编辑成功！");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "数据空间用户-通过id删除")
    @ApiOperation(value="数据空间用户-通过id删除", notes="数据空间用户-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id", required=true) String id) {
        SpaceUser spaceUser = spaceUserService.getById(id);
        if (spaceUser != null) {
            spaceUserService.removeById(id);
            updateSpaceUserCount(spaceUser.getSpaceId());
        }
        return Result.OK("删除成功！");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "数据空间用户-批量删除")
    @ApiOperation(value="数据空间用户-批量删除", notes="数据空间用户-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids", required=true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        if (!idList.isEmpty()) {
            // 获取第一个用户关联的空间ID来更新成员数量
            SpaceUser firstUser = spaceUserService.getById(idList.get(0));
            String spaceId = firstUser != null ? firstUser.getSpaceId() : null;

            this.spaceUserService.removeByIds(idList);

            // 更新空间成员数量
            if (spaceId != null) {
                updateSpaceUserCount(spaceId);
            }
        }
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过userId删除（移除用户出空间）
     *
     * @param spaceId
     * @param userId
     * @return
     */
    @AutoLog(value = "数据空间用户-移除用户")
    @ApiOperation(value="数据空间用户-移除用户", notes="数据空间用户-移除用户")
    @DeleteMapping(value = "/removeUser")
    public Result<String> removeUser(@RequestParam(name="spaceId") String spaceId,
                                       @RequestParam(name="userId") String userId) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("space_id", spaceId);
        queryWrapper.eq("user_id", userId);
        spaceUserService.remove(queryWrapper);
        // 更新空间成员数量
        updateSpaceUserCount(spaceId);
        return Result.OK("移除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "数据空间用户-通过id查询")
    @ApiOperation(value="数据空间用户-通过id查询", notes="数据空间用户-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SpaceUser> queryById(@RequestParam(name="id", required=true) String id) {
        SpaceUser spaceUser = spaceUserService.getById(id);
        return Result.OK(spaceUser);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param spaceUser
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SpaceUser spaceUser) {
        return super.exportXls(request, spaceUser, SpaceUser.class, "数据空间用户");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SpaceUser.class);
    }

    /**
     * 更新空间的成员数量
     * @param spaceId 空间ID
     */
    private void updateSpaceUserCount(String spaceId) {
        try {
            QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("space_id", spaceId);
            long count = spaceUserService.count(queryWrapper);
            spaceService.updateUserCount(spaceId, (int) count);
        } catch (Exception e) {
            log.error("更新空间成员数量失败: " + e.getMessage());
        }
    }
}
