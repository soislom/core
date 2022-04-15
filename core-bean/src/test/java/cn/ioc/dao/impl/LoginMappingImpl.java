package cn.ioc.dao.impl;

import cn.ioc.dao.LoginMapping;
import cn.tocat.bean.annotations.Mapping;

@Mapping
public class LoginMappingImpl implements LoginMapping {

    @Override
    public String login() {
        return "项目启动成功";
    }
}
