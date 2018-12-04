package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@RequestMapping("/brand")
@RestController
public class BrandController {

	@Reference(timeout=10000)
	private BrandService brandService;

	@RequestMapping("/findAll.do")
	public List<TbBrand> findAll() {

		return brandService.findAll();

	}

	/**
	 * 分页查询
	 * 
	 * @param page
	 *            当前页
	 * @param rows
	 *            每页显示条数
	 * @return
	 */
	@RequestMapping("/findPage.do")
	public PageResult findPage(int page, int rows) {
		PageResult pageResult = brandService.findPage(page, rows);
		return pageResult;
	}

	/**
	 * 品牌添加方法
	 * 
	 * @param tbBrand
	 *            品牌类
	 * @return
	 */

	@RequestMapping("/add.do")
	public Result add(@RequestBody TbBrand tbBrand) {
		try {
			brandService.add(tbBrand);
			return new Result( true,"添加成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"添加失败");
		}

	}
	/**
	 * 查找一个实体类
	 * @param id 品牌id
	 * @return 返回一个品牌实体类
	 */
	@RequestMapping("/findOne.do")
	public TbBrand findOne(long id) {
		System.out.println("传入的id值为"+id);
		return brandService.findOne(id);
	}
	/**
	 * 修改品牌信息
	 * @param tbBrand 品牌实体类
	 * @return
	 */
	@RequestMapping("/update.do")
	public Result update(@RequestBody TbBrand tbBrand) {
		try {
			brandService.update(tbBrand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	
	@RequestMapping("/delete.do")
	public Result delete(long[] ids) {
		try {
			System.out.println(ids);
			for (long l : ids) {
				System.out.println(l);
			}
			brandService.delete(ids);
			
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	
	@RequestMapping("/search.do")
	public PageResult search(@RequestBody TbBrand tbBrand,int page, int rows) {
		PageResult pageResult = brandService.findPage(tbBrand,page, rows);
		return pageResult;
	}
	
	@RequestMapping("/selectOptionList.do")
	public List<Map> selectOptionList(){
		
		return brandService.selectOptionList();
	}

}
