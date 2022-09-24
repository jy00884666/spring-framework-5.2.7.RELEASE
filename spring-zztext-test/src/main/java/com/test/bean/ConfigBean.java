package com.test.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/*申明配置类
	属性(proxyBeanMethods = false)默认等于true表示需要是否需要把配置的Bean放入Spirng容器中*/
@Configuration
/*启用AspectJ注解自动配置,proxyTargetClass用于指定是否强制使用cglib代理*/
@EnableAspectJAutoProxy
/*启用加载Bean到Spring容器中,不写参数表示全局扫描,如需扫描不同路径,basePackages = {"com.springtest", "com.springboottest"}*/
@ComponentScan("com.test")
/*包扫描的时候就会扫描到 LogAnnotationAspect.java 这里不需要再引入*/
//@Import(value = LogAnnotationAspect.class)
public class ConfigBean {
	
	/*初始化*/
	@Bean
	public Person person() {
		Person person = new Person();
		person.setName("a1");
		person.setSex("18");
		return person;
	}
}
