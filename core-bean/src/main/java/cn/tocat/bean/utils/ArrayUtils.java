package cn.tocat.bean.utils;
public class ArrayUtils {

	/**
	 * @author: JiaYao
	 * @demand: 判断数组中是否包含元素
	 * @parameters: @creationDate：
	 * @email: huangjy19940202@gmail.com
	 */
	public static boolean useArrayUtils(String[] arr, String targetValue) {
		return contains(arr, targetValue);
	}

	/**
	 * @author: JiaYao
	 * @demand: 判断数组中是否包含元素
	 * @parameters: @creationDate：
	 * @email: huangjy19940202@gmail.com
	 */
	public static boolean useArrayUtils(Class<?>[] arr, Class<?> targetValue) {
		return contains(arr, targetValue);
	}

	public static boolean contains(Object[] array, Object value) {
		for (Object arr : array) {
			if (arr == value) {
				return true;
			}
		}
		return false;
	}

}