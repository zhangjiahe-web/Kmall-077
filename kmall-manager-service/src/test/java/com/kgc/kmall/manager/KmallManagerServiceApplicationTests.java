package com.kgc.kmall.manager;

import com.kgc.kmall.bean.PmsBaseCatalog1;

import com.kgc.kmall.service.CatalogService;
import com.kgc.kmall.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class KmallManagerServiceApplicationTests {
@Resource
CatalogService catalogService;
	@Resource
	RedisUtil redisUtil;
	@Test
	void contextLoads() {
		/*List<PmsBaseCatalog1> catalog1 = catalogService.getCatalog1();
		System.out.println(catalog1);*/
		try {
			Jedis jedis = redisUtil.getJedis();
			String ping = jedis.ping();
			System.out.println(ping);
			String name = jedis.get("name");
			System.out.println(name);
		}catch (JedisConnectionException e){
			e.printStackTrace();
		}
	}

}
