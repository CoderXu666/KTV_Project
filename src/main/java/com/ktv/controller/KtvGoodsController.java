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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * KTV商品 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-11-17
 */
@RestController
@RequestMapping("/goods")
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
    @GetMapping("/getlist")
    public R getlist(){
        List<KtvGoods> ktvGoods = ktvGoodsService.list();
        return R.out(ResponseEnum.SUCCESS, ktvGoods);
    }

    /**
     * 下单菜品
     */
    @PostMapping("/save/{id}/{accountId}")
    public R save(@PathVariable Long id,@PathVariable String accountId){
        // 创建订单对象
        KtvOrderGoods ktvOrderGoods = new KtvOrderGoods();
        ktvOrderGoods.setGoodId(id.toString());  // 将菜品ID转换为字符串，用于订单记录
        ktvOrderGoods.setStatus(1); // 设置订单状态为1（可能表示订单已下单）

        // 保存订单信息到数据库
        ktvOrderGoodsService.save(ktvOrderGoods);

        // 从用户余额扣钱
        KtvUser ktvUser = ktvUserService.getById(id);
        Integer money = ktvUser.getMoney();
        // 从用户表余额减去商品价格，然后更新
        // 商品价格
        KtvGoods ktvGoods = ktvGoodsService.getById(id);
        Integer price = ktvGoods.getPrice();
        int moneys = money - price;
        ktvUser.setMoney(moneys);
        // 更新
        ktvUserService.updateById(ktvUser);

        return R.out(ResponseEnum.SUCCESS,"菜品下单成功");
    }

    /**
     * 菜单结账
     */


    /**
     * 取消菜单
     */
}

