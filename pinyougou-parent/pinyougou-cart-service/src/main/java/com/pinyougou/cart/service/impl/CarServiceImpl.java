package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

@Service(timeout=10000)
public class CarServiceImpl implements CartService {
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private RedisTemplate reidsTemplate;

	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		// 1.根据商品 SKU ID 查询 SKU 商品信息
//		System.out.println(cartList);
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item == null) {
			throw new RuntimeException("商品不存在");
		}
		if (!item.getStatus().equals("1")) {
			throw new RuntimeException("商品状态无效");
		}
		// 2.获取商家 ID
		String sellerId = item.getSellerId();
		// 3.根据商家 ID 判断购物车列表中是否存在该商家的购物车
		Cart cart = searchCartBySellerId(cartList, sellerId);
		// 4.如果购物车列表中不存在该商家的购物车
		if (cart == null) {
			// 4.1 新建购物车对象 ，
			cart = new Cart();
			cart.setSellerId(sellerId);
			cart.setSellerName(item.getSeller());
			TbOrderItem orderItem = createOrderItem(item, num);
			List orderItemList = new ArrayList();
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList);
			// 4.2 将购物车对象添加到购物车列表
//			System.out.println("123"+cart);
			cartList.add(cart);
		} else {
			// 5.如果购物车列表中存在该商家的购物车
			// 判断购物车明细列表中是否存在该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
			if (orderItem == null) {
				// 5.1. 如果没有，新增购物车明细
				orderItem = createOrderItem(item, num);
				cart.getOrderItemList().add(orderItem);
			} else {
				// 5.2. 如果有，在原购物车明细上添加数量，更改金额
				orderItem.setNum(orderItem.getNum() + num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));
				// 如果数量操作后小于等于 0，则移除
				if (orderItem.getNum() <= 0) {
					cart.getOrderItemList().remove(orderItem);// 移除购物车明细
				}
				// 如果移除后 cart 的明细数量为 0，则将 cart 移除
				if (cart.getOrderItemList().size() == 0) {
					cartList.remove(cart);
				}
			}
		}
		return cartList;
	}

	/**
	 * 根据商家 ID 查询购物车对象
	 * 
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
		if(cartList!=null) {
		for (Cart cart : cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
	}
		return null;
	}

	/**
	 * 根据商品明细 ID 查询
	 * 
	 * @param orderItemList
	 * @param itemId
	 * @return
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
		for (TbOrderItem orderItem : orderItemList) {
			if (orderItem.getItemId().longValue() == itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}

	/**
	 * 创建订单明细
	 * 
	 * @param item
	 * @param num
	 * @return
	 */
	private TbOrderItem createOrderItem(TbItem item, Integer num) {
		if (num <= 0) {
			throw new RuntimeException("数量非法");
		}
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
		return orderItem;
	}

	@Override
	public List<Cart> getCartListFromRedis(String name) {
		List<Cart> cartList =	(List<Cart>) reidsTemplate.boundHashOps("cart_Redis").get(name);
//		System.out.println("Redis读"+cartList);
		if(cartList==null) {
			cartList = new ArrayList<>();
		}
		return cartList;
	}

	@Override
	public void addCartListToRedis(String name, List<Cart> cartList) {
//		System.out.println("Redis写"+cartList);
		reidsTemplate.boundHashOps("cart_Redis").put(name, cartList);
	}

	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		List<Cart> cartList = new ArrayList<>();
		for(Cart cart : cartList2) {
			for(TbOrderItem orderItem : cart.getOrderItemList()) {
				 cartList = addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
			}
		}
		return cartList;
	}
}
