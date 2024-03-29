/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core;

import org.springframework.lang.Nullable;

/**
 * Interface defining a generic contract for attaching and accessing metadata
 * to/from arbitrary objects.
 *
 * 接口定义了最基本的对任意对象的元数据的修改或者获取，主要方法有
 *
 * @author Rob Harrop
 * @since 2.0
 */
public interface AttributeAccessor {
	
	/**
	 * Set the attribute defined by {@code name} to the supplied {@code value}.
	 * If {@code value} is {@code null}, the attribute is {@link #removeAttribute removed}.
	 * <p>In general, users should take care to prevent overlaps with other
	 * metadata attributes by using fully-qualified names, perhaps using
	 * class or package names as prefix.
	 *
	 * //将name定义的属性设置为提供的value值。如果value的值为null，则该属性为{@link #removeAttribute removed}。
	 * //通常，用户应该注意通过使用完全限定的名称（可能使用类或包名称作为前缀）来防止与其他元数据属性重叠。
	 *
	 * @param name  the unique attribute key
	 * @param value the attribute value to be attached
	 */
	void setAttribute(String name, @Nullable Object value);
	
	/**
	 * Get the value of the attribute identified by {@code name}.
	 * Return {@code null} if the attribute doesn't exist.
	 *
	 * //获取标识为name的属性的值。
	 *
	 * @param name the unique attribute key
	 * @return the current value of the attribute, if any
	 */
	@Nullable
	Object getAttribute(String name);
	
	/**
	 * Remove the attribute identified by {@code name} and return its value.
	 * Return {@code null} if no attribute under {@code name} is found.
	 *
	 * //删除标识为name的属性，并返回属性值
	 *
	 * @param name the unique attribute key
	 * @return the last value of the attribute, if any
	 */
	@Nullable
	Object removeAttribute(String name);
	
	/**
	 * Return {@code true} if the attribute identified by {@code name} exists.
	 * Otherwise return {@code false}.
	 *
	 * //如果名为name的属性是否存在，存在返回true，否则返回false。
	 *
	 * @param name the unique attribute key
	 */
	boolean hasAttribute(String name);
	
	/**
	 * Return the names of all attributes.
	 *
	 * //返回所有属性的名称。
	 */
	String[] attributeNames();
	
}
