package com.test;

import com.test.bean.ConfigBean;
import com.test.bean.Person;
import com.test.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
		
		
		/****************************面试**************************************/
		/*验证(),[],{}是否成对*/
		//System.out.println(new Test().isValid("[asdf],{adf},(fafqwer)"));
	}
	
	/*验证(),[],{}是否成对*/
	public boolean isValid(String s) {
		/*所有的左括号放在一个集合中*/
		Map<Character, Character> PAREN_DICT_MAP = new HashMap<>();
		PAREN_DICT_MAP.put(')', '(');
		PAREN_DICT_MAP.put(']', '[');
		PAREN_DICT_MAP.put('}', '{');
		
		
		/*Stack是Vector的一个子类，它实现标准的后进先出堆栈。Stack只定义了创建空堆栈的默认构造方法*/
		/*Character类在对象中包装一个基本类型char的值，此外，该类提供了几种方法，以确定字符的类别(小写字母，数字，等等)，并将字符从大写转换成小写。反之则亦然*/
		Stack<Character> stack = new Stack<Character>();
		for (char c : s.toCharArray()) {
			/*push(Object element)方法是把元素压入栈*/
			if (PAREN_DICT_MAP.containsValue(c)) {
				// 如果是左括号，直接入栈
				stack.push(c);
				continue;
			}
			//如果是右括号，则弹出栈顶的左括号进行比较，看是否是一对
			if (PAREN_DICT_MAP.containsKey(c)) {
				/*pop()方法是移除堆栈顶部的对象，并作为此函数的值返回该对象*/
				if (stack.isEmpty() || !stack.pop().equals(PAREN_DICT_MAP.get(c))) {
					return false;
				}
			}
			System.out.println("堆栈中的元素有：" + stack);
		}
		// 返回栈顶端的元素，但不从堆栈中移除它
        /*Character topE = stack.peek();
        System.out.println("返回堆栈中的栈顶元素为 : " + topE);*/
		/* isEmpty()方法是判断堆栈是否为空。*/
		// 如果最后栈不为空，说明表达式也不正确
		return stack.isEmpty();
	}
}
