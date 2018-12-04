package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

public class Goods implements Serializable {
	private TbGoods tbGoods;
	private TbGoodsDesc tbGoodsDesc;
	private List<TbItem> items;
	public TbGoods getTbGoods() {
		return tbGoods;
	}
	public void setTbGoods(TbGoods tbGoods) {
		this.tbGoods = tbGoods;
	}
	public TbGoodsDesc getTbGoodsDesc() {
		return tbGoodsDesc;
	}
	public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
		this.tbGoodsDesc = tbGoodsDesc;
	}
	public List<TbItem> getItems() {
		return items;
	}
	public void setItems(List<TbItem> items) {
		this.items = items;
	}
	

	

}
