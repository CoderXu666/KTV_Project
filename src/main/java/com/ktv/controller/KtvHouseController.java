package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ktv.pojo.KtvHouse;
import com.ktv.pojo.KtvOrderHouse;
import com.ktv.pojo.KtvUser;
import com.ktv.service.KtvHouseService;
import com.ktv.service.KtvOrderHouseService;
import com.ktv.service.KtvUserService;
import com.ktv.utils.R;
import com.ktv.utils.ResponseEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * KTV包房 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/house")
public class KtvHouseController {
    @Autowired
    private KtvUserService userService;
    @Autowired
    private KtvHouseService houseService;
    @Autowired
    private KtvOrderHouseService orderHouseService;

    /**
     * 查询所有包房
     */
    @GetMapping("/list")
    public R getList() {
        List<KtvHouse> list = houseService.list();
        return R.out(ResponseEnum.SUCCESS, list);
    }

    /**
     * 查询包房信息
     */
    @GetMapping("/detail/{id}")
    public R getHouseDetail(@PathVariable Long id) {
        KtvHouse house = houseService.getById(id);
        return R.out(ResponseEnum.SUCCESS, house);
    }

    /**
     * 收银员：完成订单
     */
    @PostMapping("/finish/{id}")
    public R finish(@PathVariable Long id) {
        KtvOrderHouse orderHouse = orderHouseService.getById(id);
        orderHouse.setStatus(1);
        orderHouseService.updateById(orderHouse);

        String accountId = orderHouse.getAccountId();
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser ktvUser = userService.getOne(wrapper);

        Long houseId = orderHouse.getHouseId();
        KtvHouse houseInfo = houseService.getById(houseId);

        Integer money = ktvUser.getMoney();
        Integer remainMoney = money - (houseInfo.getPrice() - houseInfo.getBookPrice());

        ktvUser.setMoney(remainMoney);
        userService.updateById(ktvUser);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 创建包房
     */
    @PostMapping("/save")
    public R saveHouse(String size, Integer bookPrice, Integer price, Integer total, String url) {
        KtvHouse ktvHouse = new KtvHouse();
        ktvHouse.setSize(size);
        ktvHouse.setBookPrice(bookPrice);
        ktvHouse.setPrice(price);
        ktvHouse.setCount(total);
        ktvHouse.setUseCount(0);
        ktvHouse.setUrl(url);
        houseService.saveOrUpdate(ktvHouse);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 退房
     */
    @PostMapping("/cancelHouse/{id}")
    public R cancelHouse(@PathVariable Long id) {
        // 取消包房
        KtvOrderHouse houseOrder = orderHouseService.getById(id);
        if (houseOrder.getStatus().equals(1)) {
            return R.out(ResponseEnum.FAIL, "包房已使用，不可取消");
        }
        houseOrder.setStatus(2);
        orderHouseService.updateById(houseOrder);

        // 退钱
        Long houseId = houseOrder.getHouseId();
        KtvHouse houseInfo = houseService.getById(houseId);
        Integer bookPrice = houseInfo.getBookPrice();

        // 查询用户信息
        String accountId = houseOrder.getAccountId();
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser userInfo = userService.getOne(wrapper);

        // 计算价钱
        Integer remainMoney = userInfo.getMoney();
        userInfo.setMoney(remainMoney + bookPrice);
        userService.updateById(userInfo);

        // 使用数量 - 1
        Integer remainCount = houseInfo.getUseCount();
        houseInfo.setUseCount(remainCount - 1);
        houseService.updateById(houseInfo);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 删除菜品订单
     */
    @DeleteMapping("/order/delete/{id}")
    public R deleteMenu(@PathVariable Long id) {
        orderHouseService.removeById(id);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 修改包房
     */
    @PostMapping("/update")
    public R update(@RequestBody KtvHouse house) {
        houseService.updateById(house);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 预定包房
     */
    @PostMapping("/book")
    public R bookHouse(String accountId, Long houseId) {
        // 判断是否是会员
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser userPO = userService.getOne(wrapper);
        if (!userPO.getRole().equals(6)) {
            return R.out(ResponseEnum.FAIL, "不能预定，你不是VIP");
        }

        // 判断数量还有没有
        KtvHouse housePO = houseService.getById(houseId);
        if (housePO.getUseCount() == housePO.getCount()) {
            return R.out(ResponseEnum.FAIL, "该包房数量不足");
        }

        // 交付定金
        Integer money = userPO.getMoney();
        Integer bookPrice = housePO.getBookPrice();
        int leaveMoney = money - bookPrice;

        // 修改余额
        userPO.setMoney(leaveMoney);
        userService.updateById(userPO);

        // 查询包房信息
        KtvHouse house = houseService.getById(houseId);

        // 保存订单
        KtvOrderHouse orderHouse = new KtvOrderHouse();
        orderHouse.setHouseId(houseId);
        orderHouse.setAccountId(accountId);
        orderHouse.setGoodUrl(house.getUrl());
        orderHouse.setGoodName(house.getSize());
        orderHouse.setStatus(0);
        orderHouseService.save(orderHouse);

        // 使用数量 + 1
        Integer useCount = housePO.getUseCount();
        housePO.setUseCount(useCount + 1);
        houseService.saveOrUpdate(housePO);

        // 删除订单
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 查询超时 + 未使用包房
     */
    @GetMapping("/getUnUseList")
    public R getUnUseList() {
        // 查询未使用包房订单
        QueryWrapper<KtvOrderHouse> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        List<KtvOrderHouse> houseList = orderHouseService.list(wrapper);

        // 判断是否超时（预定时间 < 当前）
        List<KtvOrderHouse> overList = new ArrayList<>();
        List<KtvOrderHouse> unOverList = new ArrayList<>();
        for (KtvOrderHouse orderHouse : houseList) {
            // 说明过期
            if (orderHouse.getCreateTime().isBefore(LocalDateTime.now())) {
                overList.add(orderHouse);
            }
            // 说明没过期
            else {
                unOverList.add(orderHouse);
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("over", overList);
        resultMap.put("unOver", unOverList);
        return R.out(ResponseEnum.SUCCESS, resultMap);
    }
}

