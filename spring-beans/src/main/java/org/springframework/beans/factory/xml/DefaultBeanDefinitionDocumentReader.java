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

package org.springframework.beans.factory.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link BeanDefinitionDocumentReader} interface that
 * reads bean definitions according to the "spring-beans" DTD and XSD format
 * (Spring's default XML bean definition format).
 *
 * <p>The structure, elements, and attribute names of the required XML document
 * are hard-coded in this class. (Of course a transform could be run if necessary
 * to produce this format). {@code <beans>} does not need to be the root
 * element of the XML document: this class will parse all bean definition elements
 * in the XML file, regardless of the actual root element.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Erik Wiersma
 * @since 18.12.2003
 */
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {
	
	public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;
	
	public static final String NESTED_BEANS_ELEMENT = "beans";
	
	public static final String ALIAS_ELEMENT = "alias";
	
	public static final String NAME_ATTRIBUTE = "name";
	
	public static final String ALIAS_ATTRIBUTE = "alias";
	
	public static final String IMPORT_ELEMENT = "import";
	
	public static final String RESOURCE_ATTRIBUTE = "resource";
	
	public static final String PROFILE_ATTRIBUTE = "profile";
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Nullable
	private XmlReaderContext readerContext;
	
	@Nullable
	private BeanDefinitionParserDelegate delegate;
	
	/**
	 * This implementation parses bean definitions according to the "spring-beans" XSD
	 * (or DTD, historically).
	 * <p>Opens a DOM Document; then initializes the default settings
	 * specified at the {@code <beans/>} level; then parses the contained bean definitions.
	 *
	 * 根据Spring DTD对Bean的定义规则解析Bean定义Document对象
	 */
	@Override
	public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
		// 获得XML描述符
		this.readerContext = readerContext;
		// getDocumentElement()获得Document的根元素
		doRegisterBeanDefinitions(doc.getDocumentElement());
	}
	
	/**
	 * Return the descriptor for the XML resource that this parser works on.
	 */
	protected final XmlReaderContext getReaderContext() {
		Assert.state(this.readerContext != null, "No XmlReaderContext available");
		return this.readerContext;
	}
	
	/**
	 * Invoke the {@link org.springframework.beans.factory.parsing.SourceExtractor}
	 * to pull the source metadata from the supplied {@link Element}.
	 */
	@Nullable
	protected Object extractSource(Element ele) {
		return getReaderContext().extractSource(ele);
	}
	
	/**
	 * Register each bean definition within the given root {@code <beans/>} element.
	 */
	@SuppressWarnings("deprecation")  // for Environment.acceptsProfiles(String...)
	protected void doRegisterBeanDefinitions(Element root) {
		// Any nested <beans> elements will cause recursion in this method. In
		// order to propagate and preserve <beans> default-* attributes correctly,
		// keep track of the current (parent) delegate, which may be null. Create
		// the new (child) delegate with a reference to the parent for fallback purposes,
		// then ultimately reset this.delegate back to its original (parent) reference.
		// this behavior emulates a stack of delegates without actually necessitating one.
		/*任何嵌套的<beans>元素将在此方法中导致递归。在为了正确传播和保存<beans> default-*属性，
		跟踪当前(父)委托，它可能是nulL。创建这个新的(子)委托带有一个用于回退的父级引用，
		然后最终将this.delegate重置为它最初的(父级)引用。此行为模拟了一堆委托，但实际上并不需要委托。*/
		
		/*具体的解析过程由BeanDefinitionParserDelegate实现,
		 *BeanDefinitionParserDelegate中定义了Spring Bean定义XML文件的各种元素*/
		BeanDefinitionParserDelegate parent = this.delegate;
		// 创建BeanDefinitionParserDelegate, 用于完成真正的解析过程
		this.delegate = createDelegate(getReaderContext(), root, parent);
		// 如果是默认的，获取profile属性，此处资料上可以配置开发、生产或者测试环境
		if (this.delegate.isDefaultNamespace(root)) {
			String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
			if (StringUtils.hasText(profileSpec)) {
				String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
						profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
				// We cannot use Profiles.of(...) since profile expressions are not supported
				// 我们不能使用 Profiles.of(…)，因为不支持配置文件表达式
				// in XML config. See SPR-12458 for details. 在XML配置。详见sp -12458。
				if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec +
								"] not matching: " + getReaderContext().getResource());
					}
					return;
				}
			}
		}
		// 在解析Bean定义之前,进行自定义的解析,增强解析过程的可扩展性
		preProcessXml(root);
		// 从Document的根元素开始进行Bean定义的Document对象
		parseBeanDefinitions(root, this.delegate);
		// 在解析Bean定义之后,进行自定义的解析,增加解析过程的可扩展性
		postProcessXml(root);
		
		this.delegate = parent;
	}
	
	/**
	 * 创建BeanDefinitionParserDelegate, 用于完成真正的解析过程
	 */
	protected BeanDefinitionParserDelegate createDelegate(
			XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {
		// 创建BeanDefinitionParserDelegate, 用于完成真正的解析过程
		BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
		// BeanDefinitionParserDelegate 初始化 Document 根元素
		delegate.initDefaults(root, parentDelegate);
		return delegate;
	}
	
	/**
	 * Parse the elements at the root level in the document:
	 * "import", "alias", "bean".
	 *
	 * 使用Spring的Bean规则从Document的根元素开始进行Bean定义的Document对象
	 *
	 * @param root the DOM root element of the document
	 */
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		// Bean定义的Document对象使用了Spring默认的XML命名空间
		if (delegate.isDefaultNamespace(root)) {
			// 获取Bean定义的Document对象根元素的所有子节点
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				// 获得Document节点是XML元素节点
				if (node instanceof Element) {
					Element ele = (Element) node;
					// Bean定义的Document的元素节点使用的是Spring默认的XML命名空间
					if (delegate.isDefaultNamespace(ele)) {
						// 使用Spring的Bean规则解析元素节点
						parseDefaultElement(ele, delegate);
					} else {
						// 没有使用Spring默认的XML命名空间,则使用用户自定义的解析规则解折元素节点
						delegate.parseCustomElement(ele);
					}
				}
			}
		} else {
			// Document的根节点没有使用Spring默认的命名空间,则使用用户自定义的解析规则解析Document根节点
			delegate.parseCustomElement(root);
		}
	}
	
	/*使用Spring的Bean规则解析元素节点*/
	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		// 如果元素节点是<Import>导入元素,进行导入解析
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
			importBeanDefinitionResource(ele);
		}
		// 如果元素节点是<Alias>别名元素,进行别名解析
		else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
			processAliasRegistration(ele);
		}
		// 如果元素节点是<Bean>元素,按照Spirng 的bean规则解析
		else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
			processBeanDefinition(ele, delegate);
		}
		// 如果元素节点是<beans>元素解析
		else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
			// recurse 递归
			doRegisterBeanDefinitions(ele);
		}
	}
	
	/**
	 * Parse an "import" element and load the bean definitions
	 * from the given resource into the bean factory.
	 *
	 * 解析一个“import”元素并将给定资源中的bean定义加载到bean工厂中。
	 */
	protected void importBeanDefinitionResource(Element ele) {
		// 获取给定的导入元素的location属性
		String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
		// 如果导入元素的lochtion属性值为空,则没有导入任何资源,直接返回
		if (!StringUtils.hasText(location)) {
			getReaderContext().error("Resource location must not be empty", ele);
			return;
		}
		
		// Resolve system properties: e.g. "${user.dir}"
		// 使用系统变量值解析location属性值
		location = getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);
		
		Set<Resource> actualResources = new LinkedHashSet<>(4);
		
		// Discover whether the location is an absolute or relative URI
		// 标识给定的导入元素的location是否是绝对路径
		boolean absoluteLocation = false;
		try {
			absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
		} catch (URISyntaxException ex) {
			// cannot convert to an URI, considering the location relative
			// unless it is the well-known Spring prefix "classpath*:"
			// 不能转换为URI，考虑到相对位置，除非它是众所周知的Spring前缀“classpath*:”
		}
		
		// Absolute or relative?
		// 是绝对路径
		if (absoluteLocation) {
			try {
				// 使用资源读入器加载给定路径的Bean定义资源
				int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
				if (logger.isTraceEnabled()) {
					logger.trace("Imported " + importCount + " bean definitions from URL location [" + location + "]");
				}
			} catch (BeanDefinitionStoreException ex) {
				getReaderContext().error(
						"Failed to import bean definitions from URL location [" + location + "]", ele, ex);
			}
		} else {
			// No URL -> considering resource location as relative to the current file.
			// 没有URL ->考虑资源位置相对于当前文件。
			// 给定的导入元素的location不是绝对路径
			try {
				int importCount;
				// 将给定导入元素的location封装为相对路径资源
				Resource relativeResource = getReaderContext().getResource().createRelative(location);
				// 封装的相对路径资源存在
				if (relativeResource.exists()) {
					// 使用资源读入器加载Bean定义资源
					importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
					actualResources.add(relativeResource);
				}
				// 封装的相对路径资源不存在
				else {
					// 获取Spring IOC容器资源读入器的基本路径
					String baseLocation = getReaderContext().getResource().getURL().toString();
					// 根据Spring IoC容器资源读入器的基本路径加载给定导入路径的资源
					importCount = getReaderContext().getReader().loadBeanDefinitions(
							StringUtils.applyRelativePath(baseLocation, location), actualResources);
				}
				if (logger.isTraceEnabled()) {
					logger.trace(
							"Imported " + importCount + " bean definitions from relative location [" + location + "]");
				}
			} catch (IOException ex) {
				getReaderContext().error("Failed to resolve current resource location", ele, ex);
			} catch (BeanDefinitionStoreException ex) {
				getReaderContext().error(
						"Failed to import bean definitions from relative location [" + location + "]", ele, ex);
			}
		}
		Resource[] actResArray = actualResources.toArray(new Resource[0]);
		// 在解析完<Import>元素之后,发送容器导入其他资源处理完成事件
		getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
	}
	
	/**
	 * Process the given alias element, registering the alias with the registry.
	 *
	 * 处理给定的alias元素，向注册中心注册别名。
	 */
	protected void processAliasRegistration(Element ele) {
		// 获取<Alias>别名元素中name的属性值
		String name = ele.getAttribute(NAME_ATTRIBUTE);
		// 获取<Alias>别名元素中alias的属性值
		String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
		boolean valid = true;
		// <alias>别名元素的 name 属性值为空
		if (!StringUtils.hasText(name)) {
			getReaderContext().error("Name must not be empty", ele);
			valid = false;
		}
		// <alias>别名元素的 alias 属性值为空
		if (!StringUtils.hasText(alias)) {
			getReaderContext().error("Alias must not be empty", ele);
			valid = false;
		}
		if (valid) {
			try {
				// 向容器的资源读入器注册别名
				getReaderContext().getRegistry().registerAlias(name, alias);
			} catch (Exception ex) {
				getReaderContext().error("Failed to register alias '" + alias +
						"' for bean with name '" + name + "'", ele, ex);
			}
			// 在解析完<Alias>元素之后,发送容器别名处理完成事件
			getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
		}
	}
	
	/**
	 * Process the given bean element, parsing the bean definition
	 * and registering it with the registry.
	 * 处理给定的bean元素，解析bean定义并将其注册到注册中心。
	 *
	 * 解析Bean定义资源Document对象的普通元素
	 */
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
		// 解析Bean定义资源文件中的<Bean>元素,这个方法中主要处理<Bean>元素的id, name和别名属性
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		/*BeanDefinitionHolder 是对 BeanDefinition 的封装,即Bean定义的封装类
		 *对Document对象中<Bean>元素的解析由 BeanDefinitionParserDelegate 实现
		 *BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);*/
		if (bdHolder != null) {
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance.
				// 向Spring IOC容器注册解析得到的Bean定义,这是Bean定义向IOC容器注册的入口
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			} catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.
			// 在完成向Spring IOC容器注册解析得到的Bean定义之后,发送注册事件
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}
	
	/**
	 * Allow the XML to be extensible by processing any custom element types first,
	 * before we start to process the bean definitions. This method is a natural
	 * extension point for any other custom pre-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 *
	 * @see #getReaderContext()
	 */
	protected void preProcessXml(Element root) {
	}
	
	/**
	 * Allow the XML to be extensible by processing any custom element types last,
	 * after we finished processing the bean definitions. This method is a natural
	 * extension point for any other custom post-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 *
	 * @see #getReaderContext()
	 */
	protected void postProcessXml(Element root) {
	}
	
}
