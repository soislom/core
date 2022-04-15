package cn.ioc.service.impl;

import cn.ioc.service.LoginService;
import cn.tocat.bean.annotations.Service;

@Service(value = "testLoginServiceImpl")
public class TestLoginServiceImpl implements LoginService {

    @Override
    public String login() {
        return "测试多态情况下依赖注入";
    }
}
