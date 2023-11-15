package com.ktv.service.impl;

import com.ktv.pojo.KtvUser;
import com.ktv.mapper.KtvUserMapper;
import com.ktv.service.KtvUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 账号 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2023-11-15
 */
@Service
public class KtvUserServiceImpl extends ServiceImpl<KtvUserMapper, KtvUser> implements KtvUserService {

}
