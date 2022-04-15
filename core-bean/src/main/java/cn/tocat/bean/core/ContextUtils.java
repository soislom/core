package cn.tocat.bean.core;

import cn.tocat.bean.utils.Assert;
import cn.tocat.bean.utils.StringUtils;

/**
 * 上下文工具类
 * 
 * @author wuchen
 *
 */
public class ContextUtils {

	private static final ApplicationContext CONTEXT = ApplicationContext.context;

	/**
	 * @demand: 从IOC容器中获取对象
	 */
	public static Object getBean(String beanName) {
		Assert.isNull(beanName);
		return CONTEXT.get(StringUtils.toLowercaseIndex(beanName));
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName, Class<T> classType) {
		return (T) CONTEXT.get(StringUtils.toLowercaseIndex(beanName));
	}
}
