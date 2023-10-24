package com.anyi.reggie.service.impl;

import com.anyi.reggie.common.CommentRedis;
import com.anyi.reggie.dto.DishDto;
import com.anyi.reggie.entity.Dish;
import com.anyi.reggie.entity.DishFlavor;
import com.anyi.reggie.mapper.DishMapper;
import com.anyi.reggie.service.CategoryService;
import com.anyi.reggie.service.DishFlavorService;
import com.anyi.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishFlavorService dishFlavorService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void addDish(DishDto dishDto) {
        log.info("dishDto: {}", dishDto);
        // 1. 首先封装菜品信息并保存
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        this.save(dish);    // 当dish被保存到数据库里面的时候
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 2. 封装flavors信息并且批量保存
        flavors.forEach((flavor) -> flavor.setDishId(dish.getId())); // 这里用forEach比重新赋值要更高效一点,因为传递进去的是指针/引用
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    // DishDto在Dish基础上
    public Page<DishDto> pageSearch(Integer page, Integer pageSize, String name) {
        // 1. 把基础的dish page查出来
        Page<Dish> dishPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getCreateTime);
        this.page(dishPage, dishLambdaQueryWrapper);

        // 2. 把dish page转换成dishDto的page
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        dishDtoPage.setRecords(dishPage.getRecords().stream().map((dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            dishDto.setCategoryName(categoryService.getById(dish.getCategoryId()).getName());
            return dishDto;
        })).collect(Collectors.toList()));
        return dishDtoPage;
    }

    @Override
    public DishDto getDishById(Long id) {
        // 1. 查询dish基础信息和口味信息
        Dish dish = this.getDishById(id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));

        // 2. 封装到dishDto里面
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    @Override
    public void updateDish(DishDto dishDto) {
        // 1.  封装菜品信息并保存
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        this.updateById(dish);

        // 2. 封装flavors信息并且批量保存
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        dishFlavors.forEach((dishFlavor) -> dishFlavor.setDishId(dish.getId()));
        dishFlavorService.updateBatchById(dishFlavors);
    }

    @Override
    public void deleteDish(List<Long> ids) {
        // 1. 删除dish本身
        this.removeByIds(ids);
        // 2. 删除和dish相关的flavor
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().in(DishFlavor::getDishId, ids));
    }

    @Override
    public List<DishDto> getList(Long categoryId, Integer status) {

        // 先尝试从redis里面取数据,如果取到了,直接返回
        String key = CommentRedis.DISH_PREFIX + categoryId + "_" + status;
        List<DishDto> dtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dtoList !=null){
            return dtoList;
        }

        // 否则从数据库里面查
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId, categoryId).eq(Dish::getStatus,status);
        List<Dish> list = list(wrapper);
        // 如果是需要生成新的对象,尽量选择stream的方式
        List<DishDto> dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            String categoryName = categoryService.getById(item.getCategoryId()).getName();
            dishDto.setCategoryName(categoryName);
            List<DishFlavor> favors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, item.getId()));
            dishDto.setFlavors(favors);
            return dishDto;
        }).collect(Collectors.toList());

        // 在取完数据之后,会保存到redis里面
        redisTemplate.opsForValue().set(key, dishDtoList,30, TimeUnit.MINUTES);
        return dishDtoList;
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        log.info("批量修改菜品的状态");
        List<Dish> dishes = this.list(new LambdaQueryWrapper<Dish>().in(Dish::getId, ids));
        dishes.forEach(dish -> dish.setStatus(status));
        this.updateBatchById(dishes);
    }
}
