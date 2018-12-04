package com.pinyougou.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;

@RestController
@RequestMapping("/content")
public class ContentController {
	
	@Reference
	private ContentService contentService;
	
	@RequestMapping("/findContentByCategoryId")
	public List<TbContent> findContentByCategoryId(long categoryId) {
		System.out.println(324456);
		 return contentService.findContentByCategoryId(categoryId);
	}


}
