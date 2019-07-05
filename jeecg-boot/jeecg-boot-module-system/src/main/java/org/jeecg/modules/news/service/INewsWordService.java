package org.jeecg.modules.news.service;

import org.jeecg.modules.news.dto.response.WordCloudResDto;
import org.jeecg.modules.news.entity.NewsWord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 新闻关键词
 * @Author: jeecg-boot
 * @Date:   2019-06-19
 * @Version: V1.0
 */
public interface INewsWordService extends IService<NewsWord> {
    /**
     * 获取词云数据
     *
     * @return
     */
    List<WordCloudResDto> selectWordColud(String wordId);
}
