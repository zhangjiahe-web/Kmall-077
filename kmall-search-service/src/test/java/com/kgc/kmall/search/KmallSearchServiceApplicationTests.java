package com.kgc.kmall.search;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class KmallSearchServiceApplicationTests {


	@Reference
	SkuService skuService;

	@Resource
	JestClient jestClient;
@Resource
	ElasticsearchRestTemplate elasticsearchRestTemplate;

	@Test
	void contextLoads() {
		List<PmsSkuInfo> allSku = skuService.getAllSku();
		List<PmsSearchSkuInfo> pmsSearchSkuInfos=new ArrayList<>();
		for (PmsSkuInfo pmsSkuInfo : allSku) {
			PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
			BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
			pmsSearchSkuInfo.setProductId(pmsSkuInfo.getSpuId());
			pmsSearchSkuInfos.add(pmsSearchSkuInfo);
		}
		for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
			Index index=new Index.Builder(pmsSearchSkuInfo).index("kmall").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
			try {
				jestClient.execute(index);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	@Test
	void contextLoads2() {
		String json="{\n" +
				"\"query\": {\n" +
				"  \"match\": {\n" +
				"    \"skuName\": \"iphone\"\n" +
				"  }\n" +
				"}  \n" +
				"}";
		Search search = new Search.Builder(json).addIndex("kmall").addType("PmsSkuInfo").build();
		try {
			SearchResult searchResult = jestClient.execute(search);
			List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
			for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
				PmsSearchSkuInfo skuInfo=hit.source;
				System.out.println(skuInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	@Test
	void contextLoads3() {
		SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();

		TermQueryBuilder termQueryBuilder=new TermQueryBuilder("skuAttrValueList.valueId",39);
		TermQueryBuilder termQueryBuilder1=new TermQueryBuilder("skuAttrValueList.valueId",43);

		boolQueryBuilder.filter(termQueryBuilder);
		boolQueryBuilder.filter(termQueryBuilder1);
		//排序
		searchSourceBuilder.sort("id", SortOrder.DESC);
		Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("kmall").addType("PmsSkuInfo").build();
		try {
			SearchResult searchResult = jestClient.execute(search);
			List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
			for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
				PmsSearchSkuInfo skuInfo=hit.source;
				System.out.println(skuInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	void contextLoads4() {
BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
boolQueryBuilder.must(new MatchQueryBuilder("skuName","iphone"));
	 SearchQuery searchQuery = new NativeSearchQueryBuilder()
			 .withQuery(boolQueryBuilder)
			 .build();
		List<PmsSearchSkuInfo> pmsSearchSkuInfos = elasticsearchRestTemplate.queryForList(searchQuery, PmsSearchSkuInfo.class);
		for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
			System.out.println(pmsSearchSkuInfo.toString());
		}

	}

}
