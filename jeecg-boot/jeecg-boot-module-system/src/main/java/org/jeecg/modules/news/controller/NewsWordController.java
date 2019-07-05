package org.jeecg.modules.news.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.news.entity.NewsWord;
import org.jeecg.modules.news.service.INewsWordService;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.news.dto.response.*;

/**
 * @Description: 新闻关键词
 * @Author: jeecg-boot
 * @Date: 2019-06-19
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "新闻关键词")
@RestController
@RequestMapping("/news/newsWord")
public class NewsWordController {
    @Autowired
    private INewsWordService newsWordService;


    /**
     * 分页列表查询
     *
     * @param newsWord
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "新闻关键词-分页列表查询")
    @ApiOperation(value = "新闻关键词-分页列表查询", notes = "新闻关键词-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<NewsWord>> queryPageList(NewsWord newsWord,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        Result<IPage<NewsWord>> result = new Result<IPage<NewsWord>>();
        QueryWrapper<NewsWord> queryWrapper = QueryGenerator.initQueryWrapper(newsWord, req.getParameterMap());
        Page<NewsWord> page = new Page<NewsWord>(pageNo, pageSize);
        IPage<NewsWord> pageList = newsWordService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param newsWord
     * @return
     */
    @AutoLog(value = "新闻关键词-添加")
    @ApiOperation(value = "新闻关键词-添加", notes = "新闻关键词-添加")
    @PostMapping(value = "/add")
    public Result<NewsWord> add(@RequestBody NewsWord newsWord) {
        Result<NewsWord> result = new Result<NewsWord>();
        try {
            newsWordService.save(newsWord);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param newsWord
     * @return
     */
    @AutoLog(value = "新闻关键词-编辑")
    @ApiOperation(value = "新闻关键词-编辑", notes = "新闻关键词-编辑")
    @PutMapping(value = "/edit")
    public Result<NewsWord> edit(@RequestBody NewsWord newsWord) {
        Result<NewsWord> result = new Result<NewsWord>();
        NewsWord newsWordEntity = newsWordService.getById(newsWord.getId());
        if (newsWordEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = newsWordService.updateById(newsWord);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "新闻关键词-通过id删除")
    @ApiOperation(value = "新闻关键词-通过id删除", notes = "新闻关键词-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<NewsWord> delete(@RequestParam(name = "id", required = true) String id) {
        Result<NewsWord> result = new Result<NewsWord>();
        NewsWord newsWord = newsWordService.getById(id);
        if (newsWord == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = newsWordService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        }

        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "新闻关键词-批量删除")
    @ApiOperation(value = "新闻关键词-批量删除", notes = "新闻关键词-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<NewsWord> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<NewsWord> result = new Result<NewsWord>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.newsWordService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "新闻关键词-通过id查询")
    @ApiOperation(value = "新闻关键词-通过id查询", notes = "新闻关键词-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<NewsWord> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<NewsWord> result = new Result<NewsWord>();
        NewsWord newsWord = newsWordService.getById(id);
        if (newsWord == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(newsWord);
            result.setSuccess(true);
        }
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
        QueryWrapper<NewsWord> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                NewsWord newsWord = JSON.parseObject(deString, NewsWord.class);
                queryWrapper = QueryGenerator.initQueryWrapper(newsWord, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<NewsWord> pageList = newsWordService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "新闻关键词列表");
        mv.addObject(NormalExcelConstants.CLASS, NewsWord.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("新闻关键词列表数据", "导出人:Jeecg", "导出信息"));
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
                List<NewsWord> listNewsWords = ExcelImportUtil.importExcel(file.getInputStream(), NewsWord.class, params);
                for (NewsWord newsWordExcel : listNewsWords) {
                    newsWordService.save(newsWordExcel);
                }
                return Result.ok("文件导入成功！数据行数:" + listNewsWords.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
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

    @RequestMapping(value = "/newWordCloud", method = RequestMethod.GET)
    public List<WordCloudResDto> newWordCloud(@RequestParam(name = "wordId", required = true) String wordId) {
        if(StringUtils.isEmpty(wordId)){
            return null;
        }
        return newsWordService.selectWordColud(wordId);
    }
}
