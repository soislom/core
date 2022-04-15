package cn.ioc.controller;

import cn.tocat.bean.annotations.Autowired;
import cn.tocat.bean.annotations.Controller;
import cn.tocat.bean.annotations.Value;

@Controller
//@Import(value = { CController.class })
public class AController {

	@Autowired
	private BController bController;
	
	@Autowired
	private CController cController;

	@Value("ioc.path")
	private String path;

	public void save() {
		bController.hi();
	}

	public void hi() {
		System.out.println(bController);
		System.out.println(cController);
		System.out.println("a hi");
		System.out.println(path);
	}

}
