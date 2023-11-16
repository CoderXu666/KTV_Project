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
import java.util.Date;
import java.util.List;

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
}

