package com.hzy.controller.admin;

import com.hzy.dto.EmployeeDTO;
import com.hzy.dto.EmployeeFixPwdDTO;
import com.hzy.dto.EmployeeLoginDTO;
import com.hzy.dto.PageDTO;
import com.hzy.entity.Employee;
import com.hzy.result.PageResult;
import com.hzy.result.Result;
import com.hzy.service.EmployeeService;
import com.hzy.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;



    /**
     * 员工注册（其实就是新增操作而已，和token什么的无关！）
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        log.info("用户传过来的注册信息(和登录格式一样的DTO):{}", employeeLoginDTO);
        employeeService.register(employeeLoginDTO);
        return Result.success();
    }

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        log.info("用户传过来的登录信息DTO:{}", employeeLoginDTO);
        EmployeeLoginVO employeeLoginVO = employeeService.login(employeeLoginDTO);

        return Result.success(employeeLoginVO);
    }


    /**
     * 修改当前登录账号的密码
     * @param employeeFixPwdDTO
     * @return
     */
    @PutMapping("/fixpwd")
    public Result fixPwd(@RequestBody EmployeeFixPwdDTO employeeFixPwdDTO){
        log.info("新旧密码信息：{}", employeeFixPwdDTO);
        employeeService.fixPwd(employeeFixPwdDTO);
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping("/add")
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增用户的信息：{}", employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 根据id获取员工信息
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Integer id){
        Employee employee = employeeService.getEmployeeById(id);
        return Result.success(employee);
    }

    /**
     * 员工条件分页查询
     * @param pageDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult<Employee>> employeePageList(PageDTO pageDTO){
        log.info("前端传过来的page参数：{}", pageDTO);
        PageResult<Employee> pageResult = employeeService.employeePageList(pageDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改员工信息（管理员能修改所有，员工只能修改自己）
     * @param employeeDTO
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工的formDTO:{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * 根据id启用禁用员工
     * @param id
     * @return
     */
    @PutMapping("/status/{id}")
    public Result onOff(@PathVariable Integer id){
        log.info("启用禁用员工账号：{}", id);
        employeeService.onOff(id);
        return Result.success();
    }

    /**
     * 管理员根据id删除员工
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        log.info("根据id删除员工,{}", id);
        employeeService.delete(id);
        return Result.success();
    }
}
