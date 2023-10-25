package com.anyi.reggie.controller;


import com.anyi.reggie.common.R;
import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.entity.AddressBook;
import com.anyi.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 地址管理 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-05-25
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    @PostMapping
    public R<String> add(@RequestBody AddressBook addressBook) {
        // 这个userId是放在跟后端的上下文LocalThread里面的,前端不会直接传递这个值
        Long userId = UserContext.getUserId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("添加地址成功！");
    }

    @GetMapping("/list")
    public R<List<AddressBook>> getList() {
        log.info("返回当前用户所有的地址");
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId, userId);
        addressBookLambdaQueryWrapper.orderByDesc(AddressBook::getIsDefault);
        List<AddressBook> addressBooks = addressBookService.list(addressBookLambdaQueryWrapper);
        return R.success(addressBooks);
    }

    @PutMapping("/default")
    public R<String> changeDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return R.success("默认地址设置成功!");
    }

    @GetMapping("/{id}")
    public R getAddress(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 获取默认地址
     *
     * @return
     */
    @GetMapping("/default")
    public R getDefault() {
        Long userId = UserContext.getUserId();
        AddressBook one = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>()
                        .eq(AddressBook::getIsDefault, true)
                        .eq(AddressBook::getUserId, userId));
        return R.success(one);
    }
}

