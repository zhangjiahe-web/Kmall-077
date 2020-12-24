package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;
import com.kgc.kmall.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@Api(tags = "小课商城后台管理系统、添加平台属性，修改平台属性-显示属性值，修改平台属性",description = "提供基本信息管理")
public class AttrController {
    @Reference
    AttrService attrService;
    @ApiOperation("根据三级分类id查询属性")
    @GetMapping("/attrInfoList")
    @ApiImplicitParam(name = "catalog3Id",value = "3级分类id",required = true)
/*    @RequestMapping("/attrInfoList")*/
    public List<PmsBaseAttrInfo> attrInfoList(Long catalog3Id){
        List<PmsBaseAttrInfo> infoList = attrService.select(catalog3Id);
        return infoList;
    }
    @ApiOperation("添加属性和修改属性和删除")
    @PostMapping("/saveAttrInfo")
    @ApiImplicitParam(name = "PmsBaseAttrInfo",value = "PmsBaseAttrInfo实体类对象",required = true)
   /* @RequestMapping("/saveAttrInfo")*/
    public Integer saveAttrInfo(@RequestBody PmsBaseAttrInfo attrInfo){
        Integer i = attrService.add(attrInfo);
        return i;
    }
    @ApiOperation("根据平台属性ID查询属性值")
    @PostMapping("/getAttrValueList")
    @ApiImplicitParam(name = "attrId",value = "平台属性ID",required = true)
  /*  @RequestMapping("/getAttrValueList")*/
    public List<PmsBaseAttrValue> getAttrValueList(Long attrId){
        List<PmsBaseAttrValue> valueList = attrService.getAttrValueList(attrId);
        return valueList;
    }
}
