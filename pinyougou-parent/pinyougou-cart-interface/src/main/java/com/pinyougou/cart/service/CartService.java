package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

public interface CartService {
	//添加商品到cookie
	public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num );
	//从Redis中读取数据
	
	public List<Cart> getCartListFromRedis(String name);
	//向Redis中写数据
	public void addCartListToRedis(String name,List<Cart> cartList);
	//合并购物车(本地购物车和Redis购物车)
	public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
