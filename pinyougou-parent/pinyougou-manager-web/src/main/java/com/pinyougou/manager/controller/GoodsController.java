package com.pinyougou.manager.controller;

import java.util.Arrays;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

/**
 * controller
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private Destination queueSolrDestination;
	
	@Autowired
	private Destination topicPageDestination;
	
	@Autowired
	private Destination queueSolrDeleteDestination;
	
	@Autowired
	private Destination topicDeletePageDestination;

	@Reference
	private GoodsService goodsService;
/*	@Reference(timeout = 10000)
	private ItemSearchService itemSearchService;*/

/*	@Reference(timeout = 10000)
	private ItemPageService itemPageService;*/
	
	

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll() {
		return goodsService.findAll();
	}

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return goodsService.findPage(page, rows);
	}

	/*	*//**
			 * 增加
			 * 
			 * @param goods
			 * @return
			 *//*
				 * @RequestMapping("/add") public Result add(@RequestBody TbGoods goods){ try {
				 * goodsService.add(goods); return new Result(true, "增加成功"); } catch (Exception
				 * e) { e.printStackTrace(); return new Result(false, "增加失败"); } }
				 */

	/**
	 * 修改
	 * 
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods) {
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id) {
		return goodsService.findOne(id);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			goodsService.delete(ids);
			// 删除索引库数据
//			itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			//删除静态详情页
			jmsTemplate.send(topicDeletePageDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			
			
			
			
			
			
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 查询+分页
	 * 
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
		return goodsService.findPage(goods, page, rows);
	}

	@RequestMapping("/updateStatus")
	public Result updateStatus(long[] ids, String status) {
		try {
			goodsService.updateStatus(ids, status);
			if ("1".equals(status)) {
				List<TbItem> itemList = goodsService.findTbItemListByGoodsIdAndAuditStatus(ids, status);
				// 判读sku有没有信息
				if (itemList.size() > 0) {
					// 删除未上架商品
					for (TbItem tbItem : itemList) {
						if (tbItem.getStatus() == "0") {
							itemList.remove(tbItem);
						}
					}
					// 将数据插入索引库
//					itemSearchService.importList(itemList);
					//activeMq中间件
					String jsonString = JSON.toJSONString(itemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
						return	session.createTextMessage(jsonString);
						}
					});
					System.out.println("准备进入service");
					// 生成商品详情页
					for (TbItem tbItem : itemList) {
//						itemPageService.getItemHtml(tbItem.getId());
						jmsTemplate.send(topicPageDestination, new MessageCreator() {
							
							@Override
							public Message createMessage(Session session) throws JMSException {
								return session.createTextMessage(tbItem.getGoodsId()+"");
							}
						});
					}

				} else {
					System.out.println("没有明细数据");
				}
			}

			return new Result(true, "成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new Result(false, "失败");
		}
	}

	@RequestMapping("/getHtml")
	public void getHtml() {
//		itemPageService.getItemHtml(149187842867962L);
	}

}
