package com.anyi.reggie.service.impl;

import com.anyi.reggie.common.UserContext;
import com.anyi.reggie.entity.AddressBook;
import com.anyi.reggie.mapper.AddressBookMapper;
import com.anyi.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Override
    public void setDefault(AddressBook defaultAddressBook) {
        // 1. query all the addressBooks from the DB and set them as non-default
//        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(AddressBook::getUserId, UserContext.getUserId());
//        List<AddressBook> addressBooks = this.list(wrapper);
//        addressBooks.forEach(addressBook -> addressBook.setIsDefault(false));
//        this.updateBatchById(addressBooks);

        // 2. use wrapper condition to query and update the default address
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId, UserContext.getUserId());
        this.update(new AddressBook().setIsDefault(false), addressBookLambdaQueryWrapper);

        defaultAddressBook.setIsDefault(true);
        this.updateById(defaultAddressBook);
    }
}
