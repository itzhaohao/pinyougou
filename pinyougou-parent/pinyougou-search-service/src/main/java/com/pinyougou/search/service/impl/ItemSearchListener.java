package com.pinyougou.search.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.sql.ast.expr.SQLCaseExpr.Item;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;

@Component
public class ItemSearchListener implements MessageListener {
	
	@Autowired
	private ItemSearchServiceImpl itemSearchServiceImpl;

	@Override
	public void onMessage(Message message) {
		System.out.println("监听接收到消息");
		try {
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			 List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
			 System.out.println(itemList);
			itemSearchServiceImpl.importList(itemList);
			System.out.println("成功导入索引库");
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
