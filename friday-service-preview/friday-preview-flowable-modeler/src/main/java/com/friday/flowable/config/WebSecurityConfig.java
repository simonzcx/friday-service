package com.friday.flowable.config;

import com.friday.flowable.common.filter.CustomFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.annotation.Resource;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private CustomFilter customFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(customFilter, SecurityContextPersistenceFilter.class)
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and().csrf().disable();//csrf 要关掉，不然前端调用会被拦截。出现 Forbidden 一闪而过的情况
    }

}
