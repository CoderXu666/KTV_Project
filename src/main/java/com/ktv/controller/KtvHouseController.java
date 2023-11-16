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
    @GetMapping("/getList")
    public R getList() {
        List<KtvHouse> list = houseService.list();
        return R.out(ResponseEnum.SUCCESS, list);
    }

    /**
     * 预定包房
     */
    @PostMapping("/book")
    public R bookHouse(String accountId, Long houseId, LocalDateTime bookTime) {
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

        // 有的话，使用数量 + 1
        Integer useCount = housePO.getUseCount();
        housePO.setCount(useCount + 1);
        houseService.saveOrUpdate(housePO);

        // 保存订单
        KtvOrderHouse orderHouse = new KtvOrderHouse();
        orderHouse.setAccountId(accountId);
        orderHouse.setHouseId(houseId);
        orderHouse.setStatus(0);
        orderHouse.setCreateTime(bookTime);
        orderHouseService.save(orderHouse);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 取消包房订单
     */
    @PostMapping("/cancel/{id}/{status}")
    public R cancel(@PathVariable Long id, @PathVariable Integer status) {
        KtvOrderHouse orderHouse = new KtvOrderHouse();
        orderHouse.setId(id);
        orderHouse.setStatus(status);
        orderHouseService.updateById(orderHouse);
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

