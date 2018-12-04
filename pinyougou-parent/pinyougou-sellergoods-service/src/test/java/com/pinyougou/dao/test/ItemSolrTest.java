package com.pinyougou.dao.test;

import java.util.List;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.pojo.TbItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class ItemSolrTest {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Test
	public void test01() {
		String status = "1";
		long[] ids = { 149187842868022L, 149187842867960L, 149187842868023L };
		List<TbItem> itemList = goodsMapper.findTbItemListByGoodsIdAndAuditStatus(ids, status);
		System.out.println(itemList);
		for(TbItem item : itemList) {
			System.out.println(item);
		}
	}

}
