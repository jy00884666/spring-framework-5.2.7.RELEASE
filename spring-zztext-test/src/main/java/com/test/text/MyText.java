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
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigBean.class);
		Person person = (Person) context.getBean("person");
		logger.info("Person:" + person);
		
		UserService userService = (UserService) context.getBean("userService");
		logger.info(userService.addUser("aaa", "111") + "");
		
	}
	
}
