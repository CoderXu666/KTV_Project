package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ktv.pojo.KtvGoods;
import com.ktv.pojo.KtvOrderGoods;
import com.ktv.pojo.KtvUser;
import com.ktv.service.KtvGoodsService;
import com.ktv.service.KtvOrderGoodsService;
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
 * KTV商品 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-11-17
 */

/**
 * ✔
 * <p>
 * KTV商品 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-11-17
 */
@RestController
@RequestMapping("/menu")
public class KtvGoodsController {
    @Autowired
    private KtvGoodsService ktvGoodsService;
    @Autowired
    private KtvOrderGoodsService ktvOrderGoodsService;
    @Autowired
    private KtvUserService ktvUserService;

    /**
     * 查询菜单
     */
    @GetMapping("/list")
    public R getList() {
        List<KtvGoods> ktvGoods = ktvGoodsService.list();
        return R.out(ResponseEnum.SUCCESS, ktvGoods);
    }

    /**
     * 查询菜单
     */
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id) {
        KtvGoods good = ktvGoodsService.getById(id);
        return R.out(ResponseEnum.SUCCESS, good);
    }

    /**
     * 修改菜单
     */
    @PostMapping("/update")
    public R update(@RequestBody KtvGoods goods) {
        ktvGoodsService.updateById(goods);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 删除菜品订单
     */
    @DeleteMapping("/order/delete/{id}")
    public R deleteMenu(@PathVariable Long id) {
        ktvOrderGoodsService.removeById(id);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 下单菜品
     * 注意：菜品预定付全款
     */
    @PostMapping("/book/{id}/{accountId}")
    public R save(@PathVariable Long id, @PathVariable String accountId) {
        // 查询用户余额
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser user = ktvUserService.getOne(wrapper);
        Integer money = user.getMoney();

        // 查询商品价格
        KtvGoods ktvGoods = ktvGoodsService.getById(id);
        Integer price = ktvGoods.getPrice();

        //余额减去商品价格 差价
        int moneys = money - price;
        user.setMoney(moneys);
        ktvUserService.updateById(user);

        // 查询商品信息
        KtvGoods good = ktvGoodsService.getById(id);

        // 下单 (保存)
        KtvOrderGoods ktvOrderGoods = new KtvOrderGoods();
        ktvOrderGoods.setGoodId(id + "");
        ktvOrderGoods.setStatus(1);
        ktvOrderGoods.setGoodName(good.getName());
        ktvOrderGoods.setGoodUrl(good.getUrl());
        ktvOrderGoods.setAccountId(accountId);
        ktvOrderGoods.setCreateTime(LocalDateTime.now());
        ktvOrderGoodsService.save(ktvOrderGoods);
        return R.out(ResponseEnum.SUCCESS, "菜品下单成功");
    }

    /**
     * 下单菜品
     * 注意：菜品预定付全款
     */
    @PostMapping("/click")
    public R save(String accountId, Long menuId) {
        // 查询用户余额
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser user = ktvUserService.getOne(wrapper);
        if (ObjectUtils.isEmpty(user)) {
            return R.out(ResponseEnum.FAIL, "该用户不存在");
        }
        Integer money = user.getMoney();

        // 查询商品价格
        KtvGoods ktvGoods = ktvGoodsService.getById(menuId);
        Integer price = ktvGoods.getPrice();

        //余额减去商品价格 差价
        int moneys = money - price;
        user.setMoney(moneys);
        ktvUserService.updateById(user);

        // 查询商品信息
        KtvGoods good = ktvGoodsService.getById(menuId);

        // 下单 (保存)
        KtvOrderGoods ktvOrderGoods = new KtvOrderGoods();
        ktvOrderGoods.setGoodId(menuId + "");
        ktvOrderGoods.setStatus(1);
        ktvOrderGoods.setGoodName(good.getName());
        ktvOrderGoods.setGoodUrl(good.getUrl());
        ktvOrderGoods.setAccountId(accountId);
        ktvOrderGoods.setCreateTime(LocalDateTime.now());
        ktvOrderGoodsService.save(ktvOrderGoods);
        return R.out(ResponseEnum.SUCCESS, "菜品下单成功");
    }

    /**
     * 取消菜单（服务员、用户都能用）
     */
    @PostMapping("/cancel/{id}")
    public R cancellation(@PathVariable Long id) {
        // 菜品订单表改为取消状态
        KtvOrderGoods orderGoods = ktvOrderGoodsService.getById(id);
        if (orderGoods.getStatus().equals(3)) {
            return R.out(ResponseEnum.SUCCESS, "菜品已完成制作，不可退款！");
        }

        orderGoods.setStatus(2);
        ktvOrderGoodsService.updateById(orderGoods);

        // 查订单里面的账号和菜品id
        String accountId = orderGoods.getAccountId();
        String goodId = orderGoods.getGoodId();

        // 通过订单id查菜品价格
        QueryWrapper<KtvGoods> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("id", goodId);
        KtvGoods price = ktvGoodsService.getOne(wrapper2);
        Integer price1 = price.getPrice();

        // 通过账号把取消菜单的菜品价格退回顾客，更新余额。
        QueryWrapper<KtvUser> wrapper3 = new QueryWrapper<>();
        wrapper3.eq("account_id", accountId);
        KtvUser user = ktvUserService.getOne(wrapper3);
        Integer money = user.getMoney();

        // 退款，价格相加
        int newMoney = money + price1;
        user.setMoney(newMoney);
        ktvUserService.updateById(user);
        return R.out(ResponseEnum.SUCCESS, "取消菜单成功，金额已退回。");
    }

    /**
     * 厨师：完成订单
     */
    @PostMapping("/finish/{id}")
    public R finish(@PathVariable Long id) {
        KtvOrderGoods order = ktvOrderGoodsService.getById(id);
        order.setStatus(3);
        ktvOrderGoodsService.updateById(order);
        return R.out(ResponseEnum.SUCCESS);
    }
}

