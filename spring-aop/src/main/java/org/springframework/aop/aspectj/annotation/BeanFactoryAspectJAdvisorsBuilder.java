/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.aop.aspectj.annotation;

import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper for retrieving @AspectJ beans from a BeanFactory and building
 * Spring Advisors based on them, for use with auto-proxying.
 *
 * @author Juergen Hoeller
 * @see AnnotationAwareAspectJAutoProxyCreator
 * @since 2.0.2
 */
public class BeanFactoryAspectJAdvisorsBuilder {

    private final ListableBeanFactory beanFactory;

    private final AspectJAdvisorFactory advisorFactory;

    @Nullable
    private volatile List<String> aspectBeanNames;

    // 单例 Bean 切面缓存 key为@AspectJ 修饰的类名小写，value为切面方法 Advisor
    private final Map<String, List<Advisor>>                      advisorsCache      = new ConcurrentHashMap<>();
    // 非单例 Bean 缓存 key为@AspectJ 修饰的类名小写，value为非单例切面的工厂
    private final Map<String, MetadataAwareAspectInstanceFactory> aspectFactoryCache = new ConcurrentHashMap<>();


    /**
     * Create a new BeanFactoryAspectJAdvisorsBuilder for the given BeanFactory.
     *
     * @param beanFactory the ListableBeanFactory to scan
     */
    public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory) {
        this(beanFactory, new ReflectiveAspectJAdvisorFactory(beanFactory));
    }

    /**
     * Create a new BeanFactoryAspectJAdvisorsBuilder for the given BeanFactory.
     *
     * @param beanFactory    the ListableBeanFactory to scan
     * @param advisorFactory the AspectJAdvisorFactory to build each Advisor with
     */
    public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {
        Assert.notNull(beanFactory, "ListableBeanFactory must not be null");
        Assert.notNull(advisorFactory, "AspectJAdvisorFactory must not be null");
        this.beanFactory    = beanFactory;
        this.advisorFactory = advisorFactory;
    }


    /**
     * Look for AspectJ-annotated aspect beans in the current bean factory,
     * and return to a list of Spring AOP Advisors representing them.
     * <p>Creates a Spring Advisor for each AspectJ advice method.
     *
     * @return the list of {@link org.springframework.aop.Advisor} beans
     * @see #isEligibleBean
     */
    /**
     * 解析应用定义的切面相关注解（@Aspect @Before）
     * 生成Advisor集合
     * @return
     */
    public List<Advisor> buildAspectJAdvisors() {
        List<String> aspectNames = this.aspectBeanNames; // aspectNames=[LoggingAspect]
        if (aspectNames == null) {
            synchronized (this) { // double-check
                aspectNames = this.aspectBeanNames;
                if (aspectNames == null) {
                    List<Advisor> advisors = new ArrayList<>();
                    aspectNames = new ArrayList<>();
                    String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory, Object.class, true, false); // 获取所有 Bean 名称
                    for (String beanName : beanNames) {
                        if (!isEligibleBean(beanName)) { //  // 2. 过滤非切面 Bean（通过 isEligibleBean 和 @Aspect 注解检查）
                            continue;
                        }
                        // We must be careful not to instantiate beans eagerly as in this case they
                        // would be cached by the Spring container but would not have been weaved.
                        Class<?> beanType = this.beanFactory.getType(beanName, false);
                        if (beanType == null) {
                            continue;
                        }
                        if (this.advisorFactory.isAspect(beanType)) { // 判断是否是切面 @Aspect注解
                            aspectNames.add(beanName);
                            AspectMetadata amd = new AspectMetadata(beanType, beanName); // 3. 解析切面元数据
                            if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
                                MetadataAwareAspectInstanceFactory factory = new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
                                // 这里解析定义的切面类，识别@Pointcut @Before @After 等注解
                                List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory); // 获取方法的Advisor
                                //4.根据作用域创建 Advisor 缓存
                                if (this.beanFactory.isSingleton(beanName)) {
                                    this.advisorsCache.put(beanName, classAdvisors); // Bean是单例模式，直接缓存Advisor
                                } else {
                                    this.aspectFactoryCache.put(beanName, factory); // Bean是原型模式，缓存工厂对象，动态创建 Advisors
                                }
                                advisors.addAll(classAdvisors);
                            } else { // 处理非单例作用域切面
                                // Per target or per this.
                                if (this.beanFactory.isSingleton(beanName)) {
                                    throw new IllegalArgumentException("Bean with name '" + beanName + "' is a singleton, but aspect instantiation model is not singleton");
                                }
                                MetadataAwareAspectInstanceFactory factory = new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
                                this.aspectFactoryCache.put(beanName, factory);
                                advisors.addAll(this.advisorFactory.getAdvisors(factory));
                            }
                        }
                    }
                    this.aspectBeanNames = aspectNames;
                    return advisors;
                }
            }
        }

        if (aspectNames.isEmpty()) {
            return Collections.emptyList();
        }
        // 从缓存中获取Advisor
        List<Advisor> advisors = new ArrayList<>(); // [beforeAdvice,afterAdvice]
        for (String aspectName : aspectNames) {
            List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
            if (cachedAdvisors != null) {
                advisors.addAll(cachedAdvisors);
            } else {
                MetadataAwareAspectInstanceFactory factory = this.aspectFactoryCache.get(aspectName);
                advisors.addAll(this.advisorFactory.getAdvisors(factory));
            }
        }
        return advisors;
    }

    /**
     * Return whether the aspect bean with the given name is eligible.
     * 返回给定名称的bean是否符合条件。
     *
     * @param beanName the name of the aspect bean
     * @return whether the bean is eligible
     */
    protected boolean isEligibleBean(String beanName) {
        return true;
    }

}