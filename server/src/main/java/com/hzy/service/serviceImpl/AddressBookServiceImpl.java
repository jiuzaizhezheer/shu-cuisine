package com.hzy.service.serviceImpl;

import com.hzy.context.BaseContext;
import com.hzy.entity.AddressBook;
import com.hzy.mapper.AddressBookMapper;
import com.hzy.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     * @param addressBook
     */
    public void addAddress(AddressBook addressBook) {
        // 要先知道是哪个用户要新增地址，并且刚开始无法设置默认地址，需要在其他前端页面设置
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询用户地址
     * @param
     * @return
     */
    public List<AddressBook> list() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        return addressBookMapper.getUserAddress(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook
     */
    public void updateAddress(AddressBook addressBook) {
        addressBookMapper.udpate(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    public AddressBook getById(Integer id) {
        return addressBookMapper.getById(id);
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    public void setDefault(AddressBook addressBook) {
        // 1、先把当前用户所有地址都设置成非默认地址
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);
        // 2、再把当前地址设置成默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.udpate(addressBook);
    }

    /**
     * 根据id删除地址
     * @param id
     */
    public void deleteById(Integer id) {
        addressBookMapper.delete(id);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook defaultList() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(1);
        List<AddressBook> userAddress = addressBookMapper.getUserAddress(addressBook);
        if (userAddress == null || userAddress.isEmpty()) {
            return null; // 或者 new AddressBook()
        }

        return userAddress.get(0);
    }

}
