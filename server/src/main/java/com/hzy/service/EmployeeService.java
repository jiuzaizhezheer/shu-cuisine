package com.hzy.service;

import com.hzy.dto.EmployeeDTO;
import com.hzy.dto.EmployeeFixPwdDTO;
import com.hzy.dto.EmployeeLoginDTO;
import com.hzy.dto.PageDTO;
import com.hzy.entity.Employee;
import com.hzy.result.PageResult;
import com.hzy.vo.EmployeeLoginVO;

public interface EmployeeService {
    Employee getEmployeeById(Integer id);

    EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO);

    void register(EmployeeLoginDTO employeeLoginDTO);

    PageResult<Employee> employeePageList(PageDTO pageDTO);

    void update(EmployeeDTO employeeDTO);

    void delete(Integer id);

    void onOff(Integer id);

    void addEmployee(EmployeeDTO employeeDTO);

    void fixPwd(EmployeeFixPwdDTO employeeFixPwdDTO);
}
