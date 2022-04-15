package cn.tocat.bean.utils;
public class Assert {

	private static final String DEFAULT_MESSAGE = "参数不能为空";

	/**
	 * 断言参数不能为空
	 * 
	 * @param param
	 */
	public static void isNull(Object param) {
		isNull(DEFAULT_MESSAGE, param);
	}

	/**
	 * 断言参数不能为空
	 * 
	 * @param param
	 * @param message
	 */
	public static void isNull(Object param, String message) {
		isNull(message, param);
	}
	
	/**
	 * 断言参数不能为空
	 * @param params
	 */
	public static void isNull(String message, Object...params) {
		if(null == params)
			throw new NullPointerException(message);
		for(Object obj : params) {
			if(null == obj)
				throw new NullPointerException(message);
		}
	}

	
	/**
	 * 判断参数是否为空并赋默认值
	 * @param object
	 * @param defaultValue
	 */
	public static void isEmpty(Object object, String defaultValue) {
		if (null == object)
			Assert.isNull(defaultValue, "默认值不能为空");
			object = defaultValue;
	}
	
	

}