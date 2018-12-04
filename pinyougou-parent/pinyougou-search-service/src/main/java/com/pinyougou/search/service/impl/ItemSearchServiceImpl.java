package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleArgumentExpr;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.sun.jdi.IntegerType;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;


	@Override
	public Map<String, Object> search(Map searchMap) {
		/*
		 * Map map = new HashMap<>(); //建立查询条件 System.out.println(searchMap); Query
		 * query = new SimpleQuery("*:*"); Criteria criteria = new
		 * Criteria("item_keywords").is(searchMap.get("keywords"));
		 * query.addCriteria(criteria ); //分页查询 ScoredPage<TbItem> pages =
		 * solrTemplate.queryForPage(query, TbItem.class); List<TbItem> tbItems =
		 * pages.getContent(); System.out.println(tbItems); map.put("rows", tbItems);
		 */

		Map<String, Object> map = new HashMap<>();
		String keywords = (String) searchMap.get("keywords");
		System.out.println(keywords);
		System.out.println(keywords.replace(" ", ""));
		searchMap.put("keywords", keywords.replace(" ", ""));
		// 1.查询高亮
		map.putAll(searchList(searchMap));
		// 2.查询分类
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList", categoryList);
		// 3.查询品牌和规格
		if (categoryList.size() > 0) {
			map.putAll(searchBrandAndSpecList(categoryList.get(0)));

		}
		// System.out.println(map);
		return map;
	}

	private Map searchList(Map searchMap) {
		// 分组页->分组入口->入口集合
		Map map = new HashMap<>();
		String sortType = (String) searchMap.get("sortType");
		String sortField = (String) searchMap.get("sortField");
		HighlightQuery query = new SimpleHighlightQuery();
		// 高亮选项
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions);
		// 查询条件
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		// 1.1 按照分类过滤
		if (!"".equals(searchMap.get("category"))) {
			Criteria filterCategory = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCategory);
			query.addFilterQuery(filterQuery);
		}
		// 1.2按照品牌过滤
		if (!"".equals(searchMap.get("brand"))) {
			Criteria filterCategory = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCategory);
			query.addFilterQuery(filterQuery);
		}
		// 1.3按规格过滤
		Map<String, String> mapSpec = (Map<String, String>) searchMap.get("spec");
		for (String key : mapSpec.keySet()) {
			Criteria filterCategory = new Criteria("item_spec_" + key).is(mapSpec.get(key));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCategory);
			query.addFilterQuery(filterQuery);
		}

		// 1.4价格筛选
		if (!"".equals(searchMap.get("price"))) {
			String priceStr = (String) searchMap.get("price");
			String[] price = priceStr.split("-");
			if (!"0".equals(price[0])) {
				// greaterThanEqual自动把字符串参数转换
				Criteria filterCategory = new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCategory);
				query.addFilterQuery(filterQuery);
			}

			if (!"*".equals(price[1])) {
				Criteria filterCategory = new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCategory);
				query.addFilterQuery(filterQuery);
			}
		}
		// 1.5分页查询

		Integer pageNo = (Integer) searchMap.get("pageNo");
		Integer pageSize = (Integer) searchMap.get("pageSize");
		if (pageNo == null) {
			pageNo = 1;
		}
		if (pageSize == null) {
			pageSize = 40;
		}
		query.setOffset((pageNo - 1) * pageSize);
		query.setRows(pageSize);
		// 1.6价格排序
		if (sortField != null && !"".equals(sortField)) {
			if ("ASC".equals(sortType)) {
				Sort sort = new Sort(Direction.ASC, "item_" + sortField);
				query.addSort(sort);
			}
			if ("DESC".equals(sortType)) {
				Sort sort = new Sort(Direction.DESC, "item_" + sortField);
				query.addSort(sort);
			}
		}

		// **********************
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		// 高亮入口集合,每一条记录的集合
		List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : entryList) {
			//
			TbItem tbItem = highlightEntry.getEntity();
			// 获得高亮列表(高亮域的个数)
			List<Highlight> highlights = highlightEntry.getHighlights();
			if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
				tbItem.setTitle(highlights.get(0).getSnipplets().get(0));
			}

		}

		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());
		map.put("total", page.getTotalElements());
		return map;

	}

	private List searchCategoryList(Map searchMap) {
		// 分组页->分组入口->入口集合
		List<String> list = new ArrayList<>();

		Query query = new SimpleQuery();
		// 表+条件 判断条件 where
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);

		// 分组条件设置
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		// 查询的每条记录 得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		//// 根据列得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		// 得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		// 得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for (GroupEntry<TbItem> groupEntry : content) {
			list.add(groupEntry.getGroupValue());// 将分组结果的名称封装到返回值中
		}
		// System.out.println(list);
		return list;

	}

	private Map searchBrandAndSpecList(String categoryName) {
		Map map = new HashMap<>();
		Long id = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
		if (id != null) {
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(id);
			List specList = (List) redisTemplate.boundHashOps("specList").get(id);
			map.put("brandList", brandList);
			map.put("specList", specList);
		}

		return map;

	}

	@Override
	public void importList(List<TbItem> tbItemList) {
		System.out.println("进入searchService");
		if(tbItemList==null||tbItemList.size()<1) {
			return;
		}
			for (TbItem item : tbItemList) {
				System.out.println(item);
				Map specMap = JSON.parseObject(item.getSpec(),Map.class);// 将 spec 字段中的 json 字符串转换为 map
				item.setSpecMap(specMap);// 给带注解的字段赋值
			}
			solrTemplate.saveBeans(tbItemList);
			solrTemplate.commit();
		
	}
	
	/**
	 * 更具goodsId删除索引库信息
	 */

	@Override
	public void deleteByGoodsIds(List goodsIds) {
		
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid").in(goodsIds);
		query.addCriteria(criteria );
		solrTemplate.delete(query );
		solrTemplate.commit();
	}



}
