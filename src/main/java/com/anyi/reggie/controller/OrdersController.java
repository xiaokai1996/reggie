package com.anyi.reggie.controller;

import com.anyi.reggie.common.R;
import com.anyi.reggie.dto.OrdersDto;
import com.anyi.reggie.entity.Orders;
import com.anyi.reggie.entity.User;
import com.anyi.reggie.service.OrdersService;
import com.anyi.reggie.service.UserService;
import com.anyi.reggie.vo.OrdersVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    // @TODO这里的resource和autowired有什么区别
    @Resource
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    @PostMapping("/submit")
    public R<String> addOrders(@RequestBody Orders orders){
        ordersService.addOrders(orders);
        return R.success("下单成功！");
    }

    // 这个是front请求的接口,实际上每次只请求一页,这里复杂的逻辑其实写在service里面了,感觉这样更加合理一些
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> getOrdersFronted(Integer page, Integer pageSize){
        Page<OrdersDto> ordersDtoPage = ordersService.userPage(page,pageSize);
        return R.success(ordersDtoPage);
    }

    @GetMapping("/page")
    public R<Page<OrdersVo>> getOrdersBackend(Integer page, Integer pageSize,
                                              Long number,
                                              String beginTime,
                                              String endTime) {
        log.info("分页查询所有订单, number={}, {}<日期<{}", number, beginTime, endTime);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        // 订单单号和时间都是可选变量
        queryWrapper.eq(number != null, Orders::getId, number);
        queryWrapper.between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);
        queryWrapper.orderByDesc(Orders::getOrderTime);


        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersVo> ordersVoPage = new Page<>();

        ordersService.page(ordersPage, queryWrapper);
        BeanUtils.copyProperties(ordersPage, ordersVoPage, "records");

        List<OrdersVo> ordersVos = ordersPage.getRecords().stream().map((orders) -> {
            OrdersVo ordersVo = new OrdersVo();
            BeanUtils.copyProperties(orders, ordersVo);
            User user = userService.getById(orders.getUserId());
            if (user != null) {
                ordersVo.setUserName(user.getPhone());
            }
            return ordersVo;
        }).collect(Collectors.toList());

        ordersVoPage.setRecords(ordersVos);
        return R.success(ordersVoPage);
    }


    @PutMapping
    // 注意这里的变量一定要和前端保持一致,不能自己随意命名,PutMapping是需要一个body的
    public R<String> updateOrdersStatusById(@RequestBody Orders orders) {
        log.info("修改订单状态:{}", orders.toString());
        Long ordersId = orders.getId();
        Integer status = orders.getStatus();
        Orders ordersDb = ordersService.getById(ordersId);
        ordersDb.setStatus(status);
        ordersService.updateById(ordersDb);
        return R.success("修改状态成功");
    }
}

