package top.jianxiaopang.clouddisk.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VerificationCode {
	//验证码图片的长和宽
	private int weight = 100;
	private int height = 30;
	//用来保存验证码的文本内容
	private String text;
	private Random random = new Random();
	//字体数组
	private String[] fontNames = {"宋体", "华文楷体", "黑体", "微软雅黑", "楷体_GB2312"};
	//验证码数组
	private String codes = "123456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";

	/**
	 * 获取随机的颜色
	 */
	private Color randomColor() {
		int red = this.random.nextInt(255);
		int green = this.random.nextInt(255);
		int blue = this.random.nextInt(255);
		return new Color(red, green, blue);
	}

	/**
	 * 获取随机字体
	 */
	private Font randomFont() {
		//获取随机的字体
		int index = random.nextInt(fontNames.length);
		String fontName = fontNames[index];
		//随机获取字体的样式，0是无样式，1是加粗，2是斜体，3是加粗加斜体
		int style = random.nextInt(4);
		//随机获取字体的大小
		int size = random.nextInt(5) + 24;
		return new Font(fontName, style, size);
	}

	/**
	 * 获取随机字符
	 */
	private char randomChar() {
		int index = random.nextInt(codes.length());
		return codes.charAt(index);
	}

	/**
	 * 画干扰线，验证码干扰线用来防止计算机解析图片
	 */
	private void drawLine(BufferedImage image) {
		//干扰线的数量
		int num = 5;
		Graphics2D g = (Graphics2D) image.getGraphics();
		for (int i = 0; i < num; i++) {
			int x1 = random.nextInt(weight);
			int y1 = random.nextInt(height);
			int x2 = random.nextInt(weight);
			int y2 = random.nextInt(height);
			//g.setColor(getRandColor(200, 250));
			g.setColor(randomColor());
			g.drawLine(x1, y1, x2, y2);
		}
	}

	/**
	 * 创建图片的方法
	 */
	private BufferedImage createImage() {
		//创建图片缓冲区
		BufferedImage image = new BufferedImage(weight, height, BufferedImage.TYPE_INT_RGB);
		//获取画笔
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, weight, height);
		return image;
	}

	/**
	 * 获取验证码图片的方法
	 */
	public BufferedImage getImage() {
		BufferedImage image = createImage();
		//获取画笔
		Graphics2D g = (Graphics2D) image.getGraphics();
		StringBuilder sb = new StringBuilder();
		drawLine(image);
		//画四个字符
		for (int i = 0; i < 4; i++) {
			//随机生成字符，因为只有画字符串的方法，没有画字符的方法，所以需要将字符变成字符串再画
			String s = randomChar() + "";
			//添加到StringBuilder里面
			sb.append(s);
			//定义字符的x坐标
			float x = i * 1.0F * weight / 4;
			//设置字体，随机
			g.setFont(randomFont());
			//设置颜色，随机
			g.setColor(randomColor());
			g.drawString(s, x, height - 5);
		}
		this.text = sb.toString();
		return image;
	}


	/**
	 * 得到随机范围内的颜色
	 *
	 * @param min
	 * @param max
	 * @return
	 */
//	Color getRandColor(int min, int max) {
//		Random random = new Random();
//		if (min > 255) {
//			min = 255;
//		}
//		if (max > 255) {
//			max = 255;
//		}
//		int r = min + random.nextInt(max - min);
//		int g = min + random.nextInt(max - min);
//		int b = min + random.nextInt(max - min);
//		return new Color(r, g, b);
//	}

	/**
	 * 获取验证码文本的方法
	 */
	public String getText() {
		return text;
	}
}
