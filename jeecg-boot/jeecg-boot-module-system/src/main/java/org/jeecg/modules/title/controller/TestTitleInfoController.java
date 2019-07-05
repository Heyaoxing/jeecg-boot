package org.jeecg.modules.title.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.title.entity.TestUserInfo;
import org.jeecg.modules.title.entity.TestTitleOrder;
import org.jeecg.modules.title.entity.TestTitleInfo;
import org.jeecg.modules.title.vo.TestTitleInfoPage;
import org.jeecg.modules.title.service.ITestTitleInfoService;
import org.jeecg.modules.title.service.ITestUserInfoService;
import org.jeecg.modules.title.service.ITestTitleOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;

 /**
 * @Description: 标题
 * @Author: jeecg-boot
 * @Date:   2019-06-14
 * @Version: V1.0
 */
@RestController
@RequestMapping("/title/testTitleInfo")
@Slf4j
public class TestTitleInfoController {
	@Autowired
	private ITestTitleInfoService testTitleInfoService;
	@Autowired
	private ITestUserInfoService testUserInfoService;
	@Autowired
	private ITestTitleOrderService testTitleOrderService;
	
	/**
	  * 分页列表查询
	 * @param testTitleInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<TestTitleInfo>> queryPageList(TestTitleInfo testTitleInfo,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<TestTitleInfo>> result = new Result<IPage<TestTitleInfo>>();
		QueryWrapper<TestTitleInfo> queryWrapper = QueryGenerator.initQueryWrapper(testTitleInfo, req.getParameterMap());
		Page<TestTitleInfo> page = new Page<TestTitleInfo>(pageNo, pageSize);
		IPage<TestTitleInfo> pageList = testTitleInfoService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param testTitleInfoPage
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<TestTitleInfo> add(@RequestBody TestTitleInfoPage testTitleInfoPage) {
		Result<TestTitleInfo> result = new Result<TestTitleInfo>();
		try {
			TestTitleInfo testTitleInfo = new TestTitleInfo();
			BeanUtils.copyProperties(testTitleInfoPage, testTitleInfo);
			
			testTitleInfoService.saveMain(testTitleInfo, testTitleInfoPage.getTestUserInfoList(),testTitleInfoPage.getTestTitleOrderList());
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param testTitleInfoPage
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<TestTitleInfo> edit(@RequestBody TestTitleInfoPage testTitleInfoPage) {
		Result<TestTitleInfo> result = new Result<TestTitleInfo>();
		TestTitleInfo testTitleInfo = new TestTitleInfo();
		BeanUtils.copyProperties(testTitleInfoPage, testTitleInfo);
		TestTitleInfo testTitleInfoEntity = testTitleInfoService.getById(testTitleInfo.getId());
		if(testTitleInfoEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = testTitleInfoService.updateById(testTitleInfo);
			testTitleInfoService.updateMain(testTitleInfo, testTitleInfoPage.getTestUserInfoList(),testTitleInfoPage.getTestTitleOrderList());
			result.success("修改成功!");
		}
		
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<TestTitleInfo> delete(@RequestParam(name="id",required=true) String id) {
		Result<TestTitleInfo> result = new Result<TestTitleInfo>();
		TestTitleInfo testTitleInfo = testTitleInfoService.getById(id);
		if(testTitleInfo==null) {
			result.error500("未找到对应实体");
		}else {
			testTitleInfoService.delMain(id);
			result.success("删除成功!");
		}
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<TestTitleInfo> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<TestTitleInfo> result = new Result<TestTitleInfo>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.testTitleInfoService.delBatchMain(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<TestTitleInfo> queryById(@RequestParam(name="id",required=true) String id) {
		Result<TestTitleInfo> result = new Result<TestTitleInfo>();
		TestTitleInfo testTitleInfo = testTitleInfoService.getById(id);
		if(testTitleInfo==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(testTitleInfo);
			result.setSuccess(true);
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryTestUserInfoByMainId")
	public Result<List<TestUserInfo>> queryTestUserInfoListByMainId(@RequestParam(name="id",required=true) String id) {
		Result<List<TestUserInfo>> result = new Result<List<TestUserInfo>>();
		List<TestUserInfo> testUserInfoList = testUserInfoService.selectByMainId(id);
		result.setResult(testUserInfoList);
		result.setSuccess(true);
		return result;
	}
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryTestTitleOrderByMainId")
	public Result<List<TestTitleOrder>> queryTestTitleOrderListByMainId(@RequestParam(name="id",required=true) String id) {
		Result<List<TestTitleOrder>> result = new Result<List<TestTitleOrder>>();
		List<TestTitleOrder> testTitleOrderList = testTitleOrderService.selectByMainId(id);
		result.setResult(testTitleOrderList);
		result.setSuccess(true);
		return result;
	}

  /**
      * 导出excel
   *
   * @param request
   * @param response
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
      // Step.1 组装查询条件
      QueryWrapper<TestTitleInfo> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              TestTitleInfo testTitleInfo = JSON.parseObject(deString, TestTitleInfo.class);
              queryWrapper = QueryGenerator.initQueryWrapper(testTitleInfo, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<TestTitleInfoPage> pageList = new ArrayList<TestTitleInfoPage>();
      List<TestTitleInfo> testTitleInfoList = testTitleInfoService.list(queryWrapper);
      for (TestTitleInfo testTitleInfo : testTitleInfoList) {
          TestTitleInfoPage vo = new TestTitleInfoPage();
          BeanUtils.copyProperties(testTitleInfo, vo);
          List<TestUserInfo> testUserInfoList = testUserInfoService.selectByMainId(testTitleInfo.getId());
          vo.setTestUserInfoList(testUserInfoList);
          List<TestTitleOrder> testTitleOrderList = testTitleOrderService.selectByMainId(testTitleInfo.getId());
          vo.setTestTitleOrderList(testTitleOrderList);
          pageList.add(vo);
      }
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "标题列表");
      mv.addObject(NormalExcelConstants.CLASS, TestTitleInfoPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("标题列表数据", "导出人:Jeecg", "导出信息"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
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
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          MultipartFile file = entity.getValue();// 获取上传文件对象
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<TestTitleInfoPage> list = ExcelImportUtil.importExcel(file.getInputStream(), TestTitleInfoPage.class, params);
              for (TestTitleInfoPage page : list) {
                  TestTitleInfo po = new TestTitleInfo();
                  BeanUtils.copyProperties(page, po);
                  testTitleInfoService.saveMain(po, page.getTestUserInfoList(),page.getTestTitleOrderList());
              }
              return Result.ok("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.ok("文件导入失败！");
  }

}
