/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * spring的扩展点之一实现该接口，可以在spring的bean创建之前修改bean的定义属性。
 * spring允许BeanFactoryPostProcessor在容器实例化任何其它bean之前读取配置元数据,并可以根据需要进行修改,
 * 例如可以把bean的scope从singleton改为prototype,也可以把property的值给修改掉。
 * 可以同时配置多个BeanFactoryPostProcessor,并通过设置'order'属性来控制各个BeanFactoryPostProcessor的执行次序。
 * BeanFactoryPostProcessor是在spring容器加载了bean的定义文件之后,在bean实例化之前执行的
 * 可以写一个例子来测试一下这个功能
 *
 * Factory hook that allows for custom modification of an application context's
 * bean definitions, adapting the bean property values of the context's underlying
 * bean factory.
 *
 * <p>Useful for custom config files targeted at system administrators that
 * override bean properties configured in the application context. See
 * {@link PropertyResourceConfigurer} and its concrete implementations for
 * out-of-the-box solutions that address such configuration needs.
 *
 * <p>A {@code BeanFactoryPostProcessor} may interact with and modify bean
 * definitions, but never bean instances. Doing so may cause premature bean
 * instantiation, violating the container and causing unintended side-effects.
 * If bean instance interaction is required, consider implementing
 * {@link BeanPostProcessor} instead.
 *
 * <h3>Registration</h3>
 * <p>An {@code ApplicationContext} auto-detects {@code BeanFactoryPostProcessor}
 * beans in its bean definitions and applies them before any other beans get created.
 * A {@code BeanFactoryPostProcessor} may also be registered programmatically
 * with a {@code ConfigurableApplicationContext}.
 *
 * <h3>Ordering</h3>
 * <p>{@code BeanFactoryPostProcessor} beans that are autodetected in an
 * {@code ApplicationContext} will be ordered according to
 * {@link org.springframework.core.PriorityOrdered} and
 * {@link org.springframework.core.Ordered} semantics. In contrast,
 * {@code BeanFactoryPostProcessor} beans that are registered programmatically
 * with a {@code ConfigurableApplicationContext} will be applied in the order of
 * registration; any ordering semantics expressed through implementing the
 * {@code PriorityOrdered} or {@code Ordered} interface will be ignored for
 * programmatically registered post-processors. Furthermore, the
 * {@link org.springframework.core.annotation.Order @Order} annotation is not
 * taken into account for {@code BeanFactoryPostProcessor} beans.
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @see BeanPostProcessor
 * @see PropertyResourceConfigurer
 * @since 06.07.2003
 */
/* @FunctionalInterface是一个信息性注解类型，用于指示接口类型符合 Java 语言规范定义的函数式接口要求。
 * 从概念上讲，函数式接口只有一个抽象方法，其他方法都有默认的实现。
 * 如果用来修饰类或枚举会编译失败,如果接口声明了一个覆盖 java.lang.Object 的公共方法之一的抽象方法，这也不会进入抽象方法计数，
 * 因为接口的任何实现都具有来自 java.lang.Object 或其他地方的实现。
 * 请注意，函数式接口的实例可以使用 lambda 表达式、方法引用或构造函数引用来创建。
 * */
@FunctionalInterface
public interface BeanFactoryPostProcessor {
	
	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for overriding or adding
	 * properties even to eager-initializing beans.
	 * @param beanFactory the bean factory used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
	
}
