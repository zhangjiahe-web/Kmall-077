package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsBaseCatalog1;
import com.kgc.kmall.bean.PmsBaseCatalog2;
import com.kgc.kmall.bean.PmsBaseCatalog3;
import com.kgc.kmall.service.CatalogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@Api(tags = "小课商城后台管理系统查询1，2，3级分类",description = "提供基本信息管理，商品信息管理")
public class CatalogController {
    @Reference
    CatalogService catalogService;
    @ApiOperation("查询1级分类")
    @PostMapping("/getCatalog1")
/*    @ApiImplicitParam(name = "id",value = "用户id",required = true)*/
   /* @RequestMapping("/getCatalog1")*/
    public List<PmsBaseCatalog1> getCatalog1(){
        List<PmsBaseCatalog1> catalog1List = catalogService.getCatalog1();
        return catalog1List;
    }
    @ApiOperation("查询2级分类")
    @PostMapping("/getCatalog2")
       @ApiImplicitParam(name = "catalog1Id",value = "1级分类id",required = true)
/*    @RequestMapping("/getCatalog2")*/
    public List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id){
        List<PmsBaseCatalog2> catalog2List = catalogService.getCatalog2(catalog1Id);
        return catalog2List;
    }
    @ApiOperation("查询3级分类")
    @PostMapping("/getCatalog3")
    @ApiImplicitParam(name = "catalog2Id",value = "2级分类id",required = true)
   /* @RequestMapping("/getCatalog3")*/
    public List<PmsBaseCatalog3> getCatalog3(Long catalog2Id){
        List<PmsBaseCatalog3> catalog3List = catalogService.getCatalog3(catalog2Id);
        return catalog3List;
    }
}
