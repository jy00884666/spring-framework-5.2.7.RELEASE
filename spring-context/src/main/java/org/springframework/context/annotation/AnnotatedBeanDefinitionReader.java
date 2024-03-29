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

package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Convenient adapter for programmatic registration of bean classes.
 *
 * <p>This is an alternative to {@link ClassPathBeanDefinitionScanner}, applying
 * the same resolution of annotations but for explicitly registered classes only.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @author Phillip Webb
 * @see AnnotationConfigApplicationContext#register
 * @since 3.0
 */
public class AnnotatedBeanDefinitionReader {
	
	private final BeanDefinitionRegistry registry;
	
	private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
	
	private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
	
	private ConditionEvaluator conditionEvaluator;
	
	/**
	 * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry.
	 * <p>If the registry is {@link EnvironmentCapable}, e.g. is an {@code ApplicationContext},
	 * the {@link Environment} will be inherited, otherwise a new
	 * {@link StandardEnvironment} will be created and used.
	 *
	 * @param registry the {@code BeanFactory} to load bean definitions into,
	 *                 in the form of a {@code BeanDefinitionRegistry}
	 * @see #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment)
	 * @see #setEnvironment(Environment)
	 *
	 * 这里的BeanDefinitionRegistry registry是通过在AnnotationConfigApplicationContext的构造方法中传进来的this
	 * 由此说明AnnotationConfigApplicationContext是一个BeanDefinitionRegistry类型的类
	 * 何以证明我们可以看到AnnotationConfigApplicationContert的类关系:
	 * GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry
	 * 看到他实现了BeanDefinitionRegistryi证明上面的说法,那么BeanDefinitionRegistry的作用是什么呢？
	 * BeanDefinitionRegistry 顾名思义就是BeanDefinition的注册器,那么何为BeDefinitione? 参考Beanion的源码的注解
	 */
	public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
		this(registry, getOrCreateEnvironment(registry));
	}
	
	/**
	 * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry,
	 * using the given {@link Environment}.
	 *
	 * @param registry    the {@code BeanFactory} to load bean definitions into,
	 *                    in the form of a {@code BeanDefinitionRegistry}
	 * @param environment the {@code Environment} to use when evaluating bean definition
	 *                    profiles.
	 * @since 3.1
	 */
	public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		Assert.notNull(environment, "Environment must not be null");
		this.registry = registry;
		this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
		AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
	}
	
	/**
	 * Get the BeanDefinitionRegistry that this reader operates on.
	 */
	public final BeanDefinitionRegistry getRegistry() {
		return this.registry;
	}
	
	/**
	 * Set the {@code Environment} to use when evaluating whether
	 * {@link Conditional @Conditional}-annotated component classes should be registered.
	 * <p>The default is a {@link StandardEnvironment}.
	 *
	 * @see #registerBean(Class, String, Class...)
	 */
	public void setEnvironment(Environment environment) {
		this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
	}
	
	/**
	 * Set the {@code BeanNameGenerator} to use for detected bean classes.
	 * <p>The default is a {@link AnnotationBeanNameGenerator}.
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator =
				(beanNameGenerator != null ? beanNameGenerator : AnnotationBeanNameGenerator.INSTANCE);
	}
	
	/**
	 * Set the {@code ScopeMetadataResolver} to use for registered component classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 */
	public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver =
				(scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
	}
	
	/**
	 * 注册一个或多个要处理的组件类。对{@code register}的调用是幂等的;多次添加相同的组件类不会产生额外的效果。
	 *
	 * @param componentClasses 一个或多个组件类，例如:{@link Configuration pconfiguration}类
	 */
	public void register(Class<?>... componentClasses) {
		for (Class<?> componentClass : componentClasses) {
			registerBean(componentClass);
		}
	}
	
	/**
	 * 从给定的bean类注册一个bean，从类声明的注释派生其元数据
	 *
	 * @param beanClass Bean 的类型
	 */
	public void registerBean(Class<?> beanClass) {
		doRegisterBean(beanClass, null, null, null, null);
	}
	
	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param beanClass the class of the bean
	 * @param name      an explicit name for the bean
	 *                  (or {@code null} for generating a default bean name)
	 * @since 5.2
	 */
	public void registerBean(Class<?> beanClass, @Nullable String name) {
		doRegisterBean(beanClass, name, null, null, null);
	}
	
	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param beanClass  the class of the bean
	 * @param qualifiers specific qualifier annotations to consider,
	 *                   in addition to qualifiers at the bean class level
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(Class<?> beanClass, Class<? extends Annotation>... qualifiers) {
		doRegisterBean(beanClass, null, qualifiers, null, null);
	}
	
	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param beanClass  the class of the bean
	 * @param name       an explicit name for the bean
	 *                   (or {@code null} for generating a default bean name)
	 * @param qualifiers specific qualifier annotations to consider,
	 *                   in addition to qualifiers at the bean class level
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(Class<?> beanClass, @Nullable String name,
							 Class<? extends Annotation>... qualifiers) {
		
		doRegisterBean(beanClass, name, qualifiers, null, null);
	}
	
	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, using the given supplier for obtaining a new
	 * instance (possibly declared as a lambda expression or method reference).
	 *
	 * @param beanClass the class of the bean
	 * @param supplier  a callback for creating an instance of the bean
	 *                  (may be {@code null})
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> beanClass, @Nullable Supplier<T> supplier) {
		doRegisterBean(beanClass, null, null, supplier, null);
	}
	
	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, using the given supplier for obtaining a new
	 * instance (possibly declared as a lambda expression or method reference).
	 *
	 * @param beanClass the class of the bean
	 * @param name      an explicit name for the bean
	 *                  (or {@code null} for generating a default bean name)
	 * @param supplier  a callback for creating an instance of the bean
	 *                  (may be {@code null})
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> beanClass, @Nullable String name, @Nullable Supplier<T> supplier) {
		doRegisterBean(beanClass, name, null, supplier, null);
	}
	
	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param beanClass   the class of the bean
	 * @param name        an explicit name for the bean
	 *                    (or {@code null} for generating a default bean name)
	 * @param supplier    a callback for creating an instance of the bean
	 *                    (may be {@code null})
	 * @param customizers one or more callbacks for customizing the factory's
	 *                    {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
	 * @since 5.2
	 */
	public <T> void registerBean(Class<T> beanClass, @Nullable String name, @Nullable Supplier<T> supplier,
								 BeanDefinitionCustomizer... customizers) {
		
		doRegisterBean(beanClass, name, null, supplier, customizers);
	}
	
	/**
	 * 从给定的bean类注册一个bean，从类声明的注释派生其元数据。
	 *
	 * @param beanClass   the class of the bean
	 * @param name        an explicit name for the bean
	 * @param qualifiers  除了bean类级别上的限定符之外，需要考虑的特定限定符注释(如果有的话)
	 * @param supplier    用于创建bean实例的回调(可以是{@code null})
	 * @param customizers 一个或多个自定义工厂的 @link BeanDefinition}的回调，例如设置一个lazy-init或主标志
	 * @since 5.0
	 */
	private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
									@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
									@Nullable BeanDefinitionCustomizer[] customizers) {
		// Bean的定义,描述
		AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
		// 通过第一步获取到的元数据(metadata)判断是否需要跳过
		if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
			return;
		}
		/*** setInstanceSupplier(supplier)
		 * 替代工厂方法（包含静态工厂）或者构造器创建对象，但是其后面的生命周期回调不影响。
		 * 也就是框架在创建对象的时候会校验这个instanceSupplier是否有值，有的话，调用这个字段获取对象。
		 *
		 * 那么其应用场景什么呢？什么情况下使用呢？
		 *
		 * 静态工厂或者工厂方法或者构造器不足：
		 * 不管是静态工厂还是工厂方法，都需要通过反射调用目标方法创建对象，反射或多或少影响性能，如果不使用反射呢？
		 * 就是面向java8函数式接口编程，就是提供一个回调方法，直接调用回调方法即可，不需要通过反射了。
		 * 源码位置：
		 *
		 * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance
		 *
		 *    Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
		 * 		if (instanceSupplier != null) {
		 * 			return obtainFromSupplier(instanceSupplier, beanName);
		 *      }
		 * */
		abd.setInstanceSupplier(supplier);
		// 得到类的作用域
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
		// 把类的作用域添加到数据结构结构中,@Scope用于指定scope作用域的（用在类上）
		abd.setScope(scopeMetadata.getScopeName());
		// 生成类的名字通过beanNameGenerator
		String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
		// 预处理公共注解 lazy Primary DependsOn 等value的值设置
		AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
		/*如果在向容器注册注解Bean定义时,使用了额外的限定符注解则解析关于@Qualifier和@Primary这两个注解,
		 *主要涉及到spring的自动装配这里需要注意的byName和qualifiers这个变量是Annotation类型的数组,
		 *里面存不仅仅是Qualifier注解理论上里面里面存的是一切注解,
		 *所以可以看到下面的代码spring去循环了这个数组然后依次判断了注解当中是否含了Primary,是否包含了Lazyd*/
		if (qualifiers != null) {
			for (Class<? extends Annotation> qualifier : qualifiers) {
				/** 是否是@Primary：自动装配时当出现多个Bean候选者时，被注解为@Primary的Bean将作为首选者，
				 * 否则将抛出异常。（只对接口的多个实现生效）*/
				if (Primary.class == qualifier) {
					abd.setPrimary(true);
				} else if (Lazy.class == qualifier) {
					// @Lazy(true) 表示延迟初始化
					abd.setLazyInit(true);
				} else {
					/** 名称装配 @Autowired 默认按类型装配，如果我们想使用按名称装配，可以结合@Qualifier注解一起使用
					 * @Autowired @Qualifier(“personDaoBean”) 存在多个实例配合使用
					 * */
					abd.addQualifier(new AutowireCandidateQualifier(qualifier));
				}
			}
		}
		if (customizers != null) {
			for (BeanDefinitionCustomizer customizer : customizers) {
				customizer.customize(abd);
			}
		}
		// BeanDefinitionHolder 也是一个数据结构
		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
		// ScopedProxyMode 这个知识点比较复杂,需要结合web去理解
		definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
		// 把上述的这个数据结构注册给registry,registry就是AnnotatonConfigApplicationContext
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
	}
	
	/**
	 * Get the Environment from the given registry if possible, otherwise return a new
	 * StandardEnvironment.
	 */
	private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		if (registry instanceof EnvironmentCapable) {
			return ((EnvironmentCapable) registry).getEnvironment();
		}
		return new StandardEnvironment();
	}
	
}
