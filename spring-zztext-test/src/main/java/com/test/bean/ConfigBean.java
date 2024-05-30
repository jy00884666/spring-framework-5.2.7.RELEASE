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
/*启用加载Bean到Spring容器中,不写参数表示全局扫描,如需扫描不同路径,basePackages = {"com.springtest", "com.springboottest"}
* @ComponentScan("com.test.**")与@ComponentScan("com.test")效果一样*/
@ComponentScan("com.test.**")
/*包扫描的时候就会扫描到 LogAnnotationAspect.java 这里不需要再引入*/
//@Import(value = LogAnnotationAspect.class)
public class ConfigBean {
	
	/*引用第三方对象,通常使用这种方式new出来*/
	@Bean
	public Person person() {
		Person person = new Person();
		person.setName("a1");
		person.setSex("18");
		return person;
	}
	
	/**
	 * Spring启动编译不想看到警告,在@ComponentScan配置类中随便加入了一个方法即可
	 * 警告: 没有处理程序要使用以下任何注释: org.aspectj.lang.annotation.AfterThrowing...
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("测试配置类启动");
	}
}
