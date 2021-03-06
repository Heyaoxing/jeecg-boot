package org.jeecg.modules.spider.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import org.jeecg.common.api.vo.KeyValue;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.news.entity.NewsWord;
import org.jeecg.modules.news.service.INewsWordService;
import org.jeecg.modules.spider.dto.KrRecordResDto;
import org.jeecg.modules.spider.entity.KrRecord;
import org.jeecg.modules.spider.service.IKrRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 36氪爬虫抓取
 * @Author: jeecg-boot
 * @Date: 2019-06-18
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "36氪爬虫抓取")
@RestController
@RequestMapping("/spider/krRecord")
public class KrRecordController {
    @Autowired
    private IKrRecordService krRecordService;

    @Autowired
    private INewsWordService newsWordService;

    /**
     * 分页列表查询
     *
     * @param krRecord
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "36氪爬虫抓取-分页列表查询")
    @ApiOperation(value = "36氪爬虫抓取-分页列表查询", notes = "36氪爬虫抓取-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<KrRecordResDto>> queryPageList(KrRecord krRecord,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        Result<IPage<KrRecordResDto>> result = new Result<>();
        QueryWrapper<KrRecord> queryWrapper = QueryGenerator.initQueryWrapper(krRecord, req.getParameterMap());
        Page<KrRecord> page = new Page<KrRecord>(pageNo, pageSize);
        IPage<KrRecord> pageList = krRecordService.page(page, queryWrapper);
        result.setSuccess(true);
        Page<KrRecordResDto> pages = new Page<>(pageNo, pageSize,pageList.getTotal(),pageList.isSearchCount());

        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            result.setResult(pages);
            return result;
        }
        pages.setCurrent(pageList.getCurrent());
        List<KrRecordResDto> krRecords = new ArrayList<>();
        for (KrRecord item : pageList.getRecords()) {
            KrRecordResDto dto=new KrRecordResDto();
            BeanUtils.copyProperties(item,dto);
            LambdaQueryWrapper<NewsWord> query = new LambdaQueryWrapper<NewsWord>()
                    .eq(NewsWord::getNewId, item.getId());
            List<NewsWord> newsWords = newsWordService.list(query);
            if(!CollectionUtils.isEmpty(newsWords)){
                List<KeyValue> words=new ArrayList<>(newsWords.size());
                newsWords.forEach(p->words.add(new KeyValue(p.getWord(),p.getWordId())));
                dto.setWords(words);
            }
            krRecords.add(dto);
        }
        pages.setRecords(krRecords);
        result.setResult(pages);
        return result;
    }

    /**
     * 添加
     *
     * @param krRecord
     * @return
     */
    @AutoLog(value = "36氪爬虫抓取-添加")
    @ApiOperation(value = "36氪爬虫抓取-添加", notes = "36氪爬虫抓取-添加")
    @PostMapping(value = "/add")
    public Result<KrRecord> add(@RequestBody KrRecord krRecord) {
        Result<KrRecord> result = new Result<KrRecord>();
        try {
            krRecordService.save(krRecord);
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
     * @param krRecord
     * @return
     */
    @AutoLog(value = "36氪爬虫抓取-编辑")
    @ApiOperation(value = "36氪爬虫抓取-编辑", notes = "36氪爬虫抓取-编辑")
    @PutMapping(value = "/edit")
    public Result<KrRecord> edit(@RequestBody KrRecord krRecord) {
        Result<KrRecord> result = new Result<KrRecord>();
        KrRecord krRecordEntity = krRecordService.getById(krRecord.getId());
        if (krRecordEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = krRecordService.updateById(krRecord);
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
    @AutoLog(value = "36氪爬虫抓取-通过id删除")
    @ApiOperation(value = "36氪爬虫抓取-通过id删除", notes = "36氪爬虫抓取-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<KrRecord> delete(@RequestParam(name = "id", required = true) String id) {
        Result<KrRecord> result = new Result<KrRecord>();
        KrRecord krRecord = krRecordService.getById(id);
        if (krRecord == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = krRecordService.removeById(id);
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
    @AutoLog(value = "36氪爬虫抓取-批量删除")
    @ApiOperation(value = "36氪爬虫抓取-批量删除", notes = "36氪爬虫抓取-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<KrRecord> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<KrRecord> result = new Result<KrRecord>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.krRecordService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "36氪爬虫抓取-通过id查询")
    @ApiOperation(value = "36氪爬虫抓取-通过id查询", notes = "36氪爬虫抓取-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<KrRecord> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<KrRecord> result = new Result<KrRecord>();
        KrRecord krRecord = krRecordService.getById(id);
        if (krRecord == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(krRecord);
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
        QueryWrapper<KrRecord> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                KrRecord krRecord = JSON.parseObject(deString, KrRecord.class);
                queryWrapper = QueryGenerator.initQueryWrapper(krRecord, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<KrRecord> pageList = krRecordService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "36氪爬虫抓取列表");
        mv.addObject(NormalExcelConstants.CLASS, KrRecord.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("36氪爬虫抓取列表数据", "导出人:Jeecg", "导出信息"));
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
                List<KrRecord> listKrRecords = ExcelImportUtil.importExcel(file.getInputStream(), KrRecord.class, params);
                for (KrRecord krRecordExcel : listKrRecords) {
                    krRecordService.save(krRecordExcel);
                }
                return Result.ok("文件导入成功！数据行数:" + listKrRecords.size());
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

}
