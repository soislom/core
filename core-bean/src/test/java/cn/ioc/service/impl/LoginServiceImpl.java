package cn.ioc.service.impl;

import cn.ioc.dao.LoginMapping;
import cn.ioc.service.LoginService;
import cn.tocat.bean.annotations.Autowired;
import cn.tocat.bean.annotations.Service;

@Service(value = "loginServiceImplTest")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapping loginMapping;

    @Override
    public String login() {
        return loginMapping.login();
    }
}
