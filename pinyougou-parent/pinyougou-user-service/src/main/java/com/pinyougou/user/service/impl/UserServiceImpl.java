package com.pinyougou.user.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueCodeDestination;
	
	@Value("${template_code}")
	private String template_code;
	@Value("${sign_name}")
	private String sign_name;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		System.out.println(user);
		user.setCreated(new Date());
		user.setUpdated(new Date());
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		userMapper.insert(user);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
	

		@Override
		public void createCode( String phone) {
			//产生验证码
			 String code = 	(long)(Math.random()*1000000)+"";
		//将验证码存入redis
		redisTemplate.boundHashOps("checkCode").put(phone, code);
		//将验证码发送至手机
		System.out.println("验证码:"+code);
		jmsTemplate.send(queueCodeDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage message = session.createMapMessage();
				message.setString("mobile",phone);
				message.setString("template_code",template_code);
				message.setString("sign_name",sign_name);
				Map<String ,String> map = new HashMap<>();
				map.put("code", code);
				message.setString("param",JSON.toJSONString(map));
				return message;
			}
		});
		
		
		}

		@Override
		public boolean checkCode(String phone,String code) {
			if(code==null) {
				return false;
			}
			String checkCode = (String) redisTemplate.boundHashOps("checkCode").get(phone);
			if(!code.equals(checkCode)) {
				return false;
			}
			
			return true;
		}

		@Override
		public PageResult findPage(TbUser user, int pageNum, int pageSize) {
			// TODO Auto-generated method stub
			return null;
		}
	
}
