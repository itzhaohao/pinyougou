package com.pinyougou.page.service;

public interface ItemPageService {
	
	/**
	 * 根据spu的id生成商品详情页
	 * @param goodsId
	 * @return
	 */
	public boolean getItemHtml(Long goodsId);
	
	public boolean deleteItemHtml(Long[] goodsIds);
}
