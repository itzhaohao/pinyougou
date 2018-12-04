package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class ItemPageServiceImpl implements ItemPageService{
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@Value("${outName}")
	private String outName;
	@Autowired
	private TbGoodsMapper goodsMapper;
	
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	
	@Autowired
	private TbItemMapper tbItemMapper;
	

	@Override
	public boolean getItemHtml(Long goodsId) {
		//配置对象
		Configuration configuration = freeMarkerConfigurer.getConfiguration();
		try {
		//模板对象
		Template template = configuration.getTemplate("item.ftl");
		//数据模型
		Map<String ,Object> dataModel = new HashMap<>();	
		
		TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
		
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
		
		TbItemCat itemCat1 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
		TbItemCat itemCat2 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
		TbItemCat itemCat3 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
		
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goods.getId());
		createCriteria.andStatusEqualTo("1");
		example.setOrderByClause("is_default desc");
		List<TbItem> itemList = tbItemMapper.selectByExample(example);
		System.out.println(itemList);
		
		dataModel.put("goods", goods);
		
		dataModel.put("goodsDesc", goodsDesc);
		
		dataModel.put("itemCat1", itemCat1);
		dataModel.put("itemCat2", itemCat2);
		dataModel.put("itemCat3", itemCat3);
		dataModel.put("itemList", itemList);
		
		//??????
		Writer out = new FileWriter(outName+goodsId+".html");
		template.process(dataModel, out);	
		out.close();
		return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		
	}


	@Override
	public boolean deleteItemHtml(Long[] goodsIds) {
		try {
			for(Long goodsId : goodsIds) {
				new File(outName+goodsId+".html").delete();
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}


}
