package com.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	// 定义一个addUser()方法，模拟应用中的添加用户的方法
	@Override
	public int addUser(String name, String pass) {
		logger.info("执行Hello组件的addUser添加用户：" + name);
		if (name.length() < 3 || name.length() > 10) {
			throw new IllegalArgumentException("name参数的长度必须大于3，小于10！");
		}
		return 20;
	}
}
