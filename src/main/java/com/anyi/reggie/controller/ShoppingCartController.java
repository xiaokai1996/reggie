package com.anyi.reggie.controller;

import com.anyi.reggie.common.R;
import com.anyi.reggie.entity.ShoppingCart;
import com.anyi.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> getList(){
        List<ShoppingCart> list = shoppingCartService.getList();
        return R.success(list);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车物品+1");
        shoppingCartService.add(shoppingCart);
        return R.success("添加成功！");
    }
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车物品-1");
        shoppingCartService.sub(shoppingCart);
        return R.success("取消成功");
    }
}

