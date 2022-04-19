package com.friday.flowable.service.impl;

import org.flowable.engine.DynamicBpmnService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomFlowableService {

    @Resource
    private DynamicBpmnService dynamicBpmnService;

    @Test
    public void test() {

    }

}
