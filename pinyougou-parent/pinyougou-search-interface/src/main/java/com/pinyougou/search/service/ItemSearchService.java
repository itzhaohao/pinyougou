package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbItem;

public interface ItemSearchService {

	/**
	 * 搜索
	 * @param keywords
	 * @return
	 */
	public Map<String,Object> search(Map searchMap); 
	
	/**
	 * 运营商审核商品,更新solr
	 * @param tbItemList
	 */
	public void importList(List<TbItem> tbItemList);
	
	/**
	 * 删除索引库数据,更具goodsId
	 * @param ids
	 */
	public void deleteByGoodsIds(List goodsIds);
}
