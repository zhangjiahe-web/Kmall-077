package com.kgc.kmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.OmsCartItem;
import com.kgc.kmall.bean.OmsCartItemExample;
import com.kgc.kmall.cart.mapper.OmsCartItemMapper;
import com.kgc.kmall.service.CartService;
import com.kgc.kmall.util.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service
public class CartServiceImpl implements CartService {

    @Resource
    OmsCartItemMapper omsCartItemMapper;
    @Resource
    RedisUtil redisUtil;



    @Override
    public OmsCartItem ifCartExistByUser(String memberId, long skuId) {
        OmsCartItemExample example = new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(Long.parseLong(memberId));
        criteria.andProductSkuIdEqualTo(skuId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(example);
        if (omsCartItems != null && omsCartItems.size() > 0) {
            return omsCartItems.get(0);
        } else {
            return null;
        }

    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        omsCartItemMapper.insertSelective(omsCartItem);
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {
        omsCartItemMapper.updateByPrimaryKeySelective(omsCartItemFromDb);
    }

    @Override
    public void flushCartCache(String memberId) {
        OmsCartItemExample example=new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(Long.parseLong(memberId));
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(example);
        // 同步到redis缓存中
        Jedis jedis = redisUtil.getJedis();
        Map<String,String> map = new HashMap<>();
        for (OmsCartItem cartItem : omsCartItems) {
            map.put(cartItem.getProductSkuId().toString(), JSON.toJSONString(cartItem));
            System.out.println("cartItem.getProductSkuId().toString()：：："+cartItem.getProductSkuId().toString());
            System.out.println("JSON.toJSONString(cartItem)"+JSON.toJSONString(cartItem));
        }
        jedis.del("user:"+memberId+":cart");
        jedis.hmset("user:"+memberId+":cart",map);
        jedis.close();
    }
    @Override
    public List<OmsCartItem> cartList(String memberId) {
        Jedis jedis = null;
        List<OmsCartItem> omsCartItems = new ArrayList<>();
            try {
                jedis = redisUtil.getJedis();
                List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
                if(hvals!=null&&hvals.size()>0){
                for (String hval : hvals) {
                    OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                    omsCartItems.add(omsCartItem);
                }
            }else {
                    OmsCartItemExample example=new OmsCartItemExample();
                    OmsCartItemExample.Criteria criteria = example.createCriteria();
                    criteria.andMemberIdEqualTo(Long.parseLong(memberId));
                     omsCartItems = omsCartItemMapper.selectByExample(example);
                     flushCartCache(memberId);
                }
            }catch (Exception e){
                // 处理异常，记录系统日志
                e.printStackTrace();
                //String message = e.getMessage();
                //logService.addErrLog(message);
                return null;
            }finally {
                jedis.close();
            }

        return omsCartItems;
    }
    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        OmsCartItemExample example=new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(omsCartItem.getMemberId());
        criteria.andProductSkuIdEqualTo(omsCartItem.getProductSkuId());
        omsCartItemMapper.updateByExampleSelective(omsCartItem,example);

        flushCartCache(omsCartItem.getMemberId().toString());
    }
  /*  @Override
    public void flushCartCache2(String memberId) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        // 同步到redis缓存中

            Jedis jedis = redisUtil.getJedis();
            Map<String, String> map = new HashMap<>();
            List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                omsCartItems.add(omsCartItem);
                map.put(omsCartItem.getProductSkuId().toString(), JSON.toJSONString(omsCartItem));
            }
            jedis.del("gwc:" + memberId + ":gwc");
            jedis.hmset("gwc:" + memberId + ":gwc", map);
            jedis.close();

    }*/


}