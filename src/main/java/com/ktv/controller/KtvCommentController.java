package com.ktv.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ktv.pojo.KtvComment;
import com.ktv.service.KtvCommentService;
import com.ktv.utils.R;
import com.ktv.utils.ResponseEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * KTV评价 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/comment")
public class KtvCommentController {
    @Autowired
    private KtvCommentService commentService;

    /**
     * 查询评论列表（首次评论）
     */
    @GetMapping("/list")
    public R list() {
        QueryWrapper<KtvComment> wrapper = new QueryWrapper<>();
        wrapper.isNull("parent_id");
        wrapper.orderByDesc("create_time");
        List<KtvComment> list = commentService.list(wrapper);
        return R.out(ResponseEnum.SUCCESS, list);
    }

    /**
     * 查询评论详情（里面子列表）
     */
    @GetMapping("/detail/{id}")
    public R subList(@PathVariable Long id) {
        QueryWrapper<KtvComment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<KtvComment> list = commentService.list(wrapper);
        return R.out(ResponseEnum.SUCCESS, list);
    }


    /**
     * 发起评论（首次不存parent_id）
     */
    @PostMapping("/save")
    public R save(Long parentId, String accountId, String content) {
        KtvComment ktvComment = new KtvComment();
        if (ObjectUtils.isEmpty(parentId)) {
            ktvComment.setAccountId(accountId);
            ktvComment.setContent(content);
        } else {
            ktvComment.setParentId(parentId);
            ktvComment.setAccountId(accountId);
            ktvComment.setContent(content);
        }
        ktvComment.setCreateTime(LocalDateTime.now());
        commentService.save(ktvComment);
        return R.out(ResponseEnum.SUCCESS);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        commentService.removeById(id);
        QueryWrapper<KtvComment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        commentService.remove(wrapper);
        return R.out(ResponseEnum.SUCCESS);
    }
}

