package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageListener implements MessageListener {
	@Autowired
	private ItemPageServiceImpl itemPageServiceImpl;

	@Override
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			System.out.println("接收消息:"+text);
			itemPageServiceImpl.getItemHtml(Long.parseLong(text));
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

	

}
