package com.friday.flowable.controller;

import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.security.FlowableAppUser;
import org.flowable.ui.common.security.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoteAccountResource {

    @GetMapping("app/rest/account")
    public UserRepresentation getAccount() {
        FlowableAppUser appUser = SecurityUtils.getCurrentFlowableAppUser();
        UserRepresentation userRepresentation = new UserRepresentation(appUser.getUserObject());
        if (appUser.getUserObject() instanceof RemoteUser) {
            RemoteUser temp = (RemoteUser) appUser.getUserObject();
            userRepresentation.setPrivileges(temp.getPrivileges());
        }
        return userRepresentation;
    }
}
