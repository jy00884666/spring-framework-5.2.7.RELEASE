package com.test.text;

import com.test.bean.ConfigBean;
import com.test.bean.Person;
import com.test.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Ctrl + Alt + Shift + u 查看类图结构
 */
public class MyText {
	
	private final static Logger logger = LoggerFactory.getLogger(MyText.class);
	
	public static void main(String[] args) {
		/*初始化 spring context*/
		/*实例化 bean*/
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigBean.class);
		/**
		 * 初始化流程
		 * 1. scan 扫描
		 * 2. parse 解析,因为有些类不能直接new,例如(懒加载,抽象类)
		 * 3. cache 解析信息缓存起来 map.put
		 * 3.1 若程序员提供了实现BeanFactoryPostProcessor接口的实现类,则会执行程序员提供的postProcessBeanFactory(...)方法
		 * 		而不去执行第四步实例化对象
		 * 4. new 实例化对象
		 */
		
		
		Person person = (Person) context.getBean("person");
		logger.info("Person:" + person);
		
		UserService userService = (UserService) context.getBean("userService");
		logger.info(userService.addUser("aaa", "111") + "");
		
	}
	
}
