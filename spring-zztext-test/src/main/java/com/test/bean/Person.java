package com.test.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * ApplicationContextAware后置处理器接口
 */
public class Person implements ApplicationContextAware {
	
	private String name;
	
	private String sex;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSex() {
		return sex;
	}
	
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", sex='" + sex + '\'' +
				'}';
	}
	
	/*后置处理器*/
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// 可以拿到IOC容器
		Person p = (Person) applicationContext.getBean("person");
		System.out.println("后置处理器前:" + p);
		p.setName("aaa");
		p.setSex("111");
		System.out.println("后置处理器后:" + p);
	}
}
