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

package org.springframework.transaction.annotation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.transaction.config.TransactionManagementConfigUtils;
import org.springframework.util.ClassUtils;

/**
 * Selects which implementation of {@link AbstractTransactionManagementConfiguration}
 * should be used based on the value of {@link EnableTransactionManagement#mode} on the
 * importing {@code @Configuration} class.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @see EnableTransactionManagement
 * @see ProxyTransactionManagementConfiguration
 * @see TransactionManagementConfigUtils#TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME
 * @see TransactionManagementConfigUtils#JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME
 * @since 3.1
 */
public class TransactionManagementConfigurationSelector extends AdviceModeImportSelector<EnableTransactionManagement> {

    /**
     * Returns {@link ProxyTransactionManagementConfiguration} or
     * {@code AspectJ(Jta)TransactionManagementConfiguration} for {@code PROXY}
     * and {@code ASPECTJ} values of {@link EnableTransactionManagement#mode()},
     * respectively.
     */
    @Override
    // TransactionManagementConfigurationSelector
    protected String[] selectImports(AdviceMode adviceMode) {
        // 注意这里的 AdviceMode 取决于@EnableTransactionManagement注解中的mode属性
        // 不是 @EnableAspectJAutoProxy的 proxyTargetClass属性
        switch (adviceMode) { // 根据不同的模式选择不同的配置类
            case PROXY:  // jdk动态代理模式
                return new String[]{AutoProxyRegistrar.class.getName(), // 负责注册动态代理基础事务
                        ProxyTransactionManagementConfiguration.class.getName()}; // 配置事务拦截器和切面
            case ASPECTJ: // cjlib 切面模式
                // org.springframework.transaction.aspectj.AspectJJtaTransactionManagementConfiguration
                // org.springframework.transaction.aspectj.AspectJTransactionManagementConfiguration
                return new String[]{determineTransactionAspectClass()};
            default:
                return null;
        }
    }

    private String determineTransactionAspectClass() {
        return (ClassUtils.isPresent("javax.transaction.Transactional", getClass().getClassLoader()) ?
                TransactionManagementConfigUtils.JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME :
                TransactionManagementConfigUtils.TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME); // 事务拦截器和切面
    }

}