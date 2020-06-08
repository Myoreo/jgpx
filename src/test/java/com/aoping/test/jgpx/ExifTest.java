package com.aoping.test.jgpx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.aoping.jiguangpx.util.HttpClientUtils;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
 * Created by aoping on May 23, 2020. Email: aoping.xu@plusx.cn Copyright(c)
 * 2014 承影互联(科技)有限公司 版权所有
 */
public class ExifTest {

	public static void main(String[] args)  throws Exception{
		final String jpegPath = "/Users/aoping/Pictures/青海甘肃/DSC0456.ARW";
		File jpegFile = new File(jpegPath);
//		URL url = new URL("http://pic.yun2photo.com/image/test/niaochao.jpg");
//		HttpURLConnection  conn =   (HttpURLConnection) url.openConnection();
//		conn.setDoInput(true);
//		conn.connect();
//		byte[] bs = new byte[1024];
//		InputStream in = conn.getInputStream();
//		int len = 0;
//		OutputStream out = new FileOutputStream(jpegFile);
//		while ((len = in.read(bs)) != -1) {
//			out.write(bs, 0, len);
//		}
//		out.close();
		
		
		Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
		// 输出所有附加属性数据
		for (Directory directory : metadata.getDirectories()) {
			System.out.println("属性组：" + directory.getName());
			for (Tag tag : directory.getTags()) {
				String kv = String.format("%s = %s", tag.getTagName(), tag.getDescription());
				System.out.println(kv);
			}
		}
	}
}
