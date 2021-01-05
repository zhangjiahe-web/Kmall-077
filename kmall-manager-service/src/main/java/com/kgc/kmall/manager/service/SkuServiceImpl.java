package com.kgc.kmall.manager.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.*;
import com.kgc.kmall.manager.mapper.PmsSkuAttrValueMapper;
import com.kgc.kmall.manager.mapper.PmsSkuImageMapper;
import com.kgc.kmall.manager.mapper.PmsSkuInfoMapper;
import com.kgc.kmall.manager.mapper.PmsSkuSaleAttrValueMapper;

import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.util.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

@Component
@Service
public class SkuServiceImpl implements SkuService {
    @Resource
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Resource
    PmsSkuImageMapper pmsSkuImageMapper;
    @Resource
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Resource
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Resource
    RedisUtil redisUtil;
    @Resource
    RedissonClient redissonClient;
    @Override
    public String saveSkuInfo(PmsSkuInfo skuInfo) {
        pmsSkuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();
        for (PmsSkuImage pmsSkuImage : skuInfo.getSkuImageList()) {
            pmsSkuImage.setSkuId(skuInfoId);
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        for (PmsSkuAttrValue pmsSkuAttrValue : skuInfo.getSkuAttrValueList()) {
            pmsSkuAttrValue.setSkuId(skuInfoId);
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
            pmsSkuSaleAttrValue.setSkuId(skuInfoId);
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
        return "success";
    }
   /* @Override
    public PmsSkuInfo selectBySkuId(Long id) {
        PmsSkuInfo pmsSkuInfo=null;
        Jedis jedis = redisUtil.getJedis();
        String skuKey= "sku:"+id+":info";
        String skuInfoJson = jedis.get(skuKey);

        if(skuInfoJson!=null){
             pmsSkuInfo = JSON.parseObject(skuInfoJson, PmsSkuInfo.class);
            jedis.close();
            return pmsSkuInfo;
        }else{
            //获取分布式锁
            //使用nx分布式锁，避免缓存击穿
            String skuLockKey="sku:"+id+":lock";
            String skuLockValue= UUID.randomUUID().toString();
            String lock = jedis.set(skuLockKey, skuLockValue, "NX", "PX",60*1000);
           if (lock.equals("OK")){
             pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(id);
            //保存到redis
            if (pmsSkuInfo!=null){
            String skuInfoJsonStr = JSON.toJSONString(pmsSkuInfo);
                //有效期随机，防止缓存雪崩
                Random random=new Random();
                int i = random.nextInt(10);
                jedis.setex(skuKey,i*60*1000,skuInfoJsonStr);
            }else {
                jedis.setex(skuKey,5*60*1000, "empty");
            }
            //拿到递归后 删除分布式锁
               //删除分布式锁
               *//*String skuLockValue2 = jedis.get(skuLockKey);
               if (skuLockValue2!=null&&skuLockValue2.equals(skuLockKey)){
                   jedis.del(skuLockKey);
               }*//*
               String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
               jedis.eval(script, Collections.singletonList(skuLockKey),Collections.singletonList(skuLockValue));
           }else {
               //没有拿到锁 线程睡眠 递归调用
               try {
                   Thread.sleep(3000);
               }catch (Exception ex){

               }
                 selectBySkuId(id);
           }
           jedis.close();
        }
return pmsSkuInfo;
    }*/
        @Override
    public PmsSkuInfo selectBySkuId(Long id) {
            PmsSkuInfo pmsSkuInfo = null;
            Jedis jedis = redisUtil.getJedis();
            String skuKey = "sku:" + id + ":info";
            String skuInfoJson = jedis.get(skuKey);

            if (skuInfoJson != null) {
                pmsSkuInfo = JSON.parseObject(skuInfoJson, PmsSkuInfo.class);
                jedis.close();
                System.out.println("缓存");
                return pmsSkuInfo;

            } else {
                //获取分布式锁
                //使用nx分布式锁，避免缓存击穿
                RLock lock = redissonClient.getLock("lock");
                lock.lock();//上锁
                try {
                    System.out.println("数据库");
                    pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(id);
                    //保存到redis
                    if (pmsSkuInfo != null) {
                        String skuInfoJsonStr = JSON.toJSONString(pmsSkuInfo);
                        //有效期随机，防止缓存雪崩
                        Random random = new Random();
                        int i = random.nextInt(10);
                        jedis.setex(skuKey, i * 60 * 1000, skuInfoJsonStr);
                    } else {
                        jedis.setex(skuKey, 5 * 60 * 1000, "empty");
                    }
                    jedis.close();

                } finally {
                    lock.unlock();
                }

            }
            return pmsSkuInfo;
        }
    @Override
    public List<PmsSkuInfo> selectBySpuId(Long spuId) {

        return pmsSkuInfoMapper.selectBySpuId(spuId);
    }
    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectByExample(null);
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSkuAttrValueExample example=new PmsSkuAttrValueExample();
            PmsSkuAttrValueExample.Criteria criteria = example.createCriteria();
            criteria.andSkuIdEqualTo(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.selectByExample(example);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfos;
    }
}
