package cn.ioc;

import cn.ioc.controller.AController;
import cn.ioc.controller.BController;
import cn.tocat.bean.core.ApplicationContext;
import cn.tocat.bean.core.ContextUtils;

public class PlatformApplication {

	public static void main(String[] args) throws Exception {
		ApplicationContext.run(PlatformApplication.class, args);
		AController aController = (AController) ContextUtils.getBean("AController");
		aController.hi();
		System.out.println("----------------------------------");
		BController bController = ContextUtils.getBean("BController", BController.class);
		bController.hi();
	}

}
