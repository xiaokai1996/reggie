package com.anyi.reggie.dto;


import com.anyi.reggie.entity.OrderDetail;
import com.anyi.reggie.entity.Orders;
import lombok.Data;
import java.util.List;


@Data
public class OrdersDto extends Orders {
    // 注意这里是extends,所以不仅包含了Orders本身,还另外包含了orderDetails
    private List<OrderDetail> orderDetails;
	
}
