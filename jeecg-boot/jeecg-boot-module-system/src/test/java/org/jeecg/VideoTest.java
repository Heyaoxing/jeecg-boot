package org.jeecg;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @Auther: 002954
 * @Date: 2019/11/29 16:29
 * @Description:
 */
public class VideoTest {
    public static String SAVEMP4NAME = "G:\\f1.mp4";
    public static String IMAGESPATH = "G:\\png\\";
    public static String VIDEOPATH = "G:\\aigei.mov";

    public static void main(String[] args) throws Exception {
        System.out.println("start...");
        create(100);
        System.out.println("end...");
    }

    public static void create(int rate) throws IOException {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(SAVEMP4NAME, 1920, 1080);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
        recorder.setFormat("mp4");
        recorder.setFrameRate(15);
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

        File file = new File(IMAGESPATH);
        File[] flist = file.listFiles();

        OpenCVFrameConverter.ToIplImage conveter = new OpenCVFrameConverter.ToIplImage();

        FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(VIDEOPATH);
        ff.start();

        String mp3Path = "G:\\音乐\\周杰伦\\一路向北_周杰伦_无损.flac";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(mp3Path);
        grabber.start();// 开始录制音频

        for (int i = 0; i < rate; i++) {
            recorder.record( grabber.grab()); // 录入音频
            if (flist.length > i) {
                if (i < 21) {
                    String fname = IMAGESPATH + "TRANS 38_0000" + String.format("%02d", i) + ".png";
                    IplImage image = opencv_imgcodecs.cvLoadImage(fname); // 非常吃内存！！
                    recorder.record(conveter.convert(image));
                    opencv_core.cvReleaseImage(image);
                    continue;
                } else {
                    String fname = IMAGESPATH + "TRANS 38_0000" + String.format("%02d", i) + ".png";
                    Frame frame = ff.grabImage();
                    BufferedImage bufferedImage = watermark(ImageIO.read(new File(fname)), Java2DFrameUtils.toBufferedImage(frame), 1);
                    recorder.record(Java2DFrameUtils.toFrame(toBufferedImage(bufferedImage)));
                    continue;
                }
            }

            Frame frame = ff.grabImage();
            BufferedImage base = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
            BufferedImage bufferedImage = watermark(base, Java2DFrameUtils.toBufferedImage(frame), 1);
            recorder.record(Java2DFrameUtils.toFrame(toBufferedImage(bufferedImage)));

        }
        ff.stop();
        ff.release();
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
    public static BufferedImage watermark(BufferedImage buffImg, BufferedImage waterImg, float alpha) {
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = buffImg.createGraphics();
        int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
        int waterImgHeight = waterImg.getHeight();// 获取层图的高度
        // 在图形和图像中实现混合和透明效果
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, alpha));
        Font font = new Font("宋体", Font.PLAIN, 64);
        g2d.setFont(font);
        g2d.drawString("hello world", 100, buffImg.getHeight() - 100);
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
}
