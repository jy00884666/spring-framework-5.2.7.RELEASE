package com.test;

import com.test.bean.ConfigBean;
import com.test.bean.Person;
import com.test.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/*Ctrl + Alt + Shift + u 查看类图结构*/
public class MyText {
	
	public static void main(String[] args) {
		// 打印默认编码
		System.out.println("打印默认编码:" + System.getProperty("file.encoding"));
		
		// 创建Spring容器
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigBean.class);
		// 获取对象Bean
		UserService userService = (UserService) context.getBean("userService");
		System.out.println("userService:" + userService.addUser("张三李四", ""));
	}
	
}
