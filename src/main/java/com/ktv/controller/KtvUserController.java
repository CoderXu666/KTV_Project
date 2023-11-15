package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ktv.pojo.KtvUser;
import com.ktv.service.KtvUserService;
import com.ktv.utils.R;
import com.ktv.utils.ResponseEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 账号 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/user")
public class KtvUserController {
    @Autowired
    private KtvUserService userService;

    /**
     * 查询所有员工信息
     */
    @GetMapping("/getEmpList")
    public R getList() {
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.ne("role", 5);
        wrapper.ne("role", 6);
        List<KtvUser> userList = userService.list(wrapper);
        return R.out(ResponseEnum.SUCCESS, userList);
    }

    /**
     * 删除员工信息
     */
    @DeleteMapping("/delete/{id}")
    public R deleteUser(@PathVariable Long id) {
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        userService.remove(wrapper);
        return R.out(ResponseEnum.SUCCESS);
    }
}

