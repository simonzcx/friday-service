package com.friday.flowable.service.impl;

import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.security.FlowableAppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);

    private static final List<String> FLOW_ABLE_MODELER_ROLES =
            Arrays.asList("access-idm", "access-rest-api", "access-task", "access-modeler", "access-admin");

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        LOGGER.debug("MyUserDetailsService:loadUserByUsername:认证权限.....");
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();

        // 配置 flowable-modeler 权限
        FLOW_ABLE_MODELER_ROLES.parallelStream().forEach(obj -> {
            authorities.add(new SimpleGrantedAuthority(obj));
        });

        RemoteUser sourceUser = new RemoteUser();
        sourceUser.setId("123456");
        sourceUser.setFirstName("admin");
        sourceUser.setDisplayName("管理员");
        sourceUser.setPassword("123456");
        sourceUser.setPrivileges(new ArrayList<>(FLOW_ABLE_MODELER_ROLES));
        return new FlowableAppUser(sourceUser, "admin", authorities);
    }
}
