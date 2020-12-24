package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsBaseSaleAttr;
import com.kgc.kmall.bean.PmsProductImage;
import com.kgc.kmall.bean.PmsProductInfo;
import com.kgc.kmall.bean.PmsProductSaleAttr;
import com.kgc.kmall.service.SpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@CrossOrigin
@RestController
@Api(tags = "商品spu管理-显示spu列表",description = "提供商品信息管理")
public class SpuController {
    @Reference
    SpuService spuService;
@Value("${fileServer.url}")
String fileServer;
    @ApiOperation("根据三级分类显示spu商品销售属性")
    @GetMapping("/spuList")
    @ApiImplicitParam(name = "catalog3Id",value = "3级分类id",required = true)
  /*  @RequestMapping("/spuList")*/
    public List<PmsProductInfo> spuList(Long catalog3Id){
        List<PmsProductInfo> infoList = spuService.spuList(catalog3Id);
        return infoList;
    }

    @ApiOperation("显示销售属性列表")
    @PostMapping("/baseSaleAttrList")
   /* @RequestMapping("/baseSaleAttrList")*/
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> saleAttrList = spuService.baseSaleAttrList();
        return saleAttrList;
    }
    @ApiOperation("图片上传")
    @PostMapping("/fileUpload")
    @ApiImplicitParam(name = "file",value = "图片上传",required = true)
   /* @RequestMapping("/fileUpload")*/
    public String fileUpload(@RequestParam("file")MultipartFile file){
        //文件上传
        //返回文件上传后的路径
        try {
            String conFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(conFile);
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer=trackerClient.getTrackerServer();
            StorageClient storageClient=new StorageClient(trackerServer,null);
            String originalFilename = file.getOriginalFilename();
            String extName = FilenameUtils.getExtension(originalFilename);
            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            String path=fileServer;
            for (int i = 0; i < upload_file.length; i++) {
                String s = upload_file[i];
                System.out.println("s = " + s);
                path+="/"+s;
            }
            System.out.println(path);
            return path;

        }catch (Exception ex){
return null;
        }

    }
    @ApiOperation("保存spu")
    @PostMapping("/saveSpuInfo")
    @ApiImplicitParam(name = "PmsProductInfo",value = "pmsProductInfo实体类对象",required = true)
   /* @RequestMapping("/saveSpuInfo")*/
    public String saveSpuInfo(@RequestBody  PmsProductInfo pmsProductInfo){
//保存数据库
        Integer integer = spuService.saveSpuInfo(pmsProductInfo);
        return integer>0?"success":"fail";
    }
    @ApiOperation("显示销售属性和属性值")
    @GetMapping("/spuSaleAttrList")
    @ApiImplicitParam(name = "spuId",value = "销售属性ID",required = true)
  /*  @RequestMapping("/spuSaleAttrList")*/
    public List<PmsProductSaleAttr> spuSaleAttrList(Long spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList=spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }
    @ApiOperation("显示图片列表")
    @GetMapping("/spuImageList")
    @ApiImplicitParam(name = "spuId",value = "销售属性ID",required = true)
    /*@RequestMapping("/spuImageList")*/
    public List<PmsProductImage> spuImageList(Long spuId){
        List<PmsProductImage> pmsProductImageList = spuService.spuImageList(spuId);
        return pmsProductImageList;
    }
}
