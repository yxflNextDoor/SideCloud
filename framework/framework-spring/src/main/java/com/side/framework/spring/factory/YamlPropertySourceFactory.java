package com.side.framework.spring.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Objects;
import java.util.Properties;

/**
 * 使@PropertySource注解可以读取yaml文件
 * 用例：@PropertySource(value = "classpath:test.yml", factory = YamlPropertySourceFactory.class)
 *
 * @author yxfl
 * @date 2024/04/02 23
 **/
public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties properties = factory.getObject();
        assert properties != null;
        return new PropertiesPropertySource(Objects.requireNonNull(resource.getResource().getFilename()), properties);
    }
}
