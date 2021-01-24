package com.github.jaychenfe.wxshop.config;


import com.github.jaychenfe.wxshop.service.ShiroRealm;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {

    @Bean()
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {

        //定义返回对象
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //必须设置 SecurityManager,Shiro的核心安全接口
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 配置访问权限 必须是LinkedHashMap，因为它必须保证有序
        // 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 一定要注意顺序,否则就不好使了
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //logout是shiro提供的过滤器
        filterChainDefinitionMap.put("/api/login", "anon");
        filterChainDefinitionMap.put("/api/code", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置自定义realm.
        securityManager.setRealm(shiroRealm);

        //配置缓存管理器
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        //配置session管理器
        securityManager.setSessionManager(new DefaultSessionManager());
        return securityManager;
    }
}
