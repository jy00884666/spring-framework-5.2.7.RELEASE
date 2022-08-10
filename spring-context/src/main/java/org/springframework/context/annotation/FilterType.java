/*
 * Copyright 2002-2013 the original author or authors.
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

/**
 * Enumeration of the type filters that may be used in conjunction with
 * 一起使用的类型筛选器的枚举
 * {@link ComponentScan @ComponentScan}.
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @see ComponentScan
 * @see ComponentScan#includeFilters()
 * @see ComponentScan#excludeFilters()
 * @see org.springframework.core.type.filter.TypeFilter
 * @since 2.5
 */
public enum FilterType {
	
	/**
	 * Filter candidates marked with a given annotation.
	 * 注解形式 比如 @Controller @Service @Repository @Compent
	 * excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = @Controller)}
	 * @see org.springframework.core.type.filter.AnnotationTypeFilter
	 */
	ANNOTATION,
	
	/**
	 * Filter candidates assignable to a given type.
	 * 指定的类型 如:UserService.class
	 * @see org.springframework.core.type.filter.AssignableTypeFilter
	 */
	ASSIGNABLE_TYPE,
	
	/**
	 * Filter candidates matching a given AspectJ type pattern expression.
	 * aspectJ形式的 不常用
	 * @see org.springframework.core.type.filter.AspectJTypeFilter
	 */
	ASPECTJ,
	
	/**
	 * Filter candidates matching a given regex pattern.
	 * 正则表达式
	 * @see org.springframework.core.type.filter.RegexPatternTypeFilter
	 */
	REGEX,
	
	/**
	 * Filter candidates using a given custom
	 * 自定义形式:实现 TypeFiler接口实现match方法,写筛选逻辑
	 * {@link org.springframework.core.type.filter.TypeFilter} implementation.
	 */
	CUSTOM
	
	
	/* FilterType.CUSTOM 自定义类型如何使用
	 public class CustomFilterType implements TypeFilter {
		
		@Override
		public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws
		IOException {
		 
			//获取当前类的注解源信息
			AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
			
			//获取当前类的class的源信息
			ClassMetadata classMetadata = metadataReader.getClassMetadata();
			
			//获取当前类的资源信息
			Resource resource = metadataReader.getResource();
			
			if(classMetadata.getClassName().contains("dao")) {
				return true;
			}
				return false;
			}
		}
		
		@ComponentScan(basePackages = {"com.leon.testcompentscan"},includeFilters = {
		@ComponentScan.Filter(type = FilterType.CUSTOM,value = CustomFilterType.class)
		},useDefaultFilters = false)
		public class MainConfig {
		
		}
	 */
}
