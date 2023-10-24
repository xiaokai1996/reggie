package com.anyi.reggie.service.impl;

import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.entity.ShoppingCart;
import com.anyi.reggie.mapper.ShoppingCartMapper;
import com.anyi.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public void add(ShoppingCart shoppingCart) {
        log.info("新增一个菜品到购物车");
        // 获取当前用户id
        Long userId = UserContext.getUserId();
        shoppingCart.setUserId(userId);

        // 查询数据库里面已有的购物车单品
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        wrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart shoppingCartInDb = this.getOne(wrapper);


        // 如果购物车里已经有相同的单品,则数量＋1
        if (shoppingCartInDb != null) {
            shoppingCart.setId(shoppingCartInDb.getId());
            shoppingCart.setNumber(shoppingCartInDb.getNumber() + 1);
            this.updateById(shoppingCart);
        } else {
            // 没有就添加
            save(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> getList() {
        Long userId = UserContext.getUserId();
        log.info("查询用户id为{}下的购物车", userId);
        List<ShoppingCart> shoppingCarts = this.list(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, userId).orderByDesc(ShoppingCart::getCreateTime));
        return shoppingCarts;
    }

    @Override
    public void sub(ShoppingCart shoppingCart) {
        Long userId = UserContext.getUserId();
        log.info("id为{}的购物车,单品减一个", userId);
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        wrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart shoppingCartInDb = getOne(wrapper);

        if (shoppingCartInDb.getNumber() == 1) {
            // 如果只有一个就把他删掉
            removeById(shoppingCartInDb.getId());
        } else {
            // 否则就减1
            shoppingCartInDb.setNumber(shoppingCartInDb.getNumber() - 1);
            updateById(shoppingCartInDb);
        }
    }
}
