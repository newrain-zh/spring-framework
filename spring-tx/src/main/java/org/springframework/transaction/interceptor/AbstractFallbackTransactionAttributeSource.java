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

package org.springframework.transaction.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract implementation of {@link TransactionAttributeSource} that caches
 * attributes for methods and implements a fallback policy: 1. specific target
 * method; 2. target class; 3. declaring method; 4. declaring class/interface.
 *
 * <p>Defaults to using the target class's transaction attribute if none is
 * associated with the target method. Any transaction attribute associated with
 * the target method completely overrides a class transaction attribute.
 * If none found on the target class, the interface that the invoked method
 * has been called through (in case of a JDK proxy) will be checked.
 *
 * <p>This implementation caches attributes by method after they are first used.
 * If it is ever desirable to allow dynamic changing of transaction attributes
 * (which is very unlikely), caching could be made configurable. Caching is
 * desirable because of the cost of evaluating rollback rules.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 */
public abstract class AbstractFallbackTransactionAttributeSource implements TransactionAttributeSource {

    /**
     * Canonical value held in cache to indicate no transaction attribute was
     * found for this method, and we don't need to look again.
     */
    @SuppressWarnings("serial")
    private static final TransactionAttribute NULL_TRANSACTION_ATTRIBUTE = new DefaultTransactionAttribute() {
        @Override
        public String toString() {
            return "null";
        }
    };


    /**
     * Logger available to subclasses.
     * <p>As this base class is not marked Serializable, the logger will be recreated
     * after serialization - provided that the concrete subclass is Serializable.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Cache of TransactionAttributes, keyed by method on a specific target class.
     * <p>As this base class is not marked Serializable, the cache will be recreated
     * after serialization - provided that the concrete subclass is Serializable.
     */
    private final Map<Object, TransactionAttribute> attributeCache = new ConcurrentHashMap<>(1024);


    /**
     * Determine the transaction attribute for this method invocation.
     * <p>Defaults to the class's transaction attribute if no method attribute is found.
     *
     * @param method      the method for the current invocation (never {@code null})
     * @param targetClass the target class for this invocation (may be {@code null})
     * @return a TransactionAttribute for this method, or {@code null} if the method
     * is not transactional
     */
    /**
     * 处理@Transactional注解 转换为TransactionAttribute
     * 非 public 方法 不处理
     *
     * @param method      the method to introspect
     * @param targetClass the target class (may be {@code null},
     *                    in which case the declaring class of the method must be used)
     * @return
     * @See computeTransactionAttribute(method, targetClass); // 这里是处理@Transactional注解的具体方法
     */
    @Override
    @Nullable
    public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        if (method.getDeclaringClass() == Object.class) {
            return null;
        }
        // First, see if we have a cached value.
        // cacheKey 示例 public org.springframework.example.tx.entity.SysUser org.springframework.example.tx.service.SysUserService.selectUserById(java.lang.Integer) on class org.springframework.example.tx.service.SysUserService
        Object               cacheKey = getCacheKey(method, targetClass);
        TransactionAttribute cached   = this.attributeCache.get(cacheKey);
        if (cached != null) {
            // Value will either be canonical value indicating there is no transaction attribute,
            // or an actual transaction attribute.
            if (cached == NULL_TRANSACTION_ATTRIBUTE) {
                return null;
            } else {
                return cached;
            }
        } else {
            // We need to work it out.
            // 核心步骤：扫描@Transactional注解 处理注解 转换为TransactionAttribute
            TransactionAttribute txAttr = computeTransactionAttribute(method, targetClass);
            // Put it in the cache.
            if (txAttr == null) { // 非 public方法 返回 null 这里放入缓存标识无事务（NULL_TRANSACTION_ATTRIBUTE）
                this.attributeCache.put(cacheKey, NULL_TRANSACTION_ATTRIBUTE);
            } else {
                String methodIdentification = ClassUtils.getQualifiedMethodName(method, targetClass);
                if (txAttr instanceof DefaultTransactionAttribute) {
                    ((DefaultTransactionAttribute) txAttr).setDescriptor(methodIdentification);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Adding transactional method '" + methodIdentification + "' with attribute: " + txAttr);
                }
                this.attributeCache.put(cacheKey, txAttr);
            }
            return txAttr;
        }
    }

    /**
     * Determine a cache key for the given method and target class.
     * <p>Must not produce same key for overloaded methods.
     * Must produce same key for different instances of the same method.
     *
     * @param method      the method (never {@code null})
     * @param targetClass the target class (may be {@code null})
     * @return the cache key (never {@code null})
     */
    protected Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    /**
     * Same signature as {@link #getTransactionAttribute}, but doesn't cache the result.
     * {@link #getTransactionAttribute} is effectively a caching decorator for this method.
     * <p>As of 4.1.8, this method can be overridden.
     *
     * @see #getTransactionAttribute
     * @since 4.1.8
     */
	/*
	    AnnotationTransactionAttributeSource
		注解解析的入口，负责从目标方法或类上提取@Transactional注解
		解析@Transactional注解，并返回一个TransactionAttribute对象，用于描述事务属性。
	 */
    @Nullable
    protected TransactionAttribute computeTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
        // Don't allow no-public methods as required.
        // 如果子类(AnnotationTransactionAttributeSource)的方法不是public的并且当前方法不是 public 的，直接返回 null
        //     null 值 会被缓存，并标记该类的此方法没有事务的标志
        //     这里用于限制只有 public方法被事务管理器增强
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        // 处理代理情况：获取目标类中最具体的方法定义（例如 CGLIB 代理或 JDK 动态代理）
        // 在使用 AOP 代理时，method 可能是接口方法，而实际的目标类方法可能有更具体的注解。
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        // First try is the method in the target class.
        // 第一步：查找目标类中具体方法上的事务属性（如 @Transactional 注解）
        TransactionAttribute txAttr = findTransactionAttribute(specificMethod);
        if (txAttr != null) {
            return txAttr;
        }

        // Second try is the transaction attribute on the target class.
        // 第二步：如果方法上没有事务属性，则查找目标类上的事务属性
        txAttr = findTransactionAttribute(specificMethod.getDeclaringClass());
        if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
            return txAttr;
        }
        if (specificMethod != method) {
            // Fallback is to look at the original method.
            // 第三步 回退到原始方法（可能是接口方法）
            txAttr = findTransactionAttribute(method);
            if (txAttr != null) {
                return txAttr;
            }
            // Last fallback is the class of the original method.
            // 第四步 否则，从原始方法所在的类中查找事务属性
            txAttr = findTransactionAttribute(method.getDeclaringClass());
            if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
                return txAttr;
            }
        }

        return null; // 表示无事务属性
    }


    /**
     * Subclasses need to implement this to return the transaction attribute for the
     * given class, if any.
     *
     * @param clazz the class to retrieve the attribute for
     * @return all transaction attribute associated with this class, or {@code null} if none
     */
    @Nullable
    protected abstract TransactionAttribute findTransactionAttribute(Class<?> clazz);

    /**
     * Subclasses need to implement this to return the transaction attribute for the
     * given method, if any.
     *
     * @param method the method to retrieve the attribute for
     * @return all transaction attribute associated with this method, or {@code null} if none
     */
    @Nullable
    protected abstract TransactionAttribute findTransactionAttribute(Method method);

    /**
     * Should only public methods be allowed to have transactional semantics?
     * <p>The default implementation returns {@code false}.
     */
    protected boolean allowPublicMethodsOnly() {
        return false;
    }

}