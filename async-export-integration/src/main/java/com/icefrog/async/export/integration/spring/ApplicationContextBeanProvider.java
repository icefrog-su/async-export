/***
 * Copyright 2019 icefrog.su
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icefrog.async.export.integration.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/***
 * Application context bean provider. impl to ApplicationContextAware
 *
 * @see ApplicationContext
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 *
 * @author icefrog
 */
@Component
public class ApplicationContextBeanProvider implements ApplicationContextAware {

    /***
     * Application Context
     */
    private static ApplicationContext applicationContext;

    /***
     * Set the applicationContext for ApplicationContextBeanProvider#applicationContext
     *
     * @param applicationContext ApplicationContext
     * @throws BeansException BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextBeanProvider.applicationContext = applicationContext;
    }

    /***
     * Get bean from applicationContext.
     *
     * @param beanId Spring bean id. The default is the name of the class and the initial letter is lowercase
     * @param clazz Class<T>
     * @param <T> the type
     * @return the bean for type. If not fount with beans. throw exception.
     */
    public <T> T getBean(String beanId, Class<T> clazz) {
        return ApplicationContextBeanProvider.applicationContext.getBean(beanId, clazz);
    }

    /***
     * Get bean from applicationContext.
     *
     * @param clazz Class<T>
     * @param <T> the type
     * @return the bean for type. If not fount with beans. throw exception.
     */
    public <T> T getBean(Class<T> clazz) {
        return ApplicationContextBeanProvider.applicationContext.getBean(clazz);
    }

    /***
     * Get bean from applicationContext.
     *
     * @param beanId Spring bean id. The default is the name of the class and the initial letter is lowercase
     * @return the bean for type. If not fount with beans. throw exception.
     */
    public Object getBean(String beanId) {
        return ApplicationContextBeanProvider.applicationContext.getBean(beanId);
    }

    /***
     * Get bean from applicationContext of type
     *
     * @param clazz Class<T>
     * @param <T> the type
     * @return the bean map for type. If not fount with beans. throw exception.
     */
    public <T> Map<String, T> getBeanOfType(Class<T> clazz) {
        return ApplicationContextBeanProvider.applicationContext.getBeansOfType(clazz);
    }
}
