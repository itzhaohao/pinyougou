package com.pinyougou.shop.controller;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController {
	
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;//文件服务器地址
	@RequestMapping("/upload.do")
	public Result upload(MultipartFile file) {
		//获取文件扩展名
		 String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		
		//2创建FastDFS的客户端
		try {
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			//执行上传处理,返回url地址
			String path = fastDFSClient.uploadFile(file.getBytes(),extName);
			System.out.println(path);
			//拼接url和ip地址,拼装成完整的url
			String url = FILE_SERVER_URL+path;
			System.out.println(url);
			
			return new Result(true,url);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"上传失败");
		}
	}
}
