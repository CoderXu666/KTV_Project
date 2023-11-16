package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ktv.pojo.KtvHouse;
import com.ktv.pojo.KtvOrderGoods;
import com.ktv.pojo.KtvOrderHouse;
import com.ktv.service.KtvHouseService;
import com.ktv.service.KtvOrderGoodsService;
import com.ktv.service.KtvOrderHouseService;
import com.ktv.utils.R;
import com.ktv.utils.ResponseEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * KTV订单 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/order")
public class KtvOrderController {
    @Autowired
    private KtvOrderGoodsService goodsService;
    @Autowired
    private KtvOrderHouseService houseService;
    @Autowired
    private KtvHouseService ktvHouseService;

    /**
     * 用户查询订单
     */
    @GetMapping("/getEmpList")
    public R getList(String accountId) {
        QueryWrapper<KtvOrderGoods> wrapper1 = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(accountId)) {
            wrapper1.eq("account_id", accountId);
        }
        List<KtvOrderGoods> goodOrderList = goodsService.list(wrapper1);

        QueryWrapper<KtvOrderHouse> wrapper2 = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(accountId)) {
            wrapper2.eq("account_id", accountId);
        }
        List<KtvOrderHouse> houseOrderList = houseService.list(wrapper2);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("good", goodOrderList);
        resultMap.put("house", houseOrderList);
        return R.out(ResponseEnum.SUCCESS, resultMap);
    }

    /**
     * 收银员结账
     */
    @PostMapping("/payMoney/{type}/{id}")
    public R payMoney(@PathVariable Integer type, Long id) {
        // 包间
        if (type == 1) {
            KtvOrderHouse orderHouse = new KtvOrderHouse();
            orderHouse.setId(id);
            orderHouse.setStatus(1);
            houseService.updateById(orderHouse);
        } else {
            KtvOrderGoods orderGoods = new KtvOrderGoods();
            orderGoods.setId(id);
            orderGoods.setStatus(4);
            goodsService.updateById(orderGoods);
        }
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 切换订单状态（只针对工作人员）
     * 比如：厨师做完菜，切换了状态，用户付完款，是不是订单改成已付款
     */
    @PostMapping("/updateStatus/{type}/{orderId}/{status}")
    public R updateStatus(@PathVariable Integer type, @PathVariable Long orderId, @PathVariable Integer status) {
        // 你是包房
        if (type == 1) {
            // 切换包房订单状态
            KtvOrderHouse orderHouse = new KtvOrderHouse();
            orderHouse.setId(orderId);
            orderHouse.setStatus(status);
            houseService.updateById(orderHouse);
            // 特殊情况：如果取消包房
            KtvOrderHouse houseOrderPO = houseService.getById(orderId);
            Long houseId = houseOrderPO.getHouseId();
            // 查询包房信息
            KtvHouse housePO = ktvHouseService.getById(houseId);
            Integer useCount = housePO.getUseCount();
            // 修改可使用包房数量
            KtvHouse house = new KtvHouse();
            house.setId(houseId);
            house.setUseCount(useCount - 1);
            ktvHouseService.updateById(house);
        }

        // 你是菜品
        else {
            KtvOrderGoods orderGood = new KtvOrderGoods();
            orderGood.setId(orderId);
            orderGood.setStatus(status);
            goodsService.updateById(orderGood);
        }
        return R.out(ResponseEnum.SUCCESS);
    }
}

