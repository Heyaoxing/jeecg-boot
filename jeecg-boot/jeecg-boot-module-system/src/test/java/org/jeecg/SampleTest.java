package org.jeecg;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.jeecg.modules.demo.mock.MockController;
import org.jeecg.modules.demo.test.entity.JeecgDemo;
import org.jeecg.modules.demo.test.mapper.JeecgDemoMapper;
import org.jeecg.modules.demo.test.service.IJeecgDemoService;
import org.jeecg.modules.map.entity.MapInfo;
import org.jeecg.modules.map.mapper.MapInfoMapper;
import org.jeecg.modules.system.service.ISysDataLogService;
import org.jeecg.modules.title.entity.TestTitleInfo;
import org.jeecg.modules.title.mapper.TestTitleInfoMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestBody;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleTest {

	@Resource
	private JeecgDemoMapper jeecgDemoMapper;
	@Resource
	private IJeecgDemoService jeecgDemoService;
	@Resource
	private ISysDataLogService sysDataLogService;
	@Resource
	private MockController mock;

	@Resource
	private MapInfoMapper mapInfoMapper;

	@Resource
	private TestTitleInfoMapper testTitleInfoMapper;
	@Test
	public void testSelect() {
		System.out.println(("----- selectAll method test ------"));
		List<JeecgDemo> userList = jeecgDemoMapper.selectList(null);
		Assert.assertEquals(5, userList.size());
		userList.forEach(System.out::println);
	}

	@Test
	public void testXmlSql() {
		System.out.println(("----- selectAll method test ------"));
		List<JeecgDemo> userList = jeecgDemoMapper.getDemoByName("Sandy12");
		userList.forEach(System.out::println);
	}

	/**
	 * 测试事务
	 */
	@Test
	public void testTran() {
		jeecgDemoService.testTran();
	}
	
	//author:lvdandan-----date：20190315---for:添加数据日志测试----
	/**
	 * 测试数据日志添加
	 */
	@Test
	public void testDataLogSave() {
		System.out.println(("----- datalog test ------"));
		String tableName = "jeecg_demo";
		String dataId = "4028ef81550c1a7901550c1cd6e70001";
		String dataContent = mock.sysDataLogJson();
		sysDataLogService.addDataLog(tableName, dataId, dataContent);
	}
	//author:lvdandan-----date：20190315---for:添加数据日志测试----


	@Test
	public void testMap(){
		getImagePixel("F:\\55791.jpg");
	}

	/**
	 * 读取一张图片的RGB值
	 */
	public void getImagePixel(String image) {

		int[] rgb = new int[3];
		File file = new File(image);
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (IOException e) {

			e.printStackTrace();
		}

		int width = bi.getWidth();
		int height = bi.getHeight();
		int minX = bi.getMinX();
		int minY = bi.getMinY();
		for(int y = minY; y < height; y++) {
			for(int x = minX; x < width; x++) {
				//获取包含这个像素的颜色信息的值, int型
				int pixel = bi.getRGB(x, y);
				//从pixel中获取rgb的值
				rgb[0] = (pixel & 0xff0000) >> 16; //r
				rgb[1] = (pixel & 0xff00) >> 8; //g
				rgb[2] = (pixel & 0xff); //b
				MapInfo mapInfo=new MapInfo();
				mapInfo.setXaxis(x);
				mapInfo.setYaxis(y);
				mapInfo.setGeologyType(2);
				if(rgb[0]==255&&rgb[1]==255&&rgb[2]==255){
					mapInfo.setGeologyType(1);
				}
				mapInfoMapper.insert(mapInfo);
				System.out.print("["+x+","+y+"]"+"("+rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");
			}
			System.out.println();
		}

	}

}
