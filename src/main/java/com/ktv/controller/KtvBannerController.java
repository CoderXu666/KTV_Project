package com.ktv.controller;


import com.ktv.pojo.KtvBanner;
import com.ktv.service.KtvBannerService;
import com.ktv.utils.R;
import com.ktv.utils.ResponseEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * KTV公告 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/banner")
public class KtvBannerController {
    @Autowired
    private KtvBannerService bannerService;

    /**
     * 查询公告
     */
    @GetMapping("/list")
    public R list() {
        List<KtvBanner> list = bannerService.list();
        return R.out(ResponseEnum.SUCCESS, list);
    }

    /**
     * 创建公告
     */
    @PostMapping("/save")
    public R save(String content, String url) {
        KtvBanner ktvBanner = new KtvBanner();
        ktvBanner.setUrl(url);
        ktvBanner.setContent(content);
        ktvBanner.setCreateTime(LocalDateTime.now());
        ktvBanner.setDeleted(false);
        bannerService.save(ktvBanner);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 修改公告
     */
    @PostMapping("/update")
    public R update(Long id, String content) {
        KtvBanner banner = new KtvBanner();
        banner.setContent(content);
        banner.setId(id);
        bannerService.updateById(banner);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return R.out(ResponseEnum.SUCCESS);
    }
}

