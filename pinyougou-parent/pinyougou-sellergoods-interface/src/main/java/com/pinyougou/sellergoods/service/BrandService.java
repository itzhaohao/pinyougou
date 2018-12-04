package com.pinyougou.sellergoods.service;


import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;
import entity.Result;



public interface BrandService {
	public List<TbBrand> findAll();
	
	//品牌分页查询
	public PageResult findPage(int pageNum,int pageSize);
	
	//品牌增加
	public void add(TbBrand tbBrand);
	//品牌修改 先查询
	public TbBrand findOne(long id);
	//品牌修改 修改数据库
	public  void update(TbBrand tbBrand);
	//删除
	public void delete(long[] ids);
	//条件查询
	public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);
	/**
	 * select2 查找品牌
	 * @return
	 */
	public List<Map> selectOptionList();
	
}
