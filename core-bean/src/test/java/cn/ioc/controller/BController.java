package cn.ioc.controller;

import cn.tocat.bean.annotations.Autowired;
import cn.tocat.bean.annotations.Bean;
import cn.tocat.bean.annotations.Controller;

@Controller
public class BController {

	@Autowired
	private AController aController;
	
	@Bean
	public CController cController() {
		return new CController();
	}

	public void save() {
		aController.hi();
	}

	public void hi() {
		System.out.println(aController);
		System.out.println("b hi");
	}

}
