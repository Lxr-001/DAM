package org.jeecg.modules.DAM.Demand.controller;

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
import org.jeecg.modules.DAM.Demand.entity.Demand;
import org.jeecg.modules.DAM.Demand.service.IDemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

 /**
 * @Description: 需求大厅
 * @Author: jeecg-boot
 * @Date:   2026-03-25
 * @Version: V1.0
 */
@Api(tags="需求大厅")
@RestController
@RequestMapping("/demand")
@Slf4j
public class DemandController extends JeecgController<Demand, IDemandService> {
	@Autowired
	private IDemandService demandService;

	/**
	 * 分页列表查询
	 *
	 * @param demand
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "需求大厅-分页列表查询")
	@ApiOperation(value="需求大厅-分页列表查询", notes="需求大厅-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Demand>> queryPageList(Demand demand,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Demand> queryWrapper = QueryGenerator.initQueryWrapper(demand, req.getParameterMap());
		Page<Demand> page = new Page<Demand>(pageNo, pageSize);
		IPage<Demand> pageList = demandService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param demand
	 * @return
	 */
	@AutoLog(value = "需求大厅-添加")
	@ApiOperation(value="需求大厅-添加", notes="需求大厅-添加")
	//@RequiresPermissions("demand:dam_demand:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Demand demand) {
		demandService.save(demand);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param demand
	 * @return
	 */
	@AutoLog(value = "需求大厅-编辑")
	@ApiOperation(value="需求大厅-编辑", notes="需求大厅-编辑")
	//@RequiresPermissions("demand:dam_demand:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Demand demand) {
		demandService.updateById(demand);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "需求大厅-通过id删除")
	@ApiOperation(value="需求大厅-通过id删除", notes="需求大厅-通过id删除")
	//@RequiresPermissions("demand:dam_demand:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		demandService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "需求大厅-批量删除")
	@ApiOperation(value="需求大厅-批量删除", notes="需求大厅-批量删除")
	//@RequiresPermissions("demand:dam_demand:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.demandService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "需求大厅-通过id查询")
	@ApiOperation(value="需求大厅-通过id查询", notes="需求大厅-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Demand> queryById(@RequestParam(name="id",required=true) String id) {
		Demand demand = demandService.getById(id);
		if(demand==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(demand);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param demand
    */
    //@RequiresPermissions("demand:dam_demand:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Demand demand) {
        return super.exportXls(request, demand, Demand.class, "需求大厅");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("demand:dam_demand:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Demand.class);
    }

}
