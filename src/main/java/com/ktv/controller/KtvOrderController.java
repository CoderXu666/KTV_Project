package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ktv.pojo.KtvOrderGoods;
import com.ktv.pojo.KtvOrderHouse;
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
}

