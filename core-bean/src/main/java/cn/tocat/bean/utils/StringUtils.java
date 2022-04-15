package cn.tocat.bean.utils;
public class StringUtils {

	public static boolean isEmpty(String param) {
		return param == null || param.isEmpty();
	}

	public static boolean isNotEmpty(String param) {
		return !isEmpty(param);
	}

	/**
	 * 类名首字母转小写
	 * 
	 * @param name
	 * @return
	 */
	public static String toLowercaseIndex(String name) {
		if (StringUtils.isNotEmpty(name)) {
			return name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
		}
		return name;
	}

	/**
	 * 类名首字母转大写
	 * 
	 * @param name
	 * @return
	 */
	public static String toUpperCaseIndex(String name) {
		if (StringUtils.isNotEmpty(name)) {
			return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
		}
		return name;
	}

}
