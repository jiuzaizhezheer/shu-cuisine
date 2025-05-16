package com.hzy.service;

import com.hzy.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void addAddress(AddressBook addressBook);

    List<AddressBook> list();

    void updateAddress(AddressBook addressBook);

    AddressBook getById(Integer id);

    void setDefault(AddressBook addressBook);

    void deleteById(Integer id);

    AddressBook defaultList();
}
