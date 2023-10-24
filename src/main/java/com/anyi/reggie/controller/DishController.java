package com.anyi.reggie.controller;

import com.anyi.reggie.common.R;
import com.anyi.reggie.dto.DishDto;
import com.anyi.reggie.entity.Dish;
import com.anyi.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

// 学习的时候可以围绕着这个DishController来进行,他是一个比较经典的MVC架构,并且在entity周围有很多的业务包装(dto/vo)
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    // resource和autowired的区别是什么? https://www.51cto.com/article/719703.html
    // 1. resource是java提供的,按照name装配
    // 2. autowired是spring提供的,按照type装配,和spring强绑定

    // dish外围有category分类,flavor口味,以及copies份数,但这些都附属于dish,所以其他几个只有service处理业务逻辑,而并没有controller接口
    @Resource
    private DishService dishService;

    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        log.info("新增菜品");
        dishService.addDish(dishDto);
        return R.success("新增菜品成功！");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        log.info("分页查询所有菜品");
        // 原本也是可以在controller里面把page直接查出来的,但是这样就涉及到了太多的业务代码,因此用一个pageSearch把业务代码封装到service层
        Page<DishDto> pageInfo = dishService.pageSearch(page, pageSize, name);
        return R.success(pageInfo);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        log.info("查询单个菜品");
        DishDto dishDto = dishService.getDishById(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateDish(dishDto);
        return R.success("修改菜品成功！");
    }

    @DeleteMapping
    public R<String> delete(List<Long> ids) {
        dishService.deleteDish(ids);
        return R.success("删除成功");
    }


    @PostMapping("/status/{status}")
    public R<String> update(@PathVariable Integer status, List<Long> ids) {
        dishService.updateStatus(status, ids);
        // 先全部查出来
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(!ids.isEmpty(), Dish::getId, ids);
        List<Dish> dishes = dishService.list(dishLambdaQueryWrapper);
        // 一次性修改,然后全部更新
        dishes.forEach(dish -> dish.setStatus(status));
        dishService.updateBatchById(dishes);
        return R.success("更新状态成功");
    }

    // 这个接口是给手机端点餐的时候用的,是不是这两个接口可以分开?
    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId, Integer status) {
        log.info("根据类别返回该类菜单");
        List<DishDto> dishDtoList = dishService.getList(categoryId, status);
        return R.success(dishDtoList);
    }
}

