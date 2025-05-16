package com.hzy.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hzy.constant.JwtClaimsConstant;
import com.hzy.constant.MessageConstant;
import com.hzy.constant.PasswordConstant;
import com.hzy.constant.StatusConstant;
import com.hzy.context.BaseContext;
import com.hzy.dto.EmployeeDTO;
import com.hzy.dto.EmployeeFixPwdDTO;
import com.hzy.dto.EmployeeLoginDTO;
import com.hzy.dto.PageDTO;
import com.hzy.entity.Employee;
import com.hzy.exception.AccountLockedException;
import com.hzy.exception.PasswordErrorException;
import com.hzy.exception.EmployeeNotFoundException;
import com.hzy.mapper.EmployeeMapper;
import com.hzy.properties.JwtProperties;
import com.hzy.result.PageResult;
import com.hzy.service.EmployeeService;
import com.hzy.utils.JwtUtil;
import com.hzy.vo.EmployeeLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    //TODO 登录逐步校验，方便用户纠错
    public EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO) {
        String account = employeeLoginDTO.getAccount();
        String password = employeeLoginDTO.getPassword();
        // 先查数据库，看是否存在该账号
        Employee employee = employeeMapper.getByAccount(account);
        if (employee == null){
            throw new EmployeeNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 再将前端传过来的密码进行MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 和之前存进数据库的加密的密码进行比对，看看是否一样，不一样要抛异常
        if (!password.equals(employee.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        //查询是否为0，是则被锁定
        if (StatusConstant.UNABLE.equals(employee.getStatus())){
            throw new AccountLockedException(MessageConstant.EMPLOYEE_NOT_ENABLED);
        }

        // 上面的没抛异常，正常来到这里，说明登录成功
        // claims就是用户数据payload部分
        Map<String, Object> claims = new HashMap<>();
        // jsonwebtoken包底层就是Map<String, Object>格式，不能修改！
        claims.put(JwtClaimsConstant.EMPLOYEE_ID, employee.getId());
        // 需要加个token给他，再返回响应
        String token = JwtUtil.createJWT(
                jwtProperties.getEmployeeSecretKey(),
                jwtProperties.getEmployeeTtl(),
                claims);
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .account(employee.getAccount())
                .token(token)
                .build();
        return employeeLoginVO;
    }

    /**
     * 注册/新增员工
     */
    //TODO MD5加密操作
    public void register(EmployeeLoginDTO employeeLoginDTO) {
        // 先对用户的密码进行MD5加密，再存到数据库中
        String password = employeeLoginDTO.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        Employee employee =  Employee.builder()
                .name("员工1")
                .account(employeeLoginDTO.getAccount())
                .password(password)
                .phone("12341234567")
                .age(20)
                .gender(1)
                .pic("")
                .status(StatusConstant.ENABLE)
                .createUser(100)
                .updateUser(100)
                .build();
        employeeMapper.regEmployee(employee);
    }

    /**
     * 根据id获取员工信息
     * @return
     */
    public Employee getEmployeeById(Integer id) {
        Employee employee = employeeMapper.getById(id);
        return employee;
    }

    /**
     * 员工分页查询
     * @return
     */
    public PageResult<Employee> employeePageList(PageDTO pageDTO) {
        // 传分页参数给PageHelper自动处理，会自动加上limit和count(*)返回分页结果和总记录数
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getPageSize());
        Page<Employee> pagelist = employeeMapper.pageQuery(pageDTO);
        return new PageResult(pagelist.getTotal(), pagelist.getResult());
    }

    /**
     * 修改员工
     * @param employeeDTO
     */
    public void update(EmployeeDTO employeeDTO) {
        // 缺少时间等字段，需要手动加入，否则Mapper里的autofill注解会为EmployeeDTO去setUpdateTime，然而根本没这个方法导致报错！
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }

    /**
     * 删除员工
     */
    public void delete(Integer id) {
        employeeMapper.delete(id);
    }

    /**
     * 根据id修改员工状态
     * @param id
     */
    public void onOff(Integer id) {
        employeeMapper.onOff(id);
    }

    /**
     * 管理员新增员工
     * @param employeeDTO
     */
    public void addEmployee(EmployeeDTO employeeDTO) {
        // 创建employee对象，将employeeDTO的属性拷贝到employee中
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        // 为user其他字段填充默认值
        //初始锁定
        employee.setStatus(StatusConstant.UNABLE);
        //默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employeeMapper.addEmployee(employee);
    }

    /**
     * 修改密码
     * @param employeeFixPwdDTO
     */
    public void fixPwd(EmployeeFixPwdDTO employeeFixPwdDTO) {
        String oldPwd = employeeFixPwdDTO.getOldPwd();
        // 将前端传过来的旧密码进行MD5加密
        oldPwd = DigestUtils.md5DigestAsHex(oldPwd.getBytes());
        // 根据id查询当前账号信息
        Integer id = BaseContext.getCurrentId();
        Employee employee = employeeMapper.getById(id);
        // 和之前存进数据库的加密的密码进行比对，看看是否一样，不一样要抛异常
        if (!oldPwd.equals(employee.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        // 旧密码正确，将新密码加密后进行更新
        String newPwd = employeeFixPwdDTO.getNewPwd();
        String password = DigestUtils.md5DigestAsHex(newPwd.getBytes());
        employee.setPassword(password);
        employeeMapper.updatePwd(employee);
    }
}
