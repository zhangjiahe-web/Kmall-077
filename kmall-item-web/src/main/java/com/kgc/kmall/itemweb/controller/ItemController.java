package com.kgc.kmall.itemweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemController {

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId){
        return "item";
    }
}
