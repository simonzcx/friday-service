package com.friday.flowable.service.impl;

import org.flowable.ui.common.model.RemoteGroup;
import org.flowable.ui.common.model.RemoteToken;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomRemoteServiceImpl implements RemoteIdmService {
    private static final Logger logger = LoggerFactory.getLogger(CustomRemoteServiceImpl.class);

    @Override
    public RemoteUser authenticateUser(String s, String s1) {
        logger.debug("CustomRemoteServiceImpl:authenticateUser");
        return null;
    }

    @Override
    public RemoteToken getToken(String s) {
        logger.debug("CustomRemoteServiceImpl:getToken");
        return null;
    }

    @Override
    public RemoteUser getUser(String s) {
        logger.debug("CustomRemoteServiceImpl:getUser");
        return null;
    }

    @Override
    public List<RemoteUser> findUsersByNameFilter(String s) {
        logger.debug("CustomRemoteServiceImpl:findUsersByNameFilter");
        return null;
    }

    @Override
    public List<RemoteUser> findUsersByGroup(String s) {
        logger.debug("CustomRemoteServiceImpl:findUsersByGroup");
        return null;
    }

    @Override
    public RemoteGroup getGroup(String s) {
        logger.debug("CustomRemoteServiceImpl:getGroup");
        return null;
    }

    @Override
    public List<RemoteGroup> findGroupsByNameFilter(String s) {
        logger.debug("CustomRemoteServiceImpl:findGroupsByNameFilter");
        return null;
    }
}
