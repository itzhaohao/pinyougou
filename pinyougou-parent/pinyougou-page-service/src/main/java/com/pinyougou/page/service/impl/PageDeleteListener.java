package com.pinyougou.page.service.impl;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageDeleteListener implements MessageListener {
	
	@Autowired
	private ItemPageServiceImpl itemPageServiceImpl;
	

	@Override
	public void onMessage(Message message) {
		try {
			ObjectMessage objectMessage = (ObjectMessage) message;
			Long[] goodsIds = (Long[]) objectMessage.getObject();
			System.out.println("接收消息");
			boolean b = itemPageServiceImpl.deleteItemHtml(goodsIds);
			System.out.println("网页删除结果:"+b);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
