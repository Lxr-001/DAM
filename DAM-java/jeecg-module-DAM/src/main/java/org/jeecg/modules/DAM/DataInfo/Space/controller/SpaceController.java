package org.jeecg.modules.DAM.DataInfo.Space.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecg.modules.DAM.DataInfo.Space.entity.Space;
import org.jeecg.modules.DAM.DataInfo.Space.service.ISpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 数据空间
 * @Author: jeecg-boot
 * @Date:   2026-03-23
 * @Version: V1.0
 */
@Api(tags="数据空间")
@RestController
@RequestMapping("/space")
@Slf4j
public class SpaceController extends JeecgController<Space, ISpaceService> {
	@Autowired
	private ISpaceService spaceService;

	 /**
	  * 分页列表查询
	  *
	  * @param departId
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "数据空间 - 分页列表查询")
	 @ApiOperation(value="数据空间 - 分页列表查询", notes="数据空间 - 分页列表查询")
	 @GetMapping(value = "/list")
	 public Result<IPage<Space>> queryPageList(@RequestParam(name = "departId", required=false) String departId,
											   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
											   HttpServletRequest req) {
		 Page<Space> page = new Page<Space>(pageNo, pageSize);
		 LambdaQueryWrapper<Space> queryWrapper = new LambdaQueryWrapper<Space>();
		 if (departId != null && !departId.isEmpty()) {
			 queryWrapper.eq(Space::getDepartId, departId);
		 }
		 IPage<Space> pageList = spaceService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }

	/**
	 *   添加
	 *
	 * @param space
	 * @return
	 */
	@AutoLog(value = "数据空间-添加")
	@ApiOperation(value="数据空间-添加", notes="数据空间-添加")
	//@RequiresPermissions("space:dam_space:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Space space) {
		spaceService.save(space);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param space
	 * @return
	 */
	@AutoLog(value = "数据空间-编辑")
	@ApiOperation(value="数据空间-编辑", notes="数据空间-编辑")
	//@RequiresPermissions("space:dam_space:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Space space) {
		spaceService.updateById(space);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "数据空间-通过id删除")
	@ApiOperation(value="数据空间-通过id删除", notes="数据空间-通过id删除")
	//@RequiresPermissions("space:dam_space:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		spaceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "数据空间-批量删除")
	@ApiOperation(value="数据空间-批量删除", notes="数据空间-批量删除")
	//@RequiresPermissions("space:dam_space:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.spaceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "数据空间-通过id查询")
	@ApiOperation(value="数据空间-通过id查询", notes="数据空间-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Space> queryById(@RequestParam(name="id",required=true) String id) {
		Space space = spaceService.getById(id);
		if(space==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(space);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param space
    */
    //@RequiresPermissions("space:dam_space:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Space space) {
        return super.exportXls(request, space, Space.class, "数据空间");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("space:dam_space:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Space.class);
    }

}
