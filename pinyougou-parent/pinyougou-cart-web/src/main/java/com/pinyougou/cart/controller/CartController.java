package com.pinyougou.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;
	
	@Reference
	private CartService carservice;
	
	@RequestMapping("/findCartListByCookie")
	public List<Cart> findCartListByCookie(){
		String cookie_value = CookieUtil.getCookieValue(request, "cookie_value", "UTF-8");
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if(cookie_value==null||"".equals(cookie_value)) {
			cookie_value="[]";
		}
		List<Cart> cookie_cartList = (List<Cart>) JSON.parseArray(cookie_value, Cart.class);
		System.out.println(name);
		if("anonymousUser".equals(name)) {//未登录
			System.out.println("从cookie中读取数据:"+cookie_cartList);
			return cookie_cartList;
		}else {//已登录
			List<Cart> cartList = carservice.getCartListFromRedis(name);
			System.out.println("从redis中读取cartList"+cartList);
			if(cartList == null) {
				cartList = new ArrayList<Cart>();
			}
			//如果cookie中有数据,执行合并,否则不执行
			if(cookie_cartList.size()>0 && cookie_cartList!=null) {
				List<Cart> mergeCartList = carservice.mergeCartList(cookie_cartList, cartList);
				System.out.println("执行了合并操作,结果为:"+mergeCartList);
				return mergeCartList;	
			}
			return cartList;
		}
	}
	@CrossOrigin(origins = "http://location:9105")
	@RequestMapping("/addCartList")
	public Result addCartList(Long itemId, Integer num) {
//		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
//		response.setHeader("Access-Control-Allow-Credentials", "true");
		try {
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			List<Cart> cartList = findCartListByCookie();
			if("anonymousUser".equals(name)) {//未登录
				//从cookie取值
				//存入购物车
				cartList = carservice.addGoodsToCartList(cartList, itemId, num);
				//结果值存入cookie
				String cookieValue = JSON.toJSONString(cartList);
				CookieUtil.setCookie(request, response, "cookie_value", cookieValue, 3600*24, "UTF-8");
				System.out.println("向cookie中写数据:"+cookieValue);
			}else {//已登录
				cartList = carservice.addGoodsToCartList(cartList, itemId, num);
				carservice.addCartListToRedis(name, cartList);
				System.out.println("向redis中写入cartList:"+cartList);
			}
			return new Result(true,"存入购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"存入购物车失败");
		}
		
	}
	
}
