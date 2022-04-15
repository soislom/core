package cn.ioc.controller;

import cn.ioc.service.LoginService;
import cn.tocat.bean.annotations.Autowired;
import cn.tocat.bean.annotations.Controller;
import cn.tocat.bean.annotations.Value;

@Controller
public class LoginController {

	@Value(value = "ioc.scan.pathTest")
	private String pathTest;

//    @MyAutowired(value = "loginServiceImplTest")
	@Autowired
	private LoginService loginServiceImplTest;

	@Autowired
	private LoginService testLoginServiceImpl;

	public String login() {
		return loginServiceImplTest.login();
	}

}
