package com.hzy.exception;
/**
 * 地址簿业务异常
  */

public class AddressBookBusinessException extends BaseException{
    public AddressBookBusinessException(){};
    public AddressBookBusinessException(String msg){
        super(msg);
    };
}
