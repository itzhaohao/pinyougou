package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbBrandMapper brandMaper;

	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		List<TbItem> items = goods.getItems();
		// 设置审核状态
		goods.getTbGoods().setAuditStatus("0");
		// 插入good信息
		goodsMapper.insert(goods.getTbGoods());
		// 插入goodsDesc信息
		goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());
		goodsDescMapper.insert(goods.getTbGoodsDesc());
		//插入ItemsList
		saveItemList(goods);

	}

	private void saveItemList(Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		List<TbItem> items = goods.getItems();

		// item表(sku表)
		if ("1".equals(tbGoods.getIsEnableSpec())) {
			// 商品标题:spu名称+规格选型值
			for (TbItem tbItem : items) {
				String title = tbGoods.getGoodsName();
				Map<String, Object> parseObject = JSON.parseObject(tbItem.getSpec(), Map.class);

				for (String key : parseObject.keySet()) {
					title += parseObject.get(key);
				}
				tbItem.setTitle(title);// 标题
				setItemValues(tbItem, goods);
				itemMapper.insert(tbItem);
			}
		} else {

			TbItem tbItem = new TbItem();
			tbItem.setTitle(tbGoods.getGoodsName());// 标题
			tbItem.setPrice(tbGoods.getPrice());// 价格
			tbItem.setStatus("1");// 是否启用
			tbItem.setIsDefault("1");// 是否默认
			tbItem.setNum(9999);// 库存数量
			tbItem.setSpec("{}");
			setItemValues(tbItem, goods);// 其他设置
			itemMapper.insert(tbItem);// 插入

		}

	}

	private void setItemValues(TbItem tbItem, Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		List<TbItem> items = goods.getItems();
		// categoryId 分类ID
		tbItem.setCategoryid(tbGoods.getCategory3Id());
		// createTime
		tbItem.setCreateTime(new Date());
		// updateTime
		tbItem.setUpdateTime(new Date());
		// goods_id 商品Id
		tbItem.setGoodsId(tbGoods.getId());
		// seller_id 商家ID
		tbItem.setSellerId(tbGoods.getSellerId());
		// category 分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
		tbItem.setCategory(itemCat.getName());
		// brand 品牌名称
		TbBrand brand = brandMaper.selectByPrimaryKey(tbGoods.getBrandId());
		tbItem.setBrand(brand.getName());
		// seller 商家店铺名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
		tbItem.setSeller(seller.getNickName());
		// image
		List<Map> imgList = JSON.parseArray(tbGoodsDesc.getItemImages(), Map.class);
		if (imgList.size() > 0) {

			tbItem.setImage((String) imgList.get(0).get("url"));
		}
	}

	/**
	 * 修改 先删除,后插入
	 */
	@Override
	public void update(Goods goods) {
		//如果修改了数据,需要重新申请
		System.out.println("updateService");
		goods.getTbGoods().setAuditStatus("0");
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());

		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		itemMapper.deleteByExample(example);
		
			saveItemList(goods);

	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();
		goods.setTbGoods(goodsMapper.selectByPrimaryKey(id));
		goods.setTbGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andGoodsIdEqualTo(id);
		List<TbItem> items = itemMapper.selectByExample(example);
		goods.setItems(items);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if (goods != null) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
				// criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
				criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
			}

		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	* 批量修改状态
	* @param ids
	* @param status
	*/
	@Override
	public void updateStatus(long[] ids, String status) {
		for(long id :ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
			
		}
		
		
		
	}
	/**
	 * 运营商审核通过商品,查询结果用于search服务动态存储solr中
	 */
	@Override
	public List<TbItem> findTbItemListByGoodsIdAndAuditStatus(long[] ids,String status) {
		return 	goodsMapper.findTbItemListByGoodsIdAndAuditStatus(ids,status);
	}

}
