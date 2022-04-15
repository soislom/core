package cn.tocat.bean.core;

import static cn.tocat.bean.utils.ArrayUtils.useArrayUtils;
import static cn.tocat.bean.utils.StringUtils.toLowercaseIndex;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import cn.tocat.bean.annotations.Autowired;
import cn.tocat.bean.annotations.Bean;
import cn.tocat.bean.annotations.Controller;
import cn.tocat.bean.annotations.Import;
import cn.tocat.bean.annotations.Mapping;
import cn.tocat.bean.annotations.Service;
import cn.tocat.bean.annotations.Value;
import cn.tocat.bean.utils.Assert;
import cn.tocat.bean.utils.ConfigurationUtils;
import cn.tocat.bean.utils.StringUtils;

/**
 * @author: wuchen
 */
public final class ApplicationContext {

	private static final Logger log = Logger.getGlobal();

	protected static ApplicationContext context = new ApplicationContext();

	/**
	 * IOC容器
	 */
	private Map<String, Object> iocBeanMap = new ConcurrentHashMap<String, Object>(32);
	/**
	 * 类集合:存放所有的全类名
	 */
	private Set<String> classSet = new HashSet<String>();

	private ApplicationContext() {
	}

	public Object get(String key) {
		return iocBeanMap.get(key);
	}

	public static ApplicationContext run(Class<?> clasz, String[] args) {
		try {
			context.classLoader();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return context;
	}

	/**
	 * 控制反转
	 */
	private void addServiceToIoc(Class<?> clasz) throws IllegalAccessException, InstantiationException {
		// 预留位置，之后优化
		if (clasz.getAnnotation(Controller.class) != null) {
			iocBeanMap.put(toLowercaseIndex(clasz.getSimpleName()), clasz.newInstance());
			log.info("initial @Controller class " + toLowercaseIndex(clasz.getSimpleName()));
		} else if (clasz.getAnnotation(Service.class) != null) {
			// 将当前类交由IOC管理
			Service myService = (Service) clasz.getAnnotation(Service.class);
			iocBeanMap.put(StringUtils.isEmpty(myService.value()) ? toLowercaseIndex(clasz.getSimpleName())
					: toLowercaseIndex(myService.value()), clasz.newInstance());
			log.info("initial @Service class " + toLowercaseIndex(clasz.getSimpleName()));
		} else if (clasz.getAnnotation(Mapping.class) != null) {
			Mapping myMapping = (Mapping) clasz.getAnnotation(Mapping.class);
			iocBeanMap.put(StringUtils.isEmpty(myMapping.value()) ? toLowercaseIndex(clasz.getSimpleName())
					: toLowercaseIndex(myMapping.value()), clasz.newInstance());
			log.info("initial @Mapping class " + toLowercaseIndex(clasz.getSimpleName()));
		}
		if (clasz.getAnnotation(Import.class) != null) {
			// 将 import 类交IOC 管理
			Import myImport = clasz.getAnnotation(Import.class);
			Class<?>[] importClasses = myImport.value();
			Assert.isNull("import class cannot be null", importClasses);
			for (Class<?> importClass : importClasses) {
				iocBeanMap.put(toLowercaseIndex(importClass.getSimpleName()), importClass.newInstance());
				log.info("initial @Import class " + importClass.getSimpleName());
			}
		}
	}

	/**
	 * 依赖注入
	 * 
	 * @param obj
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	private void injectField(Object obj)
			throws IllegalAccessException, InstantiationException, ClassNotFoundException {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotation(Autowired.class) != null) {
				field.setAccessible(true);
				Autowired myAutowired = field.getAnnotation(Autowired.class);
				Class<?> fieldClass = field.getType();
				// 接口不能被实例化，需要对接口进行特殊处理获取其子类，获取所有实现类
				if (fieldClass.isInterface()) {
					// 如果有指定获取子类名
					if (StringUtils.isNotEmpty(myAutowired.value())) {
						field.set(obj, iocBeanMap.get(myAutowired.value()));
					} else {
						// 当注入接口时，属性的名字与接口实现类名一致则直接从容器中获取
						Object objByName = iocBeanMap.get(field.getName());
						if (objByName != null) {
							field.set(obj, objByName);
							// 递归依赖注入
							injectField(field.getType());
						} else {
							// 注入接口时，如果属性名称与接口实现类名不一致的情况下
							List<Object> list = findSuperInterfaceByIoc(field.getType());
							if (list != null && list.size() > 0) {
								if (list.size() > 1) {
									throw new RuntimeException(
											obj.getClass() + "  注入接口 " + field.getType() + "   失败，请在注解中指定需要注入的具体实现类");
								} else {
									field.set(obj, list.get(0));
									// 递归依赖注入
									injectField(field.getType());
								}
							} else {
								throw new RuntimeException("当前类" + obj.getClass() + "  不能注入接口 "
										+ field.getType().getClass() + "  ， 接口没有实现类不能被实例化");
							}
						}
					}
				} else {
					String beanName = StringUtils.isEmpty(myAutowired.value()) ? toLowercaseIndex(field.getName())
							: toLowercaseIndex(myAutowired.value());
					Object beanObj = iocBeanMap.get(beanName);
					field.set(obj, beanObj == null ? field.getType().newInstance() : beanObj);
					log.info(obj.getClass() + " inject field " + field.getName());
//                递归依赖注入
				}
				injectField(field.getType());
				addBeanToMethod(field.getType());
			}
			if (field.getAnnotation(Value.class) != null) {
				field.setAccessible(true);
				Value value = field.getAnnotation(Value.class);
				Assert.isEmpty(ConfigurationUtils.get(value.value()), value.defaultValue());
				field.set(obj, StringUtils.isNotEmpty(value.value()) ? ConfigurationUtils.get(value.value()) : null);
				log.info("inject configuation " + obj.getClass() + " loading properties " + value.value());
			}
		}
	}

	private void addBeanToMethod(Object obj)
			throws IllegalAccessException, InstantiationException, ClassNotFoundException {
		Method[] methods = obj.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getAnnotation(Bean.class) != null) {
				method.setAccessible(true);
				Bean bean = method.getAnnotation(Bean.class);
				Class<?> fieldClass = method.getReturnType();
				// 接口不能被实例化，需要对接口进行特殊处理获取其子类，获取所有实现类
				iocBeanMap.put(StringUtils.isEmpty(bean.value()) ? toLowercaseIndex(fieldClass.getSimpleName())
						: toLowercaseIndex(bean.value()), fieldClass.newInstance());
				log.info("initial @Bean class " + toLowercaseIndex(fieldClass.getSimpleName()));
			}
		}
	}

	/**
	 * @author: wuchen
	 * @demand: 判断需要注入的接口所有的实现类
	 * @parameters: classz @creationDate： 2019年3月18日 17:12:23
	 */
	private List<Object> findSuperInterfaceByIoc(Class<?> classz) {
		Set<String> beanNameList = iocBeanMap.keySet();
		ArrayList<Object> objectArrayList = new ArrayList<>();
		for (String beanName : beanNameList) {
			Object obj = iocBeanMap.get(beanName);
			Class<?>[] interfaces = obj.getClass().getInterfaces();
			if (useArrayUtils(interfaces, classz)) {
				objectArrayList.add(obj);
			}
		}
		return objectArrayList;
	}

	/**
	 * @author: JiaYao
	 * @demand: 类加载器
	 * @parameters: @creationDate：
	 * @email: huangjy19940202@gmail.com
	 */
	private void classLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		// 加载配置文件所有配置信息
		new ConfigurationUtils(null);
		// 获取扫描包路径
		String classScanPath = (String) ConfigurationUtils.get(Constant.DEFUALT_PROPERTIES_KEY);
		if (StringUtils.isNotEmpty(classScanPath)) {
			classScanPath = classScanPath.replace(".", "/");
		} else {
			throw new RuntimeException("请配置项目包扫描路径 ioc.scan.path");
		}
		// 获取项目中全部的代码文件中带有MyService注解的
		scanClassByFile(classScanPath);
		for (String className : classSet) {
			addServiceToIoc(Class.forName(className));
		}
		// 获取带有Service注解类的所有的带Autowired注解的属性并对其进行实例化
		Set<String> beanKeySet = iocBeanMap.keySet();
		for (String beanName : beanKeySet) {
			injectField(iocBeanMap.get(beanName));
		}
	}

	/**
	 * 从文件中扫描所有的Class
	 * @param packageName
	 */
	private void scanClassByFile(String packageName) {
		URL url = this.getClass().getClassLoader().getResource(packageName);
		File file = new File(url.getFile());
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fileSon : files) {
				if (fileSon.isDirectory()) {
					// 递归扫描
					scanClassByFile(packageName + "/" + fileSon.getName());
				} else {
					// 是文件并且是以 .class结尾
					if (fileSon.getName().endsWith(".class")) {
						log.info("loading class " + packageName.replace("/", ".") + "." + fileSon.getName());
						classSet.add(packageName.replace("/", ".") + "." + fileSon.getName().replace(".class", ""));
					}
				}
			}
		} else {
			throw new RuntimeException("没有找到需要扫描的文件目录");
		}
	}

}