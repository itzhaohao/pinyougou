package com.pinyougou.shop.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	private SellerService sellerServie;
	
	public void setSellerServie(SellerService sellerServie) {
		this.sellerServie = sellerServie;
	}


	/**
	 * 认证类
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("经过了UserDetailsServiceImpl.....");
		//构建角色
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		//得到seller对象 
		TbSeller seller = sellerServie.findOne(username);
		String password = seller.getPassword();
		if(seller!=null&&seller.getStatus().equals("1")) {
			return new User(username,password,authorities);
		}else {
			return null;
		}
	}

}
