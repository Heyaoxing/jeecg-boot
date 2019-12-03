package org.jeecg;

import org.apache.commons.lang.StringUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.jeecg.model.AssestEntity;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: 002954
 * @Date: 2019/11/29 16:29
 * @Description:
 */
public class NewVideoTest {
    public static String SAVEMP4NAME = "G:\\f2.mp4";
    // 540/960，720/1280，1080/1920
    public static int width=720;
    public static int height=1280;

    public static void main(String[] args) throws Exception {
        List<AssestEntity> assests = new ArrayList();
        assests.add(new AssestEntity("黄奇帆谈“中小企业”融资难 或因实体经济信用不到位", "G:\\result\\1.jpg"));
        assests.add(new AssestEntity("吉林省人民检察院原检察长杨克勤被双开", "G:\\result\\2.jpg"));
        assests.add(new AssestEntity("《环球时报》英文版发推：北京消息人士向《环球时报》表示，中国坚持必须将撤销关税作为第一阶段贸易协议的一部分。 美国若取消原定于12月15日实施的对华关税，不能替代关税的撤销", "G:\\result\\3.jpg"));
        assests.add(new AssestEntity("瑞典环保少女被写入中学教材 被描述成“圣人”", "G:\\result\\4.jpg"));

        System.out.println("start...");
        create(200, assests);
        System.out.println("end...");
    }

    public static void create(int rate, List<AssestEntity> list) throws IOException {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(SAVEMP4NAME, width, height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
        recorder.setFormat("mp4");
        recorder.setFrameRate(10);
        recorder.setVideoBitrate(8000000); //8000kb/s 这个说明视频每秒大小，值越大图片转过来的压缩率就越小质量就会越高
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P); // yuv420p  先默认吧，这个应该属于设置视频的处理模式吧
        // 不可变(固定)音频比特率
        recorder.setAudioOption("crf", "0");
        // 最高质量
        recorder.setAudioQuality(0);
        // 音频比特率
        recorder.setAudioBitrate(192000);
        //音频采样率
        recorder.setSampleRate(44100);
        // 双通道(立体声)
        recorder.setAudioChannels(2);
        recorder.start();

        String mp3Path = "G:\\音乐\\周杰伦\\一路向北_周杰伦_无损.flac";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(mp3Path);
        grabber.start();// 开始录制音频
        int index = 0;
        for (int i = 0; i < rate; i++) {
            if ((rate / list.size()) * (index + 1) < i && index < list.size() - 1) {
                index = index + 1;
            }

            recorder.record(grabber.grab()); // 录入音频
            BufferedImage base = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            BufferedImage bufferedImage = watermark(base, ImageIO.read(new File(list.get(index).getPath())), list.get(index).getTitle(), 1);
            recorder.record(Java2DFrameUtils.toFrame(toBufferedImage(bufferedImage)));

        }
        grabber.stop();
        grabber.release();
        recorder.stop();
        recorder.release();
    }


    /**
     * @param x     距离右下角的X偏移量
     * @param y     距离右下角的Y偏移量
     * @param alpha 透明度, 选择值从0.0~1.0: 完全透明~完全不透明
     * @return BufferedImage
     * @throws IOException
     * @Title: 构造图片
     * @Description: 生成水印并返回java.awt.image.BufferedImage
     */
    public static BufferedImage watermark(BufferedImage buffImg, BufferedImage waterImg, String text, float alpha) {
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = buffImg.createGraphics();
        int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
        int waterImgHeight = waterImg.getHeight();// 获取层图的高度
        // 在图形和图像中实现混合和透明效果
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, alpha));

        if (StringUtils.isNotBlank(text)) {
            Font font = new Font("宋体", Font.BOLD, 50);
            g2d.setFont(font);
            g2d.setColor(Color.cyan);
            drawString(g2d, font, text, 10, buffImg.getHeight() - 100, buffImg.getWidth() - 10);
        }
        // 绘制
        int x = (buffImg.getWidth() - waterImg.getWidth()) / 2;
        int y = (buffImg.getHeight() - waterImg.getHeight()) / 2;
        g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
        g2d.dispose();// 释放图形上下文使用的系统资源
        return buffImg;
    }


    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_3BYTE_BGR); //表示一个具有 8 位 RGB 颜色分量的图像，对应于 Windows 风格的 BGR 颜色模型，具有用 3 字节存储的 Blue、Green 和 Red 三种颜色
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }


    public static void drawString(Graphics2D g, Font font, String text, int x, int y, int maxWidth) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        FontMetrics metrics = label.getFontMetrics(label.getFont());
        int textH = metrics.getHeight();
        int textW = metrics.stringWidth(label.getText()); //字符串的宽
        String tempText = text;
        int line = textW / maxWidth;
        y = y - line * textH;
        while (textW > maxWidth) {
            int n = textW / maxWidth;
            int subPos = tempText.length() / n;
            String drawText = tempText.substring(0, subPos);
            int subTxtW = metrics.stringWidth(drawText);
            while (subTxtW > maxWidth) {
                subPos--;
                drawText = tempText.substring(0, subPos);
                subTxtW = metrics.stringWidth(drawText);
            }
            g.drawString(drawText, x, y);
            y += textH;
            textW = textW - subTxtW;
            tempText = tempText.substring(subPos);
        }
        g.drawString(tempText, x, y);
    }
}
