package org.jeecg;

import org.bytedeco.ffmpeg.global.avcodec;
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
 * @Date: 2019/11/27 17:41
 * @Description:
 */
public class JavaCVTest {
    public static void main(String[] args) throws Exception {
        System.out.println("start...");
        String saveMp4name = "G:\\f1.mp4"; //保存的视频名称
        // 目录中所有的图片，都是jpg的，以1.jpg,2.jpg的方式，方便操作
        String imagesPath = "G:\\png\\"; // 图片集合的目录
        test(saveMp4name, imagesPath);
        System.out.println("end...");
    }

    public static void test(String saveMp4name, String imagesPath) throws Exception {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(saveMp4name, 1920, 1080);
//		recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 28
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // 28
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
        recorder.setFormat("mp4");
        //	recorder.setFormat("flv,mov,mp4,m4a,3gp,3g2,mj2,h264,ogg,MPEG4");
        recorder.setFrameRate(15);
        recorder.setPixelFormat(0); // yuv420p
        recorder.start();
        //
        OpenCVFrameConverter.ToIplImage conveter = new OpenCVFrameConverter.ToIplImage();
        // 列出目录中所有的图片，都是jpg的，以1.jpg,2.jpg的方式，方便操作
        File file = new File(imagesPath);
        File[] flist = file.listFiles();
        // 循环所有图片
        for (int i = 0; i < flist.length; i++) {
            if (i > 21) {
                String fname = imagesPath + "TRANS 38_0000" + String.format("%02d", i) + ".png";
                String fname1 = "G:\\red.jpg";
                BufferedImage bufferedImage = watermark(new File(fname), new File(fname1), 0, 0, 1);
                recorder.record(Java2DFrameUtils.toFrame(toBufferedImage(bufferedImage)));
            } else {
                String fname = imagesPath + "TRANS 38_0000" + String.format("%02d", i) + ".png";
                IplImage image = opencv_imgcodecs.cvLoadImage(fname); // 非常吃内存！！
                recorder.record(conveter.convert(image));
                opencv_core.cvReleaseImage(image);
            }
        }
        recorder.stop();
        recorder.release();
    }


    /**
     * @param file      源文件(图片)
     * @param waterFile 水印文件(图片)
     * @param x         距离右下角的X偏移量
     * @param y         距离右下角的Y偏移量
     * @param alpha     透明度, 选择值从0.0~1.0: 完全透明~完全不透明
     * @return BufferedImage
     * @throws IOException
     * @Title: 构造图片
     * @Description: 生成水印并返回java.awt.image.BufferedImage
     */
    public static BufferedImage watermark(File file, File waterFile, int x, int y, float alpha) throws IOException {
        // 获取底图
        BufferedImage buffImg = ImageIO.read(file);
        // 获取层图
        BufferedImage waterImg = ImageIO.read(waterFile);
        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = buffImg.createGraphics();
        int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
        int waterImgHeight = waterImg.getHeight();// 获取层图的高度
        // 在图形和图像中实现混合和透明效果
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, alpha));
        // 绘制

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
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_3BYTE_BGR); //表示一个具有 8 位 RGB 颜色分量的图像，对应于 Windows 风格的 BGR 颜色模型，具有用 3 字节存储的 Blue、Green 和 Red 三种颜色

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }
}
