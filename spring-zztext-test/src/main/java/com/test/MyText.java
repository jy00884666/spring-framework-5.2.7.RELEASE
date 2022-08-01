package com.test;

import com.test.bean.Person;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Ctrl + Alt + Shift + u 查看类图结构
 */
public class MyText {
	
	public static void main(String[] args) {
		// 创建Spring容器
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Person.class);
		// 获取对象Bean
		Person person = (Person) context.getBean("person");
		System.out.println("person:" + person);
	}
	
}
