package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ktv.pojo.KtvOrderGoods;
import com.ktv.pojo.KtvOrderHouse;
import com.ktv.pojo.KtvUser;
import com.ktv.service.KtvOrderGoodsService;
import com.ktv.service.KtvOrderHouseService;
import com.ktv.service.KtvUserService;
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
    private KtvUserService userService;

    /**
     * 用户查询订单
     */
    @GetMapping("/list")
    public R getList(String accountId) {
        // 如果不是顾客，那就全查
        QueryWrapper<KtvUser> wrapper = new QueryWrapper<>();
        wrapper.eq("account_id", accountId);
        KtvUser userPO = userService.getOne(wrapper);
        if (!userPO.getRole().equals(5) || !userPO.getRole().equals(6)) {
            List<KtvOrderGoods> goodList = goodsService.list();
            List<KtvOrderHouse> houseList = houseService.list();
            // 返回
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("good", goodList);
            resultMap.put("house", houseList);
            return R.out(ResponseEnum.SUCCESS, resultMap);
        } else {
            // 查询菜品订单信息
            QueryWrapper<KtvOrderGoods> wrapper1 = new QueryWrapper<>();
            if (StringUtils.isNotEmpty(accountId)) {
                wrapper1.eq("account_id", accountId);
            }
            List<KtvOrderGoods> goodOrderList = goodsService.list(wrapper1);


            // 查询包房订单信息
            QueryWrapper<KtvOrderHouse> wrapper2 = new QueryWrapper<>();
            if (StringUtils.isNotEmpty(accountId)) {
                wrapper2.eq("account_id", accountId);
            }
            List<KtvOrderHouse> houseOrderList = houseService.list(wrapper2);

            // 返回
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("good", goodOrderList);
            resultMap.put("house", houseOrderList);
            return R.out(ResponseEnum.SUCCESS, resultMap);
        }
    }

    /**
     * TODO 现在我只交包房定金，包房尾款没付
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
        }
        // 菜品
        else {
            KtvOrderGoods orderGoods = new KtvOrderGoods();
            orderGoods.setId(id);
            orderGoods.setStatus(4);
            goodsService.updateById(orderGoods);
        }
        return R.out(ResponseEnum.SUCCESS);
    }
}

