package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ktv.pojo.KtvUser;
import com.ktv.service.KtvUserService;
import com.ktv.utils.R;
import com.ktv.utils.ResponseEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
     * 登录功能
     * 成功切换标识，
     */
    @PostMapping("/login")
    public R login(String accountId, String password) {
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser userPO = userService.getOne(wrapper);
        if (ObjectUtils.isEmpty(userPO)) {
            return R.out(ResponseEnum.FAIL);
        }
        if (!password.equals(userPO.getPassword())) {
            return R.out(ResponseEnum.FAIL);
        }

        List<KtvUser> userList = userService.list();
        for (KtvUser user : userList) {
            user.setStatus("N");
        }
        userService.updateBatchById(userList);
        userPO.setStatus("Y");
        userService.updateById(userPO);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 查询登陆中的用户
     */
    @GetMapping("/getLoginUser")
    public R getLoginUser() {
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "Y");
        List<KtvUser> userList = userService.list(wrapper);
        KtvUser user = userList.get(0);
        return R.out(ResponseEnum.SUCCESS, user);
    }

    /**
     * 查询所有用户列表
     */
    @GetMapping("/list")
    public R getList() {
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.ne("role", 1);
        List<KtvUser> userList = userService.list(wrapper);
        return R.out(ResponseEnum.SUCCESS, userList);
    }

    /**
     * 注册会员
     */
    @PostMapping("/vip/register/{id}")
    public R register(@PathVariable Long id) {
        KtvUser userPO = userService.getById(id);

        // 如果账户已经是vip了，变成庶民
        if (userPO.getRole().equals(6)) {
            KtvUser user = new KtvUser();
            user.setId(userPO.getId());
            user.setRole(5);
            userService.updateById(user);
        } else {
            // 不是会员，注册成为会员
            KtvUser user = new KtvUser();
            user.setId(userPO.getId());
            user.setRole(6);
            userService.updateById(user);
        }
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 查询员工信息
     */
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id) {
        KtvUser userPO = userService.getById(id);
        return R.out(ResponseEnum.SUCCESS, userPO);
    }

    /**
     * 删除员工信息
     */
    @DeleteMapping("/delete/{id}")
    public R deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 查询vip列表
     */
    @GetMapping("/getVipList")
    public R getVipList() {
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("role", 6);
        List<KtvUser> vipList = userService.list(wrapper);
        return R.out(ResponseEnum.SUCCESS, vipList);
    }

    /**
     * 修改vip状态
     */
    @PostMapping("/updateVip/{accountId}/{role}")
    public R updateVip(@PathVariable String accountId, @PathVariable Integer role) {
        // 查询vip信息
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser user = userService.getOne(wrapper);

        // 如果用户不存在
        if (user == null) {
            return R.out(ResponseEnum.FAIL, "用户不存在");
        }

        // 更新用户角色
        user.setRole(role);
        boolean updated = userService.updateById(user);

        // 检查更新是否成功
        if (updated) {
            return R.out(ResponseEnum.SUCCESS, "VIP状态更新成功");
        } else {
            return R.out(ResponseEnum.FAIL, "VIP状态更新失败");
        }
    }

    /**
     * 保存用户信息
     */
    @PostMapping("/save")
    public R saveUser(@RequestBody KtvUser user) {
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        user.setMoney(0);
        userService.saveOrUpdate(user);
        return R.out(ResponseEnum.SUCCESS);
    }
}

