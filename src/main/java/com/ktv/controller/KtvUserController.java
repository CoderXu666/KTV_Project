package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
     * 注册会员
     */
    @PostMapping("/register/{accountId}")
    public R register(@PathVariable String accountId) {
        // 查询账户
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser userPO = userService.getOne(wrapper);

        // 如果账户已经是vip了，就不要注册了
        if (userPO.getRole().equals(6)) {
            return R.out(ResponseEnum.SUCCESS, "已经是会员了，不能重复注册！");
        }

        // 不是会员，注册成为会员
        KtvUser user = new KtvUser();
        user.setId(userPO.getId());
        user.setRole(6);
        userService.updateById(user);
        return R.out(ResponseEnum.SUCCESS);
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

    /**
     * TODO VIP操作的CRUD（查询vip列表，修改vip状态，注册vip）
     * TODO 顾客可以点餐（查询菜单，下单， 取消菜单）
     */

    /**
     * 查询vip列表
     */
    @GetMapping("/getVipList")
    public R getVipList(){
        // 查询vip用户
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("role",6);
        List<KtvUser> vipList = userService.list(wrapper);
        return R.out(ResponseEnum.SUCCESS, vipList);
    }

    /**
     * 修改vip状态
     */
    @PostMapping("/updateVip/{account_id}/{role}")
    public R updateVip(@PathVariable String account_id, @PathVariable Integer role){
        // 查询vip信息
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", account_id);
        KtvUser user = userService.getOne(wrapper);

        // 如果用户不存在
        if (user == null) {
            return R.out(ResponseEnum.FAIL, "用户不存在");
        }

        // 更新用户角色
        user.setRole(role);
        boolean updated = userService.updateById(user);

        // 检查更新是否成功
        if(updated) {
            return R.out(ResponseEnum.SUCCESS, "VIP状态更新成功");
        } else {
            return R.out(ResponseEnum.FAIL, "VIP状态更新失败");
        }
    }

    /**
     * 注册vip
     */
    @PostMapping("/registerVip/{account_id}")
    public R registerVip(@PathVariable String account_id){
        // 查询用户
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", account_id);
        KtvUser user = userService.getOne(wrapper);

        // 用户是否存在
        if(user == null){
            return R.out(ResponseEnum.FAIL,"用户不存在");
        }
        // 如果是vip
        if(user.getRole() != null && user.getRole().equals(6)){
            return R.out(ResponseEnum.SUCCESS,"用户是vip");

        }

        // 不是vip，设为vip
        user.setRole(6);
        boolean updated = userService.updateById(user);
        if(updated){
            return R.out(ResponseEnum.SUCCESS, "注册vip成功");
        } else{
            return R.out(ResponseEnum.FAIL, "注册vip失败");
        }
    }

    /**
     * 查询菜单
     */

    /**
     * 下单
     */

    /**
     * 取消菜单
     */
}

