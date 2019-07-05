package org.jeecg.modules.news.service.impl;

import org.jeecg.modules.news.dto.response.WordCloudResDto;
import org.jeecg.modules.news.entity.NewsWord;
import org.jeecg.modules.news.mapper.NewsWordMapper;
import org.jeecg.modules.news.service.INewsWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 新闻关键词
 * @Author: jeecg-boot
 * @Date:   2019-06-19
 * @Version: V1.0
 */
@Service
public class NewsWordServiceImpl extends ServiceImpl<NewsWordMapper, NewsWord> implements INewsWordService {

    @Autowired
    private NewsWordMapper newsWordMapper;
    @Override
    public List<WordCloudResDto> selectWordColud(String wordId) {
        return newsWordMapper.selectWordColud(wordId);
    }
}
