package org.jeecg.modules.quartz.job;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.mchange.v2.uid.UidUtils;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.news.entity.NewsWord;
import org.jeecg.modules.news.service.INewsWordService;
import org.jeecg.modules.spider.entity.KrRecord;
import org.jeecg.modules.spider.service.IKrRecordService;
import org.jeecg.modules.word.entity.WordInfo;
import org.jeecg.modules.word.service.IWordInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 示例不带参定时任务
 *
 * @Author Scott
 */
@Slf4j
public class AnalyzeWordJob implements Job {
    @Autowired
    private IKrRecordService krRecordService;

    @Autowired
    private IWordInfoService wordInfoService;

    @Autowired
    private INewsWordService newsWordService;

    private static List<Nature> filterNatures = new ArrayList<Nature>(Arrays.asList(Nature.bg, Nature.mg, Nature.qg, Nature.ud, Nature.uj, Nature.uz, Nature.ug, Nature.ul,
            Nature.uv, Nature.yg, Nature.zg, Nature.f, Nature.vyou, Nature.vshi, Nature.vf, Nature.vl, Nature.vg, Nature.ad,
            Nature.qt, Nature.d, Nature.dg, Nature.dl, Nature.p, Nature.pba, Nature.pbei, Nature.c, Nature.cc, Nature.u, Nature.uzhe,
            Nature.ule, Nature.uguo, Nature.ude1, Nature.ude2, Nature.ude3, Nature.usuo, Nature.udeng, Nature.udh, Nature.uls,
            Nature.uzhi, Nature.ulian, Nature.e, Nature.y, Nature.h, Nature.k, Nature.x, Nature.xx, Nature.xu, Nature.w, Nature.wkz,
            Nature.wky, Nature.wyz, Nature.wyy, Nature.wj, Nature.ww, Nature.wt, Nature.wd, Nature.wf, Nature.wn, Nature.wm, Nature.ws,
            Nature.wp, Nature.wb, Nature.wh, Nature.end, Nature.begin, Nature.vi, Nature.rr, Nature.v, Nature.vn, Nature.b
            , Nature.nx, Nature.q, Nature.m, Nature.mq, Nature.ng, Nature.a, Nature.rz
    ));
    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (StringUtils.isEmpty(parameter)) {
            parameter = "10";
        }

        int size = Integer.parseInt(parameter);
        log.info(" 开始分词,每次分词数:" + parameter);
        long start=System.currentTimeMillis();
        process(size);
        log.info(" 结束分词 cost:"+(System.currentTimeMillis()-start));
    }

    private void process(int size) {
        List<KrRecord> records = krRecordService.selectNotAnalyze(size);
        if (CollectionUtils.isEmpty(records)) {
            log.info("未找到没分词的新闻");
            return;
        }

        for (KrRecord item : records) {
            List<Term> terms = HanLP.segment(item.getDescription());
            if (CollectionUtils.isEmpty(terms))
                continue;
            insert(terms, item);
        }
    }

    private void insert(List<Term> terms, KrRecord krRecord) {
        List<Term> filters = terms.stream().filter(p -> !filterNatures.contains(p.nature)).collect(Collectors.toList());
        List<WordInfo> wordInfos = new ArrayList<>();
        List<NewsWord> newsWords = new ArrayList<>();
        for (Term item : filters) {

            if(item.word.length()==1)continue;

            NewsWord newsWord = new NewsWord();
            newsWord.setWord(item.word);
            newsWord.setNewId(krRecord.getId());
            newsWord.setNewsCreateTime(krRecord.getPublishedAt());
            newsWord.setCreateTime(new Date());

            LambdaQueryWrapper<WordInfo> query = new LambdaQueryWrapper<WordInfo>()
                    .eq(WordInfo::getWord, item.word);
            WordInfo word = wordInfoService.getOne(query);
            if (Objects.isNull(word)) {
                WordInfo wordInfo = new WordInfo();
                wordInfo.setId(UUIDGenerator.generate());
                wordInfo.setWord(item.word);
                wordInfo.setCreateTime(new Date());
                wordInfo.setWordType(item.nature.toString());
                wordInfos.add(wordInfo);

                newsWord.setWordId(wordInfo.getId());
            }else{
                newsWord.setWordId(word.getId());
            }

            newsWords.add(newsWord);

        }

        if (!CollectionUtils.isEmpty(wordInfos)) {
            wordInfoService.saveBatch(wordInfos);
        }

        LambdaQueryWrapper<NewsWord> query = new LambdaQueryWrapper<NewsWord>()
                .eq(NewsWord::getNewId, krRecord.getId());
        newsWordService.remove(query);
        //保存关联词
        newsWordService.saveBatch(newsWords);
        //更新分析状态
        krRecordService.update(new KrRecord().setIsAnalyze(1), new LambdaQueryWrapper<KrRecord>().eq(KrRecord::getId, krRecord.getId()));

        log.info("新闻:{} 分词数:{}",krRecord.getId(),newsWords.size());
    }
}
