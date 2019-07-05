package org.jeecg.modules.news.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.news.dto.response.WordCloudResDto;
import org.jeecg.modules.news.entity.NewsWord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 新闻关键词
 * @Author: jeecg-boot
 * @Date: 2019-06-19
 * @Version: V1.0
 */
public interface NewsWordMapper extends BaseMapper<NewsWord> {
    /**
     * 获取词云数据
     *
     * @return
     */
    List<WordCloudResDto> selectWordColud(@Param("wordId")String wordId);
}
