package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@Service
public class BrandServiceImpl implements BrandService {
	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

	// 分页查询
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());

	}

	// 添加
	@Override
	public void add(TbBrand tbBrand) {
		brandMapper.insert(tbBrand);

	}

	// 查找一个类
	@Override
	public TbBrand findOne(long id) {
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(id);
		System.out.println(tbBrand);
		return tbBrand;

	}

	// 更新
	@Override
	public void update(TbBrand tbBrand) {
		brandMapper.updateByPrimaryKey(tbBrand);
	}

	// 删除
	@Override
	public void delete(long[] ids) {
		for (long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}

	}

	@Override
	public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if (tbBrand != null) {
			if (tbBrand.getName() != null && tbBrand.getName().length() > 0) {
				criteria.andNameLike("%" + tbBrand.getName() + "%");
			}
			if (tbBrand.getFirstChar() != null && tbBrand.getFirstChar().length() > 0) {
				criteria.andFirstCharLike("%" + tbBrand.getFirstChar() + "%");
			}
		}
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);

		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		List<Map> selectOptionList = brandMapper.selectOptionList();
		return selectOptionList;
	}

}
