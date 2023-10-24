package com.anyi.reggie.service;

import com.anyi.reggie.dto.OrdersDto;
import com.anyi.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrdersService extends IService<Orders> {

    void addOrders(Orders orders);

    Page<OrdersDto> userPage(Integer page, Integer pageSize);
}
