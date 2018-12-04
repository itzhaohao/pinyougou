package com.pinyougou.search.service.impl;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemDeleteListener implements MessageListener {
	@Autowired
	private ItemSearchServiceImpl itemSearchServiceImpl;

	@Override
	public void onMessage(Message message) {
	 try {
		 System.out.println("读取中间件信息");
		 ObjectMessage objectMessage = 	 (ObjectMessage) message;
		Long[] ids = (Long[]) objectMessage.getObject();
		itemSearchServiceImpl.deleteByGoodsIds(Arrays.asList(ids));
		System.out.println("删除索引库信息");
	} catch (JMSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
		
	}

}
