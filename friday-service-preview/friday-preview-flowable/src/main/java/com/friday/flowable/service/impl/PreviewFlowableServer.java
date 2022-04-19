package com.friday.flowable.service.impl;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PreviewFlowableServer {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;


    /**
     * Step1：部署流程
     * <p>
     * act_re_deployment(部署对象表) 存放流程定义的显示名和部署时间，每部署一次增加一条记录
     * act_re_procdef(流程定义表)存放流程定义的属性信息，部署每一个新的流程定义都会在这张表中增加一条记录
     * <p>
     * 注：当流程定义KEY相同的情况下，使用的是版本升级
     * act_ge_bytearray(资源文件表) 存储流程定义相关的部署信息。即流程定义文档的存放地。
     * 每部署一次都会增加两条记录，一条是关于bpmn规则文件的，
     * 一条是图片的（如果部署时只指定可bpmn一个文件，Flowable会在部署时解析bpmn文件内容自动生成流程图）。
     * 两个文件不是很大，都是以二进制形式存储在数据库中
     * <p>
     * 下一个部署ID(act_re_deployment的ID_)由act_ge_property表的next.dbid决定
     * <p>
     * insert into ACT_RE_PROCDEF(ID_, REV_, CATEGORY_, NAME_, KEY_, VERSION_, DEPLOYMENT_ID_, RESOURCE_NAME_, DGRM_RESOURCE_NAME_, DESCRIPTION_, HAS_START_FORM_KEY_, HAS_GRAPHICAL_NOTATION_ , SUSPENSION_STATE_, DERIVED_FROM_, DERIVED_FROM_ROOT_, DERIVED_VERSION_, TENANT_ID_, ENGINE_VERSION_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), http://www.flowable.org/processdef(String), 请假流程(String), leave_flow(String), 1(Integer), e5ae503b-bf83-11ec-b5f2-b278c0660b00(String), bpmn/请假流程.bpmn20.xml(String), bpmn/请假流程.leave_flow.png(String), 请假流程(String), false(Boolean), true(Boolean), 1(Integer), null, null, 0(Integer), (String), null
     * <p>
     * insert into ACT_RE_DEPLOYMENT(ID_, NAME_, CATEGORY_, KEY_, TENANT_ID_, DEPLOY_TIME_, DERIVED_FROM_, DERIVED_FROM_ROOT_, PARENT_DEPLOYMENT_ID_, ENGINE_VERSION_) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: e5ae503b-bf83-11ec-b5f2-b278c0660b00(String), 请假流程(String), null, null, (String), 2022-04-19 09:56:15.667(Timestamp), null, null, null, null
     * <p>
     * INSERT INTO ACT_GE_BYTEARRAY(ID_, REV_, NAME_, BYTES_, DEPLOYMENT_ID_, GENERATED_) VALUES (?, 1, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?)
     * Parameters: e5ae503c-bf83-11ec-b5f2-b278c0660b00(String), bpmn/请假流程.bpmn20.xml(String), java.io.ByteArrayInputStream@3757e8e2(ByteArrayInputStream), e5ae503b-bf83-11ec-b5f2-b278c0660b00(String), false(Boolean), e63819ad-bf83-11ec-b5f2-b278c0660b00(String), bpmn/请假流程.leave_flow.png(String), java.io.ByteArrayInputStream@1653b84e(ByteArrayInputStream), e5ae503b-bf83-11ec-b5f2-b278c0660b00(String), true(Boolean)
     */
    @Test
    public void deploy() {
        DeploymentBuilder builder = repositoryService.createDeployment()
                .name("请假流程")
                .addClasspathResource("bpmn/请假流程.bpmn20.xml");
        Deployment deployment = builder.deploy();

        System.out.println(deployment);
    }

    /**
     * Step2：启动流程实例
     * 启动流程实例（流程实例是最大的执行实例execution）
     * 流程实例只有一条，执行实例有多条，执行实例的parentId或（最顶层parentId）是流程实例的ID
     * 操作的数据库表为act_ru_execution,如果是用户任务节点，同时会在act_ru_task添加一条实例
     * <p>
     * insert into ACT_HI_TASKINST ( ID_, REV_, TASK_DEF_ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, SCOPE_DEFINITION_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, OWNER_, ASSIGNEE_, START_TIME_, CLAIM_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TASK_DEF_KEY_, FORM_KEY_, PRIORITY_, DUE_DATE_, CATEGORY_, TENANT_ID_, LAST_UPDATED_TIME_ ) values ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: e0320fc0-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), null, null, null, null, 组长审批(String), null, null, null, null, 2022-04-19 10:24:44.45(Timestamp), null, null, null, null, zz_sq(String), null, 50(Integer), null, null, (String), 2022-04-19 10:24:44.452(Timestamp)
     * Updates: 1
     * <p>
     * insert into ACT_HI_PROCINST ( ID_, REV_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, START_TIME_, END_TIME_, DURATION_, START_USER_ID_, START_ACT_ID_, END_ACT_ID_, SUPER_PROCESS_INSTANCE_ID_, DELETE_REASON_, TENANT_ID_, NAME_, CALLBACK_ID_, CALLBACK_TYPE_ ) values ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 10:24:44.446(Timestamp), null, null, null, startEvent1(String), null, null, null, (String), null, null, null
     * Updates: 1
     * <p>
     * insert into ACT_HI_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: e031737d-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), startEvent1(String), null, null, 开始(String), startEvent(String), null, 2022-04-19 10:24:44.447(Timestamp), 2022-04-19 10:24:44.449(Timestamp), 2(Long), null, (String), e031c19e-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), sid-9F2FD79A-636C-40BE-A329-49D9F4D561C3(String), null, null, null, sequenceFlow(String), null, 2022-04-19 10:24:44.449(Timestamp), 2022-04-19 10:24:44.449(Timestamp), 0(Long), null, (String), e031c19f-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), zz_sq(String), e0320fc0-bf87-11ec-b2be-26459411c9ea(String), null, 组长审批(String), userTask(String), null, 2022-04-19 10:24:44.449(Timestamp), null, null, null, (String)
     * Updates: 3
     * <p>
     * insert into ACT_RU_EXECUTION (ID_, REV_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, ACT_ID_, IS_ACTIVE_, IS_CONCURRENT_, IS_SCOPE_,IS_EVENT_SCOPE_, IS_MI_ROOT_, PARENT_ID_, SUPER_EXEC_, ROOT_PROC_INST_ID_, SUSPENSION_STATE_, TENANT_ID_, NAME_, START_ACT_ID_, START_TIME_, START_USER_ID_, IS_COUNT_ENABLED_, EVT_SUBSCR_COUNT_, TASK_COUNT_, JOB_COUNT_, TIMER_JOB_COUNT_, SUSP_JOB_COUNT_, DEADLETTER_JOB_COUNT_, VAR_COUNT_, ID_LINK_COUNT_, CALLBACK_ID_, CALLBACK_TYPE_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), null, true(Boolean), false(Boolean), true(Boolean), false(Boolean), false(Boolean), null, null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, startEvent1(String), 2022-04-19 10:24:44.446(Timestamp), null, true(Boolean), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), null, null, e031737c-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), zz_sq(String), true(Boolean), false(Boolean), false(Boolean), false(Boolean), false(Boolean), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, null, 2022-04-19 10:24:44.447(Timestamp), null, true(Boolean), 0(Integer), 1(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), null, null
     * Updates: 2
     * <p>
     * insert into ACT_RU_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: e031737d-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), startEvent1(String), null, null, 开始(String), startEvent(String), null, 2022-04-19 10:24:44.447(Timestamp), 2022-04-19 10:24:44.449(Timestamp), 2(Long), null, (String), e031c19e-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), sid-9F2FD79A-636C-40BE-A329-49D9F4D561C3(String), null, null, null, sequenceFlow(String), null, 2022-04-19 10:24:44.449(Timestamp), 2022-04-19 10:24:44.449(Timestamp), 0(Long), null, (String), e031c19f-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), zz_sq(String), e0320fc0-bf87-11ec-b2be-26459411c9ea(String), null, 组长审批(String), userTask(String), null, 2022-04-19 10:24:44.449(Timestamp), null, null, null, (String)
     * Updates: 3
     * <p>
     * insert into ACT_RU_TASK (ID_, REV_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, PRIORITY_, CREATE_TIME_, OWNER_, ASSIGNEE_, DELEGATION_, EXECUTION_ID_, PROC_INST_ID_, PROC_DEF_ID_, TASK_DEF_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, SCOPE_DEFINITION_ID_, TASK_DEF_KEY_, DUE_DATE_, CATEGORY_, SUSPENSION_STATE_, TENANT_ID_, FORM_KEY_, CLAIM_TIME_, IS_COUNT_ENABLED_, VAR_COUNT_, ID_LINK_COUNT_, SUB_TASK_COUNT_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: e0320fc0-bf87-11ec-b2be-26459411c9ea(String), 组长审批(String), null, null, 50(Integer), 2022-04-19 10:24:44.45(Timestamp), null, null, null, e031737c-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), null, null, null, null, null, zz_sq(String), null, null, 1(Integer), (String), null, null, true(Boolean), 0(Integer), 2(Integer), 0(Integer)
     * Updates: 1
     */
    @Test
    public void startProcessInstanceByKey() {
        String processDefinitionKey = "leave_flow";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        System.out.println(processInstance.getId());
        System.out.println(processInstance.getActivityId());
    }

    /**
     * Step3：查询任务（节点1）
     * <p>
     * select distinct RES.* from ACT_RU_TASK RES WHERE (RES.ASSIGNEE_ = ? or ( RES.ASSIGNEE_ is null and exists(select LINK.ID_ from ACT_RU_IDENTITYLINK LINK where LINK.TASK_ID_ = RES.ID_ and LINK.TYPE_ = 'candidate' and (LINK.USER_ID_ = ? )))) order by RES.ID_ asc
     * Parameters: 张三(String), 张三(String)
     * Total: 1
     */
    @Test
    public void findTaskByUserIdNode1() {
        String userId = "张三";
        Task task = taskService.createTaskQuery().taskCandidateOrAssigned(userId).singleResult();
        System.out.println(task);
    }

    /**
     * Step3：抢占/认领任务（节点1）
     *
     * update ACT_HI_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, ASSIGNEE_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 张三(String), e031c19f-bf87-11ec-b2be-26459411c9ea(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_HI_TASKINST SET REV_ = ?, ASSIGNEE_ = ?, CLAIM_TIME_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), 张三(String), 2022-04-19 10:43:24.884(Timestamp), 2022-04-19 10:43:25.057(Timestamp), e0320fc0-bf87-11ec-b2be-26459411c9ea(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, ASSIGNEE_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 张三(String), e031c19f-bf87-11ec-b2be-26459411c9ea(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_TASK SET REV_ = ?, ASSIGNEE_ = ?, CLAIM_TIME_ = ? where ID_= ? and REV_ = ?
     * Parameters: 2(Integer), 张三(String), 2022-04-19 10:43:24.884(Timestamp), e0320fc0-bf87-11ec-b2be-26459411c9ea(String), 1(Integer)
     * Updates: 1
     */
    @Test
    public void claimTaskNode1() {
        String userId = "张三";
        Task task = taskService.createTaskQuery().taskCandidateOrAssigned(userId).singleResult();

        taskService.claim(task.getId(), userId);
    }

    /**
     * Step4：完成任务（节点1）
     * Preparing: insert into ACT_HI_DETAIL (ID_, TYPE_, PROC_INST_ID_, EXECUTION_ID_, ACT_INST_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, TIME_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) values ( ?, 'VariableUpdate', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 0819921c-bf8d-11ec-abe8-4ecbd0e4f490(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031c19f-bf87-11ec-b2be-26459411c9ea(String), null, assignee(String), 0(Integer), string(String), 2022-04-19 11:01:38.882(Timestamp), null, null, null, 王五(String), null
     * Updates: 1
     *
     * Preparing: insert into ACT_HI_VARINST (ID_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_, CREATE_TIME_, LAST_UPDATED_TIME_) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 08106a5b-bf8d-11ec-abe8-4ecbd0e4f490(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, assignee(String), 0(Integer), string(String), null, null, null, null, null, null, 王五(String), null, 2022-04-19 11:01:38.826(Timestamp), 2022-04-19 11:01:38.826(Timestamp)
     * Updates: 1
     *
     * Preparing: insert into ACT_HI_TASKINST ( ID_, REV_, TASK_DEF_ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, SCOPE_DEFINITION_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, OWNER_, ASSIGNEE_, START_TIME_, CLAIM_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TASK_DEF_KEY_, FORM_KEY_, PRIORITY_, DUE_DATE_, CATEGORY_, TENANT_ID_, LAST_UPDATED_TIME_ ) values ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 08402cef-bf8d-11ec-abe8-4ecbd0e4f490(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), null, null, null, null, 动态审批(String), null, null, null, null, 2022-04-19 11:01:39.133(Timestamp), null, null, null, null, dt_sp(String), null, 50(Integer), null, null, (String), 2022-04-19 11:01:39.136(Timestamp)
     * Updates: 1
     *
     * Preparing: insert into ACT_HI_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: 08388bcd-bf8d-11ec-abe8-4ecbd0e4f490(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), sid-F6E212BE-5AC3-4B6C-BA4A-3DA3D205C11A(String), null, null, null, sequenceFlow(String), null, 2022-04-19 11:01:39.086(Timestamp), 2022-04-19 11:01:39.086(Timestamp), 0(Long), null, (String), 083fb7be-bf8d-11ec-abe8-4ecbd0e4f490(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), dt_sp(String), 08402cef-bf8d-11ec-abe8-4ecbd0e4f490(String), null, 动态审批(String), userTask(String), null, 2022-04-19 11:01:39.133(Timestamp), null, null, null, (String)
     * Updates: 2
     *
     * Preparing: insert into ACT_RU_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: 08388bcd-bf8d-11ec-abe8-4ecbd0e4f490(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), sid-F6E212BE-5AC3-4B6C-BA4A-3DA3D205C11A(String), null, null, null, sequenceFlow(String), null, 2022-04-19 11:01:39.086(Timestamp), 2022-04-19 11:01:39.086(Timestamp), 0(Long), null, (String), 083fb7be-bf8d-11ec-abe8-4ecbd0e4f490(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), dt_sp(String), 08402cef-bf8d-11ec-abe8-4ecbd0e4f490(String), null, 动态审批(String), userTask(String), null, 2022-04-19 11:01:39.133(Timestamp), null, null, null, (String)
     * Updates: 2
     *
     * Preparing: insert into ACT_RU_TASK (ID_, REV_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, PRIORITY_, CREATE_TIME_, OWNER_, ASSIGNEE_, DELEGATION_, EXECUTION_ID_, PROC_INST_ID_, PROC_DEF_ID_, TASK_DEF_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, SCOPE_DEFINITION_ID_, TASK_DEF_KEY_, DUE_DATE_, CATEGORY_, SUSPENSION_STATE_, TENANT_ID_, FORM_KEY_, CLAIM_TIME_, IS_COUNT_ENABLED_, VAR_COUNT_, ID_LINK_COUNT_, SUB_TASK_COUNT_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 08402cef-bf8d-11ec-abe8-4ecbd0e4f490(String), 动态审批(String), null, null, 50(Integer), 2022-04-19 11:01:39.133(Timestamp), null, null, null, e031737c-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), null, null, null, null, null, dt_sp(String), null, null, 1(Integer), (String), null, null, true(Boolean), 0(Integer), 1(Integer), 0(Integer)
     * Updates: 1
     *
     * Preparing: insert into ACT_RU_VARIABLE (ID_, REV_, TYPE_, NAME_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) values ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 08106a5b-bf8d-11ec-abe8-4ecbd0e4f490(String), string(String), assignee(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, null, null, null, null, null, null, 王五(String), null
     * Updates: 1
     *
     * Preparing: update ACT_HI_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 3(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 11:01:39.022(Timestamp), 2215022(Long), e031c19f-bf87-11ec-b2be-26459411c9ea(String), 2(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_HI_TASKINST SET REV_ = ?, END_TIME_ = ?, DURATION_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 3(Integer), 2022-04-19 11:01:38.977(Timestamp), 2214977(Long), 2022-04-19 11:01:38.977(Timestamp), e0320fc0-bf87-11ec-b2be-26459411c9ea(String), 2(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 3(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 11:01:39.022(Timestamp), 2215022(Long), e031c19f-bf87-11ec-b2be-26459411c9ea(String), 2(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_EXECUTION SET REV_ = ?, ACT_ID_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), dt_sp(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: delete from ACT_RU_TASK where ID_ = ? and REV_ = ?
     * Parameters: e0320fc0-bf87-11ec-b2be-26459411c9ea(String), 2(Integer)
     * Updates: 1
     */
    @Test
    public void completeTaskNode1() {
        String userId = "张三";
        Task task = taskService.createTaskQuery().taskCandidateOrAssigned(userId).singleResult();

        Map<String, Object> variables = new HashMap<String, Object>() {{
            put("assignee", "王五");
        }};

        taskService.complete(task.getId(), variables);
    }

    /**
     * Step5：查询任务（节点2）
     * <p>
     * select distinct RES.* from ACT_RU_TASK RES WHERE (RES.ASSIGNEE_ = ? or ( RES.ASSIGNEE_ is null and exists(select LINK.ID_ from ACT_RU_IDENTITYLINK LINK where LINK.TASK_ID_ = RES.ID_ and LINK.TYPE_ = 'candidate' and (LINK.USER_ID_ = ? )))) order by RES.ID_ asc
     * Parameters: 王五(String), 王五(String)
     * Total: 1
     */
    @Test
    public void findTaskByUserIdNode2() {
        String userId = "王五";
        Task task = taskService.createTaskQuery().taskCandidateOrAssigned(userId).singleResult();
        System.out.println(task);
    }

    /**
     * Step6：抢占/认领任务（节点2）
     */
    @Test
    public void claimTaskNode2() {
        String userId = "王五";
        Task task = taskService.createTaskQuery().taskCandidateOrAssigned(userId).singleResult();

        taskService.claim(task.getId(), userId);
    }

    /**
     * Step7：完成任务（节点2）
     *
     * parent 'e0314c6b-bf87-11ec-b2be-26459411c9ea' created with parent e0314c6b-bf87-11ec-b2be-26459411c9ea
     * parent '317b4d88-bf91-11ec-9b6a-2615652caa30' created with parent 317b4d88-bf91-11ec-9b6a-2615652caa30
     * parent '317b4d88-bf91-11ec-9b6a-2615652caa30' created with parent 317b4d88-bf91-11ec-9b6a-2615652caa30
     * parent '317b4d88-bf91-11ec-9b6a-2615652caa30' created with parent 317b4d88-bf91-11ec-9b6a-2615652caa30
     * parent '317b4d88-bf91-11ec-9b6a-2615652caa30' created with parent 317b4d88-bf91-11ec-9b6a-2615652caa30
     * Preparing: insert into ACT_HI_DETAIL (ID_, TYPE_, PROC_INST_ID_, EXECUTION_ID_, ACT_INST_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, TIME_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: 3157bff6-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 083fb7be-bf8d-11ec-abe8-4ecbd0e4f490(String), null, parallelList(String), 0(Integer), serializable(String), 2022-04-19 11:31:26.063(Timestamp), 315798e5-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, 317be9ca-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, nrOfInstances(String), 0(Integer), integer(String), 2022-04-19 11:31:26.301(Timestamp), null, null, 4(Long), 4(String), null, 317be9cc-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, nrOfCompletedInstances(String), 0(Integer), integer(String), 2022-04-19 11:31:26.301(Timestamp), null, null, 0(Long), 0(String), null, 317be9ce-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, nrOfActiveInstances(String), 0(Integer), integer(String), 2022-04-19 11:31:26.301(Timestamp), null, null, 4(Long), 4(String), null, 317c0fe4-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), null, null, parallel(String), 0(Integer), string(String), 2022-04-19 11:31:26.302(Timestamp), null, null, null, 并行会签审批人1(String), null, 317c36f6-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), null, null, parallel(String), 0(Integer), string(String), 2022-04-19 11:31:26.303(Timestamp), null, null, null, 并行会签审批人2(String), null, 317c36f8-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), null, null, parallel(String), 0(Integer), string(String), 2022-04-19 11:31:26.303(Timestamp), null, null, null, 并行会签审批人3(String), null, 317c36fa-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), null, null, parallel(String), 0(Integer), string(String), 2022-04-19 11:31:26.303(Timestamp), null, null, null, 并行会签审批人4(String), null, 317c5e0c-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), null, null, loopCounter(String), 0(Integer), integer(String), 2022-04-19 11:31:26.304(Timestamp), null, null, 0(Long), 0(String), null, 31833be2-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), null, null, loopCounter(String), 0(Integer), integer(String), 2022-04-19 11:31:26.349(Timestamp), null, null, 1(Long), 1(String), null, 318362f8-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), null, null, loopCounter(String), 0(Integer), integer(String), 2022-04-19 11:31:26.35(Timestamp), null, null, 2(Long), 2(String), null, 318362fe-bf91-11ec-9b6a-2615652caa30(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), null, null, loopCounter(String), 0(Integer), integer(String), 2022-04-19 11:31:26.35(Timestamp), null, null, 3(Long), 3(String), null
     * Updates: 12
     *
     * Preparing: insert into ACT_HI_VARINST (ID_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_, CREATE_TIME_, LAST_UPDATED_TIME_) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 31506cf3-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, parallelList(String), 0(Integer), serializable(String), null, null, null, 31509404-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, 2022-04-19 11:31:26.017(Timestamp), 2022-04-19 11:31:26.017(Timestamp), 317be9c9-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, nrOfInstances(String), 0(Integer), integer(String), null, null, null, null, null, 4(Long), 4(String), null, 2022-04-19 11:31:26.301(Timestamp), 2022-04-19 11:31:26.301(Timestamp), 317be9cb-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, nrOfCompletedInstances(String), 0(Integer), integer(String), null, null, null, null, null, 0(Long), 0(String), null, 2022-04-19 11:31:26.301(Timestamp), 2022-04-19 11:31:26.301(Timestamp), 317be9cd-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, nrOfActiveInstances(String), 0(Integer), integer(String), null, null, null, null, null, 4(Long), 4(String), null, 2022-04-19 11:31:26.301(Timestamp), 2022-04-19 11:31:26.301(Timestamp), 317c0fe3-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), null, parallel(String), 0(Integer), string(String), null, null, null, null, null, null, 并行会签审批人1(String), null, 2022-04-19 11:31:26.302(Timestamp), 2022-04-19 11:31:26.302(Timestamp), 317c36f5-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), null, parallel(String), 0(Integer), string(String), null, null, null, null, null, null, 并行会签审批人2(String), null, 2022-04-19 11:31:26.303(Timestamp), 2022-04-19 11:31:26.303(Timestamp), 317c36f7-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), null, parallel(String), 0(Integer), string(String), null, null, null, null, null, null, 并行会签审批人3(String), null, 2022-04-19 11:31:26.303(Timestamp), 2022-04-19 11:31:26.303(Timestamp), 317c36f9-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), null, parallel(String), 0(Integer), string(String), null, null, null, null, null, null, 并行会签审批人4(String), null, 2022-04-19 11:31:26.303(Timestamp), 2022-04-19 11:31:26.303(Timestamp), 317c5e0b-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), null, loopCounter(String), 0(Integer), integer(String), null, null, null, null, null, 0(Long), 0(String), null, 2022-04-19 11:31:26.304(Timestamp), 2022-04-19 11:31:26.304(Timestamp), 31833be1-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), null, loopCounter(String), 0(Integer), integer(String), null, null, null, null, null, 1(Long), 1(String), null, 2022-04-19 11:31:26.349(Timestamp), 2022-04-19 11:31:26.349(Timestamp), 318362f7-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), null, loopCounter(String), 0(Integer), integer(String), null, null, null, null, null, 2(Long), 2(String), null, 2022-04-19 11:31:26.35(Timestamp), 2022-04-19 11:31:26.35(Timestamp), 318362fd-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), null, loopCounter(String), 0(Integer), integer(String), null, null, null, null, null, 3(Long), 3(String), null, 2022-04-19 11:31:26.35(Timestamp), 2022-04-19 11:31:26.35(Timestamp)
     * Updates: 12
     *
     * Preparing: insert into ACT_HI_TASKINST ( ID_, REV_, TASK_DEF_ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, SCOPE_DEFINITION_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, OWNER_, ASSIGNEE_, START_TIME_, CLAIM_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TASK_DEF_KEY_, FORM_KEY_, PRIORITY_, DUE_DATE_, CATEGORY_, TENANT_ID_, LAST_UPDATED_TIME_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: 317c5e0e-bf91-11ec-9b6a-2615652caa30(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, 并行会签审批(String), null, null, null, 并行会签审批人1(String), 2022-04-19 11:31:26.304(Timestamp), null, null, null, null, hq_sp_parallel(String), null, 50(Integer), null, null, (String), 2022-04-19 11:31:26.308(Timestamp), 31833be4-bf91-11ec-9b6a-2615652caa30(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, 并行会签审批(String), null, null, null, 并行会签审批人2(String), 2022-04-19 11:31:26.349(Timestamp), null, null, null, null, hq_sp_parallel(String), null, 50(Integer), null, null, (String), 2022-04-19 11:31:26.35(Timestamp), 318362fa-bf91-11ec-9b6a-2615652caa30(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, 并行会签审批(String), null, null, null, 并行会签审批人3(String), 2022-04-19 11:31:26.35(Timestamp), null, null, null, null, hq_sp_parallel(String), null, 50(Integer), null, null, (String), 2022-04-19 11:31:26.35(Timestamp), 31838a10-bf91-11ec-9b6a-2615652caa30(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, 并行会签审批(String), null, null, null, 并行会签审批人4(String), 2022-04-19 11:31:26.35(Timestamp), null, null, null, null, hq_sp_parallel(String), null, 50(Integer), null, null, (String), 2022-04-19 11:31:26.351(Timestamp)
     * Updates: 4
     *
     * Preparing: insert into ACT_HI_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: 3174e4e7-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), sid-51A06B9C-47F5-4DB8-959A-A00E541D336B(String), null, null, null, sequenceFlow(String), null, 2022-04-19 11:31:26.255(Timestamp), 2022-04-19 11:31:26.255(Timestamp), 0(Long), null, (String), 317c5e0d-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String), 317c5e0e-bf91-11ec-9b6a-2615652caa30(String), null, 并行会签审批(String), userTask(String), 并行会签审批人1(String), 2022-04-19 11:31:26.304(Timestamp), null, null, null, (String), 31833be3-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String), 31833be4-bf91-11ec-9b6a-2615652caa30(String), null, 并行会签审批(String), userTask(String), 并行会签审批人2(String), 2022-04-19 11:31:26.349(Timestamp), null, null, null, (String), 318362f9-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String), 318362fa-bf91-11ec-9b6a-2615652caa30(String), null, 并行会签审批(String), userTask(String), 并行会签审批人3(String), 2022-04-19 11:31:26.35(Timestamp), null, null, null, (String), 318362ff-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String), 31838a10-bf91-11ec-9b6a-2615652caa30(String), null, 并行会签审批(String), userTask(String), 并行会签审批人4(String), 2022-04-19 11:31:26.35(Timestamp), null, null, null, (String)
     * Updates: 5
     *
     * Preparing: insert into ACT_RU_EXECUTION (ID_, REV_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, ACT_ID_, IS_ACTIVE_, IS_CONCURRENT_, IS_SCOPE_,IS_EVENT_SCOPE_, IS_MI_ROOT_, PARENT_ID_, SUPER_EXEC_, ROOT_PROC_INST_ID_, SUSPENSION_STATE_, TENANT_ID_, NAME_, START_ACT_ID_, START_TIME_, START_USER_ID_, IS_COUNT_ENABLED_, EVT_SUBSCR_COUNT_, TASK_COUNT_, JOB_COUNT_, TIMER_JOB_COUNT_, SUSP_JOB_COUNT_, DEADLETTER_JOB_COUNT_, VAR_COUNT_, ID_LINK_COUNT_, CALLBACK_ID_, CALLBACK_TYPE_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), hq_sp_parallel(String), false(Boolean), false(Boolean), false(Boolean), false(Boolean), true(Boolean), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, null, 2022-04-19 11:31:26.256(Timestamp), null, true(Boolean), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 3(Integer), 0(Integer), null, null, 317be9cf-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), hq_sp_parallel(String), true(Boolean), false(Boolean), false(Boolean), false(Boolean), false(Boolean), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, null, 2022-04-19 11:31:26.301(Timestamp), null, true(Boolean), 0(Integer), 1(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 2(Integer), 0(Integer), null, null, 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), hq_sp_parallel(String), true(Boolean), false(Boolean), false(Boolean), false(Boolean), false(Boolean), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, null, 2022-04-19 11:31:26.302(Timestamp), null, true(Boolean), 0(Integer), 1(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 2(Integer), 0(Integer), null, null, 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), hq_sp_parallel(String), true(Boolean), false(Boolean), false(Boolean), false(Boolean), false(Boolean), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, null, 2022-04-19 11:31:26.302(Timestamp), null, true(Boolean), 0(Integer), 1(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 2(Integer), 0(Integer), null, null, 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), hq_sp_parallel(String), true(Boolean), false(Boolean), false(Boolean), false(Boolean), false(Boolean), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, null, 2022-04-19 11:31:26.302(Timestamp), null, true(Boolean), 0(Integer), 1(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 2(Integer), 0(Integer), null, null
     * Updates: 5
     *
     * Preparing: insert into ACT_RU_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: 3174e4e7-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e031737c-bf87-11ec-b2be-26459411c9ea(String), sid-51A06B9C-47F5-4DB8-959A-A00E541D336B(String), null, null, null, sequenceFlow(String), null, 2022-04-19 11:31:26.255(Timestamp), 2022-04-19 11:31:26.255(Timestamp), 0(Long), null, (String), 317c5e0d-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String), 317c5e0e-bf91-11ec-9b6a-2615652caa30(String), null, 并行会签审批(String), userTask(String), 并行会签审批人1(String), 2022-04-19 11:31:26.304(Timestamp), null, null, null, (String), 31833be3-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String), 31833be4-bf91-11ec-9b6a-2615652caa30(String), null, 并行会签审批(String), userTask(String), 并行会签审批人2(String), 2022-04-19 11:31:26.349(Timestamp), null, null, null, (String), 318362f9-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String), 318362fa-bf91-11ec-9b6a-2615652caa30(String), null, 并行会签审批(String), userTask(String), 并行会签审批人3(String), 2022-04-19 11:31:26.35(Timestamp), null, null, null, (String), 318362ff-bf91-11ec-9b6a-2615652caa30(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String), 31838a10-bf91-11ec-9b6a-2615652caa30(String), null, 并行会签审批(String), userTask(String), 并行会签审批人4(String), 2022-04-19 11:31:26.35(Timestamp), null, null, null, (String)
     * Updates: 5
     *
     * Preparing: INSERT INTO ACT_RU_TASK (ID_, REV_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, PRIORITY_, CREATE_TIME_, OWNER_, ASSIGNEE_, DELEGATION_, EXECUTION_ID_, PROC_INST_ID_, PROC_DEF_ID_, TASK_DEF_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, SCOPE_DEFINITION_ID_, TASK_DEF_KEY_, DUE_DATE_, CATEGORY_, SUSPENSION_STATE_, TENANT_ID_, FORM_KEY_, CLAIM_TIME_, IS_COUNT_ENABLED_, VAR_COUNT_, ID_LINK_COUNT_, SUB_TASK_COUNT_) VALUES (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 317c5e0e-bf91-11ec-9b6a-2615652caa30(String), 并行会签审批(String), null, null, 50(Integer), 2022-04-19 11:31:26.304(Timestamp), null, 并行会签审批人1(String), null, 317be9cf-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), null, null, null, null, null, hq_sp_parallel(String), null, null, 1(Integer), (String), null, null, true(Boolean), 0(Integer), 0(Integer), 0(Integer), 31833be4-bf91-11ec-9b6a-2615652caa30(String), 并行会签审批(String), null, null, 50(Integer), 2022-04-19 11:31:26.349(Timestamp), null, 并行会签审批人2(String), null, 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), null, null, null, null, null, hq_sp_parallel(String), null, null, 1(Integer), (String), null, null, true(Boolean), 0(Integer), 0(Integer), 0(Integer), 318362fa-bf91-11ec-9b6a-2615652caa30(String), 并行会签审批(String), null, null, 50(Integer), 2022-04-19 11:31:26.35(Timestamp), null, 并行会签审批人3(String), null, 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), null, null, null, null, null, hq_sp_parallel(String), null, null, 1(Integer), (String), null, null, true(Boolean), 0(Integer), 0(Integer), 0(Integer), 31838a10-bf91-11ec-9b6a-2615652caa30(String), 并行会签审批(String), null, null, 50(Integer), 2022-04-19 11:31:26.35(Timestamp), null, 并行会签审批人4(String), null, 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), null, null, null, null, null, hq_sp_parallel(String), null, null, 1(Integer), (String), null, null, true(Boolean), 0(Integer), 0(Integer), 0(Integer)
     * Updates: 4
     *
     * Preparing: insert into ACT_GE_BYTEARRAY(ID_, REV_, NAME_, BYTES_, DEPLOYMENT_ID_) values (?, 1, ?, ?, ?) , (?, 1, ?, ?, ?)
     * Parameters: 315045e2-bf91-11ec-9b6a-2615652caa30(String), var-parallelList(String), java.io.ByteArrayInputStream@19827608(ByteArrayInputStream), null, 31509404-bf91-11ec-9b6a-2615652caa30(String), hist.var-parallelList(String), java.io.ByteArrayInputStream@cc9ef8d(ByteArrayInputStream), null
     * Updates: 2
     *
     * Preparing: insert into ACT_GE_BYTEARRAY(ID_, REV_, NAME_, BYTES_, DEPLOYMENT_ID_) values ( ?, 1, ?, ?, ? )
     * Parameters: 315798e5-bf91-11ec-9b6a-2615652caa30(String), hist.detail.var-parallelList(String), java.io.ByteArrayInputStream@c412556(ByteArrayInputStream), null
     * Updates: 1
     *
     * Preparing: INSERT INTO ACT_RU_VARIABLE (ID_, REV_, TYPE_, NAME_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) VALUES ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: 31506cf3-bf91-11ec-9b6a-2615652caa30(String), serializable(String), parallelList(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, null, null, null, 315045e2-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, 317be9c9-bf91-11ec-9b6a-2615652caa30(String), integer(String), nrOfInstances(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, 4(Long), 4(String), null, 317be9cb-bf91-11ec-9b6a-2615652caa30(String), integer(String), nrOfCompletedInstances(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, 0(Long), 0(String), null, 317be9cd-bf91-11ec-9b6a-2615652caa30(String), integer(String), nrOfActiveInstances(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, 4(Long), 4(String), null, 317c0fe3-bf91-11ec-9b6a-2615652caa30(String), string(String), parallel(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, null, 并行会签审批人1(String), null, 317c36f5-bf91-11ec-9b6a-2615652caa30(String), string(String), parallel(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, null, 并行会签审批人2(String), null, 317c36f7-bf91-11ec-9b6a-2615652caa30(String), string(String), parallel(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, null, 并行会签审批人3(String), null, 317c36f9-bf91-11ec-9b6a-2615652caa30(String), string(String), parallel(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, null, 并行会签审批人4(String), null, 317c5e0b-bf91-11ec-9b6a-2615652caa30(String), integer(String), loopCounter(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, 0(Long), 0(String), null, 31833be1-bf91-11ec-9b6a-2615652caa30(String), integer(String), loopCounter(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, 1(Long), 1(String), null, 318362f7-bf91-11ec-9b6a-2615652caa30(String), integer(String), loopCounter(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, 2(Long), 2(String), null, 318362fd-bf91-11ec-9b6a-2615652caa30(String), integer(String), loopCounter(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), null, null, null, null, null, null, 3(Long), 3(String), null
     * Updates: 12
     *
     * Preparing: update ACT_HI_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 3(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 11:31:26.204(Timestamp), 1787204(Long), 083fb7be-bf8d-11ec-abe8-4ecbd0e4f490(String), 2(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_HI_TASKINST SET REV_ = ?, END_TIME_ = ?, DURATION_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 3(Integer), 2022-04-19 11:31:26.152(Timestamp), 1787152(Long), 2022-04-19 11:31:26.152(Timestamp), 08402cef-bf8d-11ec-abe8-4ecbd0e4f490(String), 2(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 3(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 11:31:26.204(Timestamp), 1787204(Long), 083fb7be-bf8d-11ec-abe8-4ecbd0e4f490(String), 2(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_EXECUTION SET REV_ = ?, ACT_ID_ = ?, IS_ACTIVE_ = ?, TASK_COUNT_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 3(Integer), hq_sp_parallel(String), false(Boolean), 0(Integer), e031737c-bf87-11ec-b2be-26459411c9ea(String), 2(Integer)
     * Updates: 1
     *
     * Preparing: delete from ACT_RU_TASK where ID_ = ? and REV_ = ?
     * Parameters: 08402cef-bf8d-11ec-abe8-4ecbd0e4f490(String), 2(Integer)
     * Updates: 1
     *
     * Preparing: delete from ACT_RU_EXECUTION where ID_ = ? and REV_ = ?
     * Parameters: e031737c-bf87-11ec-b2be-26459411c9ea(String), 3(Integer)
     * Updates: 1
     */
    @Test
    public void completeTaskNode2() {
        String taskId = "08402cef-bf8d-11ec-abe8-4ecbd0e4f490";

        //这种写法是匿名内部类的写法，ArrayList 实现了序列化方式，但是这个匿名内部类没有实现序列化
        /*List<String> candidateUsers = new ArrayList<String>() {{
            add("并行会签审批人1");
            add("并行会签审批人2");
            add("并行会签审批人3");
            add("并行会签审批人4");
        }};*/

        List<String> candidateUsers = Arrays.asList("并行会签审批人1", "并行会签审批人2", "并行会签审批人3", "并行会签审批人4");

        Map<String, Object> variables = new HashMap<String, Object>() {{
            put("parallelList", candidateUsers);
        }};

        taskService.complete(taskId, variables);
    }


    /**
     * Step8：查询任务（节点3 并行会签）
     * <p>
     */
    @Test
    public void findTaskByUserIdNode3() {
        /*String userId = "并行会签审批人1";
        Task task = taskService.createTaskQuery().taskCandidateOrAssigned(userId).singleResult();
        System.out.println(task);*/

        String processInstanceId = "e0314c6b-bf87-11ec-b2be-26459411c9ea";
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        System.out.println(tasks);
    }

    /**
     * Step9：完成任务（节点3-1 并行会签）
     *
     * select * from ACT_RU_EXECUTION where ID_ = ?
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     *
     * Preparing: select * from ACT_HI_TASKINST where ID_ = ?
     * Parameters: 317c5e0e-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     *
     * Preparing: select distinct T.* from ACT_RU_TASK T where T.EXECUTION_ID_ = ?
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     *
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 2
     *
     * Preparing: select * from ACT_RU_EXECUTION where ID_ = ?
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 3
     *
     * Preparing: select * from ACT_RU_EXECUTION where ID_ = ?
     * Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * Total: 1
     *
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * Total: 2
     *
     * Preparing: select * from ACT_GE_BYTEARRAY where ID_ = ?
     * Parameters: 315045e2-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     *
     * Preparing: select * from ACT_RU_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String)
     * Total: 0
     *
     * Preparing: select * from ACT_HI_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String)
     * Total: 0
     *
     * Preparing: select * from ACT_HI_VARINST where ID_ = ?
     * Parameters: 317be9cb-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     *
     * Preparing: select * from ACT_HI_VARINST where ID_ = ?
     * Parameters: 317be9cd-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     *
     * Preparing: select * from ACT_RU_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String)
     * Total: 1
     *
     * Preparing: select * from ACT_HI_ACTINST where ID_ =?
     * Parameters: 317c5e0d-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     *
     * Preparing: insert into ACT_HI_DETAIL (ID_, TYPE_, PROC_INST_ID_, EXECUTION_ID_, ACT_INST_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, TIME_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: 56cbb32a-bfa2-11ec-b2d6-360799b22124(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, nrOfCompletedInstances(String), 1(Integer), integer(String), 2022-04-19 13:34:10.344(Timestamp), null, null, 1(Long), 1(String), null, 56d6fdcb-bfa2-11ec-b2d6-360799b22124(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, nrOfActiveInstances(String), 1(Integer), integer(String), 2022-04-19 13:34:10.418(Timestamp), null, null, 3(Long), 3(String), null
     * Updates: 2
     *
     * Preparing: update ACT_RU_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 13:34:10.491(Timestamp), 7364491(Long), 317c5e0d-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_HI_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 13:34:10.491(Timestamp), 7364491(Long), 317c5e0d-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_EXECUTION SET REV_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_EXECUTION SET REV_ = ?, IS_ACTIVE_ = ?, TASK_COUNT_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), false(Boolean), 0(Integer), 317be9cf-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_HI_TASKINST SET REV_ = ?, END_TIME_ = ?, DURATION_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), 2022-04-19 13:34:10.026(Timestamp), 7364026(Long), 2022-04-19 13:34:10.026(Timestamp), 317c5e0e-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_VARIABLE SET REV_ = ?, BYTEARRAY_ID_ = ?, LONG_ = ?, TEXT_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), null, 3(Long), 3(String), 317be9cd-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_RU_VARIABLE SET REV_ = ?, BYTEARRAY_ID_ = ?, LONG_ = ?, TEXT_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), null, 1(Long), 1(String), 317be9cb-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_HI_VARINST SET REV_ = ?, BYTEARRAY_ID_ = ?, LONG_ = ?, TEXT_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 1(Integer), null, 3(Long), 3(String), 2022-04-19 13:34:10.453(Timestamp), 317be9cd-bf91-11ec-9b6a-2615652caa30(String), 0(Integer)
     * Updates: 1
     *
     * Preparing: update ACT_HI_VARINST SET REV_ = ?, BYTEARRAY_ID_ = ?, LONG_ = ?, TEXT_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 1(Integer), null, 1(Long), 1(String), 2022-04-19 13:34:10.418(Timestamp), 317be9cb-bf91-11ec-9b6a-2615652caa30(String), 0(Integer)
     * Updates: 1
     *
     * Preparing: delete from ACT_RU_TASK where ID_ = ? and REV_ = ?
     * Parameters: 317c5e0e-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     */
    @Test
    public void completeTaskNode3And1() {
        String taskId = "317c5e0e-bf91-11ec-9b6a-2615652caa30";
        taskService.complete(taskId);
    }

    /**
     * Step10：完成任务（节点3-2 并行会签）
     *
     * select * from ACT_RU_EXECUTION where ID_ = ?
     * Parameters: 317c0fe0-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_HI_TASKINST where ID_ = ?
     * Parameters: 31833be4-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select distinct T.* from ACT_RU_TASK T where T.EXECUTION_ID_ = ?
     * Parameters: 317c0fe0-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: 317c0fe0-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 2
     * Preparing: select * from ACT_RU_EXECUTION where ID_ = ?
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 3
     * Preparing: select * from ACT_RU_EXECUTION where ID_ = ?
     * Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * Total: 1
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * Total: 2
     * Preparing: select * from ACT_GE_BYTEARRAY where ID_ = ?
     * Parameters: 315045e2-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String)
     * Total: 0
     * Preparing: select * from ACT_HI_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String)
     * Total: 0
     * Preparing: select * from ACT_HI_VARINST where ID_ = ?
     * Parameters: 317be9cb-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_HI_VARINST where ID_ = ?
     * Parameters: 317be9cd-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * Parameters: 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String)
     * Total: 1
     * Preparing: select * from ACT_HI_ACTINST where ID_ =?
     * Parameters: 31833be3-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_EXECUTION where PARENT_ID_ = ?
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 4
     * Preparing: select * from ACT_RU_EXECUTION where PARENT_ID_ = ?
     * Parameters: 317c0fe2-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_EXECUTION where PARENT_ID_ = ?
     * Parameters: 317c0fe0-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_EXECUTION where PARENT_ID_ = ?
     * Parameters: 317c0fe1-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_EXECUTION where PARENT_ID_ = ?
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_EXECUTION where SUPER_EXEC_ = ?
     * Parameters: 317c0fe2-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_EXECUTION where SUPER_EXEC_ = ?
     * Parameters: 317c0fe0-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_EXECUTION where SUPER_EXEC_ = ?
     * Parameters: 317c0fe1-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_EXECUTION where SUPER_EXEC_ = ?
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_EXECUTION where SUPER_EXEC_ = ?
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 0
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 2
     * Preparing: select * from ACT_RU_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * Parameters: 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String)
     * Total: 1
     * Preparing: select * from ACT_HI_ACTINST where ID_ =?
     * Parameters: 318362f9-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: 317c0fe1-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 2
     * Preparing: select distinct T.* from ACT_RU_TASK T where T.EXECUTION_ID_ = ?
     * Parameters: 317c0fe1-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_HI_TASKINST where ID_ = ?
     * Parameters: 318362fa-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * Parameters: 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), hq_sp_parallel(String)
     * Total: 1
     * Preparing: select * from ACT_HI_ACTINST where ID_ =?
     * Parameters: 318362ff-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * Parameters: 317c0fe2-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 2
     * Preparing: select distinct T.* from ACT_RU_TASK T where T.EXECUTION_ID_ = ?
     * Parameters: 317c0fe2-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_HI_TASKINST where ID_ = ?
     * Parameters: 31838a10-bf91-11ec-9b6a-2615652caa30(String)
     * Total: 1
     * Preparing: select * from ACT_RU_EXECUTION where PARENT_ID_ = ?
     * Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * Total: 1
     *
     * parent 'e0314c6b-bf87-11ec-b2be-26459411c9ea' created with parent e0314c6b-bf87-11ec-b2be-26459411c9ea
     * parent 'e0314c6b-bf87-11ec-b2be-26459411c9ea' created with parent e0314c6b-bf87-11ec-b2be-26459411c9ea
     * parent 'bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61' created with parent bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61
     *
     * Preparing: insert into ACT_HI_DETAIL (ID_, TYPE_, PROC_INST_ID_, EXECUTION_ID_, ACT_INST_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, TIME_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: bbad52c1-bfa5-11ec-94ce-4a90ec3e8e61(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, nrOfCompletedInstances(String), 2(Integer), integer(String), 2022-04-19 13:58:28.085(Timestamp), null, null, 2(Long), 2(String), null, bbf9c512-bfa5-11ec-94ce-4a90ec3e8e61(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 317b4d88-bf91-11ec-9b6a-2615652caa30(String), null, null, nrOfActiveInstances(String), 2(Integer), integer(String), 2022-04-19 13:58:28.586(Timestamp), null, null, 2(Long), 2(String), null, bc8b56b8-bfa5-11ec-94ce-4a90ec3e8e61(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, nrOfInstances(String), 0(Integer), integer(String), 2022-04-19 13:58:29.54(Timestamp), null, null, 3(Long), 3(String), null, bc8b56ba-bfa5-11ec-94ce-4a90ec3e8e61(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, nrOfCompletedInstances(String), 0(Integer), integer(String), 2022-04-19 13:58:29.54(Timestamp), null, null, 0(Long), 0(String), null, bc8b7dcc-bfa5-11ec-94ce-4a90ec3e8e61(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, nrOfActiveInstances(String), 0(Integer), integer(String), 2022-04-19 13:58:29.541(Timestamp), null, null, 1(Long), 1(String), null, bc8b7dce-bfa5-11ec-94ce-4a90ec3e8e61(String), VariableUpdate(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b56b6-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, loopCounter(String), 0(Integer), integer(String), 2022-04-19 13:58:29.541(Timestamp), null, null, 0(Long), 0(String), null
     * Updates: 6
     * Preparing: insert into ACT_HI_VARINST (ID_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, NAME_, REV_, VAR_TYPE_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_, CREATE_TIME_, LAST_UPDATED_TIME_) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: bc8b56b7-bfa5-11ec-94ce-4a90ec3e8e61(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, nrOfInstances(String), 0(Integer), integer(String), null, null, null, null, null, 3(Long), 3(String), null, 2022-04-19 13:58:29.54(Timestamp), 2022-04-19 13:58:29.54(Timestamp), bc8b56b9-bfa5-11ec-94ce-4a90ec3e8e61(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, nrOfCompletedInstances(String), 0(Integer), integer(String), null, null, null, null, null, 0(Long), 0(String), null, 2022-04-19 13:58:29.54(Timestamp), 2022-04-19 13:58:29.54(Timestamp), bc8b56bb-bfa5-11ec-94ce-4a90ec3e8e61(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, nrOfActiveInstances(String), 0(Integer), integer(String), null, null, null, null, null, 1(Long), 1(String), null, 2022-04-19 13:58:29.54(Timestamp), 2022-04-19 13:58:29.54(Timestamp), bc8b7dcd-bfa5-11ec-94ce-4a90ec3e8e61(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b56b6-bfa5-11ec-94ce-4a90ec3e8e61(String), null, loopCounter(String), 0(Integer), integer(String), null, null, null, null, null, 0(Long), 0(String), null, 2022-04-19 13:58:29.541(Timestamp), 2022-04-19 13:58:29.541(Timestamp)
     * Updates: 4
     * Preparing: insert into ACT_HI_TASKINST ( ID_, REV_, TASK_DEF_ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, SCOPE_DEFINITION_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, OWNER_, ASSIGNEE_, START_TIME_, CLAIM_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TASK_DEF_KEY_, FORM_KEY_, PRIORITY_, DUE_DATE_, CATEGORY_, TENANT_ID_, LAST_UPDATED_TIME_ ) values ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: bc8ba4e0-bfa5-11ec-94ce-4a90ec3e8e61(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b56b6-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, null, null, 串行会签审批(String), null, null, null, null, 2022-04-19 13:58:29.541(Timestamp), null, null, null, null, cx_hq_sequential(String), null, 50(Integer), null, null, (String), 2022-04-19 13:58:29.542(Timestamp)
     * Updates: 1
     * Preparing: insert into ACT_HI_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: bc8b2fa4-bfa5-11ec-94ce-4a90ec3e8e61(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8a6c53-bfa5-11ec-94ce-4a90ec3e8e61(String), sid-AFD6302C-B1CD-41EC-8151-E9E79D190286(String), null, null, null, sequenceFlow(String), null, 2022-04-19 13:58:29.539(Timestamp), 2022-04-19 13:58:29.539(Timestamp), 0(Long), null, (String), bc8b7dcf-bfa5-11ec-94ce-4a90ec3e8e61(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b56b6-bfa5-11ec-94ce-4a90ec3e8e61(String), cx_hq_sequential(String), bc8ba4e0-bfa5-11ec-94ce-4a90ec3e8e61(String), null, 串行会签审批(String), userTask(String), null, 2022-04-19 13:58:29.541(Timestamp), null, null, null, (String)
     * Updates: 2
     * Preparing: insert into ACT_RU_EXECUTION (ID_, REV_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, ACT_ID_, IS_ACTIVE_, IS_CONCURRENT_, IS_SCOPE_,IS_EVENT_SCOPE_, IS_MI_ROOT_, PARENT_ID_, SUPER_EXEC_, ROOT_PROC_INST_ID_, SUSPENSION_STATE_, TENANT_ID_, NAME_, START_ACT_ID_, START_TIME_, START_USER_ID_, IS_COUNT_ENABLED_, EVT_SUBSCR_COUNT_, TASK_COUNT_, JOB_COUNT_, TIMER_JOB_COUNT_, SUSP_JOB_COUNT_, DEADLETTER_JOB_COUNT_, VAR_COUNT_, ID_LINK_COUNT_, CALLBACK_ID_, CALLBACK_TYPE_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), cx_hq_sequential(String), false(Boolean), false(Boolean), false(Boolean), false(Boolean), true(Boolean), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, null, 2022-04-19 13:58:29.539(Timestamp), null, true(Boolean), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 3(Integer), 0(Integer), null, null, bc8b56b6-bfa5-11ec-94ce-4a90ec3e8e61(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), null, leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), cx_hq_sequential(String), true(Boolean), false(Boolean), false(Boolean), false(Boolean), false(Boolean), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer), (String), null, null, 2022-04-19 13:58:29.54(Timestamp), null, true(Boolean), 0(Integer), 1(Integer), 0(Integer), 0(Integer), 0(Integer), 0(Integer), 1(Integer), 0(Integer), null, null
     * Updates: 2
     * Preparing: insert into ACT_RU_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * Parameters: bc8b2fa4-bfa5-11ec-94ce-4a90ec3e8e61(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8a6c53-bfa5-11ec-94ce-4a90ec3e8e61(String), sid-AFD6302C-B1CD-41EC-8151-E9E79D190286(String), null, null, null, sequenceFlow(String), null, 2022-04-19 13:58:29.539(Timestamp), 2022-04-19 13:58:29.539(Timestamp), 0(Long), null, (String), bc8b7dcf-bfa5-11ec-94ce-4a90ec3e8e61(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b56b6-bfa5-11ec-94ce-4a90ec3e8e61(String), cx_hq_sequential(String), bc8ba4e0-bfa5-11ec-94ce-4a90ec3e8e61(String), null, 串行会签审批(String), userTask(String), null, 2022-04-19 13:58:29.541(Timestamp), null, null, null, (String)
     * Updates: 2
     * Preparing: insert into ACT_RU_TASK (ID_, REV_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, PRIORITY_, CREATE_TIME_, OWNER_, ASSIGNEE_, DELEGATION_, EXECUTION_ID_, PROC_INST_ID_, PROC_DEF_ID_, TASK_DEF_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, SCOPE_DEFINITION_ID_, TASK_DEF_KEY_, DUE_DATE_, CATEGORY_, SUSPENSION_STATE_, TENANT_ID_, FORM_KEY_, CLAIM_TIME_, IS_COUNT_ENABLED_, VAR_COUNT_, ID_LINK_COUNT_, SUB_TASK_COUNT_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: bc8ba4e0-bfa5-11ec-94ce-4a90ec3e8e61(String), 串行会签审批(String), null, null, 50(Integer), 2022-04-19 13:58:29.541(Timestamp), null, null, null, bc8b56b6-bfa5-11ec-94ce-4a90ec3e8e61(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), null, null, null, null, null, cx_hq_sequential(String), null, null, 1(Integer), (String), null, null, true(Boolean), 0(Integer), 3(Integer), 0(Integer)
     * Updates: 1
     * Preparing: INSERT INTO ACT_RU_VARIABLE (ID_, REV_, TYPE_, NAME_, PROC_INST_ID_, EXECUTION_ID_, TASK_ID_, SCOPE_ID_, SUB_SCOPE_ID_, SCOPE_TYPE_, BYTEARRAY_ID_, DOUBLE_, LONG_ , TEXT_, TEXT2_) VALUES ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
     * Parameters: bc8b56b7-bfa5-11ec-94ce-4a90ec3e8e61(String), integer(String), nrOfInstances(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, null, null, null, null, 3(Long), 3(String), null, bc8b56b9-bfa5-11ec-94ce-4a90ec3e8e61(String), integer(String), nrOfCompletedInstances(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, null, null, null, null, 0(Long), 0(String), null, bc8b56bb-bfa5-11ec-94ce-4a90ec3e8e61(String), integer(String), nrOfActiveInstances(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b2fa5-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, null, null, null, null, 1(Long), 1(String), null, bc8b7dcd-bfa5-11ec-94ce-4a90ec3e8e61(String), integer(String), loopCounter(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bc8b56b6-bfa5-11ec-94ce-4a90ec3e8e61(String), null, null, null, null, null, null, 0(Long), 0(String), null
     * Updates: 4
     * Preparing: update ACT_HI_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ?, DELETE_REASON_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 13:58:29.157(Timestamp), 8823157(Long), MI_END(String), 318362f9-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_HI_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 13:58:28.661(Timestamp), 8822661(Long), 31833be3-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_HI_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ?, DELETE_REASON_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 13:58:29.343(Timestamp), 8823343(Long), MI_END(String), 318362ff-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_HI_TASKINST SET REV_ = ?, END_TIME_ = ?, DURATION_ = ?, DELETE_REASON_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), 2022-04-19 13:58:29.306(Timestamp), 8823306(Long), MI_END(String), 2022-04-19 13:58:29.306(Timestamp), 318362fa-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_HI_TASKINST SET REV_ = ?, END_TIME_ = ?, DURATION_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), 2022-04-19 13:58:27.773(Timestamp), 8821773(Long), 2022-04-19 13:58:27.773(Timestamp), 31833be4-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_HI_TASKINST SET REV_ = ?, END_TIME_ = ?, DURATION_ = ?, DELETE_REASON_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), 2022-04-19 13:58:29.497(Timestamp), 8823497(Long), MI_END(String), 2022-04-19 13:58:29.497(Timestamp), 31838a10-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_RU_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ?, DELETE_REASON_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 13:58:29.157(Timestamp), 8823157(Long), MI_END(String), 318362f9-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_RU_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 13:58:28.661(Timestamp), 8822661(Long), 31833be3-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_RU_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ?, DELETE_REASON_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 13:58:29.343(Timestamp), 8823343(Long), MI_END(String), 318362ff-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_RU_EXECUTION SET REV_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 3(Integer), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 2(Integer)
     * Updates: 1
     * Preparing: update ACT_RU_EXECUTION SET REV_ = ?, IS_ACTIVE_ = ?, TASK_COUNT_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), false(Boolean), 0(Integer), 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_RU_EXECUTION SET REV_ = ?, IS_ACTIVE_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), false(Boolean), 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_RU_EXECUTION SET REV_ = ?, IS_ACTIVE_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), false(Boolean), 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_HI_VARINST SET REV_ = ?, BYTEARRAY_ID_ = ?, LONG_ = ?, TEXT_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), null, 2(Long), 2(String), 2022-04-19 13:58:28.625(Timestamp), 317be9cd-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: update ACT_HI_VARINST SET REV_ = ?, BYTEARRAY_ID_ = ?, LONG_ = ?, TEXT_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * Parameters: 2(Integer), null, 2(Long), 2(String), 2022-04-19 13:58:28.586(Timestamp), 317be9cb-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: delete from ACT_RU_VARIABLE where EXECUTION_ID_ = ?
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String)
     * Updates: 2
     * Preparing: delete from ACT_RU_VARIABLE where EXECUTION_ID_ = ?
     * Parameters: 317c0fe1-bf91-11ec-9b6a-2615652caa30(String)
     * Updates: 2
     * Preparing: delete from ACT_RU_VARIABLE where EXECUTION_ID_ = ?
     * Parameters: 317c0fe0-bf91-11ec-9b6a-2615652caa30(String)
     * Updates: 2
     * Preparing: delete from ACT_RU_VARIABLE where EXECUTION_ID_ = ?
     * Parameters: 317c0fe2-bf91-11ec-9b6a-2615652caa30(String)
     * Updates: 2
     * Preparing: delete from ACT_RU_VARIABLE where EXECUTION_ID_ = ?
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String)
     * Updates: 3
     * Preparing: delete from ACT_RU_TASK where ID_ = ? and REV_ = ?
     * Parameters: 31833be4-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     * Preparing: delete from ACT_RU_TASK where EXECUTION_ID_ = ?
     * Parameters: 317c0fe1-bf91-11ec-9b6a-2615652caa30(String)
     * Updates: 1
     * Preparing: delete from ACT_RU_TASK where EXECUTION_ID_ = ?
     * Parameters: 317c0fe2-bf91-11ec-9b6a-2615652caa30(String)
     * Updates: 1
     * Preparing: delete from ACT_RU_EXECUTION where ID_ = ? and REV_ = ?
     * Parameters: 317be9cf-bf91-11ec-9b6a-2615652caa30(String), 2(Integer)
     * Updates: 1
     * Preparing: delete from ACT_RU_EXECUTION where ID_ = ? and REV_ = ?
     * Parameters: 317c0fe1-bf91-11ec-9b6a-2615652caa30(String), 2(Integer)
     * Updates: 1
     * Preparing: delete from ACT_RU_EXECUTION where ID_ = ? and REV_ = ?
     * Parameters: 317c0fe0-bf91-11ec-9b6a-2615652caa30(String), 2(Integer)
     * Updates: 1
     * Preparing: delete from ACT_RU_EXECUTION where ID_ = ? and REV_ = ?
     * Parameters: 317c0fe2-bf91-11ec-9b6a-2615652caa30(String), 2(Integer)
     * Updates: 1
     * Preparing: delete from ACT_RU_EXECUTION where ID_ = ? and REV_ = ?
     * Parameters: 317b4d88-bf91-11ec-9b6a-2615652caa30(String), 1(Integer)
     * Updates: 1
     */
    @Test
    public void completeTaskNode3And2() {
        String taskId = "31833be4-bf91-11ec-9b6a-2615652caa30";
        taskService.complete(taskId);
    }

    /**
     * Step11：完成任务（节点4-1 串行会签）
     */
    @Test
    public void completeTaskNode4And1() {
        String taskId = "bc8ba4e0-bfa5-11ec-94ce-4a90ec3e8e61";
        taskService.complete(taskId);
    }

    /**
     * Step12：完成任务（节点4-2 串行会签）
     */
    @Test
    public void completeTaskNode4And2() {
        String taskId = "4a8d3209-bfac-11ec-b0bc-aaf288232d34";
        taskService.complete(taskId);
    }

    /**
     * Step13：完成任务（节点4-3 串行会签）
     */
    @Test
    public void completeTaskNode4And3() {
        String taskId = "8f29205a-bfac-11ec-9f9e-aaf288232d34";

        Map<String, Object> variables = new HashMap<String, Object>(){{
            put("leave_day", 5);
        }};
        taskService.complete(taskId, variables);
    }

    /**
     * Step15：完成任务（节点5 王经理审批）
     *
     * select * from ACT_RU_EXECUTION where ID_ = ?
     * 2022-04-19 14:57:18.412 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectExecution - ==> Parameters: bae2dd05-bfad-11ec-ba75-aaf288232d34(String)
     * 2022-04-19 14:57:18.448 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectExecution - <==      Total: 1
     * 2022-04-19 14:57:18.448 [DEBUG] org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntityImpl.selectHistoricTaskInstance - ==>  Preparing: select * from ACT_HI_TASKINST where ID_ = ?
     * 2022-04-19 14:57:18.448 [DEBUG] org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntityImpl.selectHistoricTaskInstance - ==> Parameters: bae797fa-bfad-11ec-ba75-aaf288232d34(String)
     * 2022-04-19 14:57:18.484 [DEBUG] org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntityImpl.selectHistoricTaskInstance - <==      Total: 1
     * 2022-04-19 14:57:18.486 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.selectTasksByExecutionId - ==>  Preparing: select distinct T.* from ACT_RU_TASK T where T.EXECUTION_ID_ = ?
     * 2022-04-19 14:57:18.487 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.selectTasksByExecutionId - ==> Parameters: bae2dd05-bfad-11ec-ba75-aaf288232d34(String)
     * 2022-04-19 14:57:18.522 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.selectTasksByExecutionId - <==      Total: 1
     * 2022-04-19 14:57:18.526 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.selectUnfinishedActivityInstanceExecutionIdAndActivityId - ==>  Preparing: select * from ACT_RU_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * 2022-04-19 14:57:18.526 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.selectUnfinishedActivityInstanceExecutionIdAndActivityId - ==> Parameters: bae2dd05-bfad-11ec-ba75-aaf288232d34(String), zjl_sp(String)
     * 2022-04-19 14:57:18.561 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.selectUnfinishedActivityInstanceExecutionIdAndActivityId - <==      Total: 1
     * 2022-04-19 14:57:18.562 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.selectHistoricActivityInstance - ==>  Preparing: select * from ACT_HI_ACTINST where ID_ =?
     * 2022-04-19 14:57:18.562 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.selectHistoricActivityInstance - ==> Parameters: bae797f9-bfad-11ec-ba75-aaf288232d34(String)
     * 2022-04-19 14:57:18.597 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.selectHistoricActivityInstance - <==      Total: 1
     * 2022-04-19 14:57:18.601 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.selectUnfinishedActivityInstanceExecutionIdAndActivityId - ==>  Preparing: select * from ACT_RU_ACTINST RES where EXECUTION_ID_ = ? and ACT_ID_ = ? and END_TIME_ is null
     * 2022-04-19 14:57:18.601 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.selectUnfinishedActivityInstanceExecutionIdAndActivityId - ==> Parameters: bae2dd05-bfad-11ec-ba75-aaf288232d34(String), sid-5D7C6F7B-38B2-4163-8BD7-EE4297ECA3C2(String)
     * 2022-04-19 14:57:18.638 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.selectUnfinishedActivityInstanceExecutionIdAndActivityId - <==      Total: 0
     * 2022-04-19 14:57:18.641 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectExecution - ==>  Preparing: select * from ACT_RU_EXECUTION where ID_ = ?
     * 2022-04-19 14:57:18.641 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectExecution - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:18.680 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectExecution - <==      Total: 1
     * 2022-04-19 14:57:18.681 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectExecutionsByParentExecutionId - ==>  Preparing: select * from ACT_RU_EXECUTION where PARENT_ID_ = ?
     * 2022-04-19 14:57:18.681 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectExecutionsByParentExecutionId - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:18.720 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectExecutionsByParentExecutionId - <==      Total: 1
     * 2022-04-19 14:57:18.721 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectChildExecutionsByProcessInstanceId - ==>  Preparing: select * from ACT_RU_EXECUTION where PROC_INST_ID_ = ? and PARENT_ID_ is not null
     * 2022-04-19 14:57:18.721 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectChildExecutionsByProcessInstanceId - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:18.759 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectChildExecutionsByProcessInstanceId - <==      Total: 1
     * 2022-04-19 14:57:18.759 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectSubProcessInstanceBySuperExecutionId - ==>  Preparing: select * from ACT_RU_EXECUTION where SUPER_EXEC_ = ?
     * 2022-04-19 14:57:18.759 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectSubProcessInstanceBySuperExecutionId - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:18.796 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.selectSubProcessInstanceBySuperExecutionId - <==      Total: 0
     * 2022-04-19 14:57:18.835 [DEBUG] org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntityImpl.selectVariablesByExecutionId - ==>  Preparing: select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     * 2022-04-19 14:57:18.835 [DEBUG] org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntityImpl.selectVariablesByExecutionId - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:18.875 [DEBUG] org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntityImpl.selectVariablesByExecutionId - <==      Total: 3
     * 2022-04-19 14:57:18.877 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.selectTasksByExecutionId - ==>  Preparing: select distinct T.* from ACT_RU_TASK T where T.EXECUTION_ID_ = ?
     * 2022-04-19 14:57:18.877 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.selectTasksByExecutionId - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:18.914 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.selectTasksByExecutionId - <==      Total: 0
     * 2022-04-19 14:57:19.067 [DEBUG] org.flowable.engine.impl.persistence.entity.EventSubscriptionEntityImpl.selectEventSubscriptionsByExecution - ==>  Preparing: select * from ACT_RU_EVENT_SUBSCR where (EXECUTION_ID_ = ?)
     * 2022-04-19 14:57:19.067 [DEBUG] org.flowable.engine.impl.persistence.entity.EventSubscriptionEntityImpl.selectEventSubscriptionsByExecution - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:19.104 [DEBUG] org.flowable.engine.impl.persistence.entity.EventSubscriptionEntityImpl.selectEventSubscriptionsByExecution - <==      Total: 0
     * 2022-04-19 14:57:19.106 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl.selectHistoricProcessInstance - ==>  Preparing: select * from ACT_HI_PROCINST where PROC_INST_ID_ = ?
     * 2022-04-19 14:57:19.106 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl.selectHistoricProcessInstance - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:19.144 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl.selectHistoricProcessInstance - <==      Total: 1
     * 2022-04-19 14:57:19.151 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.bulkInsertHistoricActivityInstance - ==>  Preparing: insert into ACT_HI_ACTINST ( ID_, REV_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     * 2022-04-19 14:57:19.154 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.bulkInsertHistoricActivityInstance - ==> Parameters: f40728b9-bfad-11ec-8025-aaf288232d34(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bae2dd05-bfad-11ec-ba75-aaf288232d34(String), sid-3D64DAEA-90AF-4AFC-946E-9DBE7C6AC63A(String), null, null, null, sequenceFlow(String), null, 2022-04-19 14:57:18.601(Timestamp), 2022-04-19 14:57:18.601(Timestamp), 0(Long), null, (String), f40d1c2a-bfad-11ec-8025-aaf288232d34(String), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), bae2dd05-bfad-11ec-ba75-aaf288232d34(String), sid-5D7C6F7B-38B2-4163-8BD7-EE4297ECA3C2(String), null, null, 结束(String), endEvent(String), null, 2022-04-19 14:57:18.639(Timestamp), 2022-04-19 14:57:18.681(Timestamp), 42(Long), null, (String)
     * 2022-04-19 14:57:19.189 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.bulkInsertHistoricActivityInstance - <==    Updates: 2
     * 2022-04-19 14:57:19.196 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.updateHistoricActivityInstance - ==>  Preparing: update ACT_HI_ACTINST SET REV_ = ?, PROC_DEF_ID_ = ?, END_TIME_ = ?, DURATION_ = ? where ID_ = ? and REV_ = ?
     * 2022-04-19 14:57:19.196 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.updateHistoricActivityInstance - ==> Parameters: 2(Integer), leave_flow:1:e644c3de-bf83-11ec-b5f2-b278c0660b00(String), 2022-04-19 14:57:18.561(Timestamp), 96561(Long), bae797f9-bfad-11ec-ba75-aaf288232d34(String), 1(Integer)
     * 2022-04-19 14:57:19.260 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl.updateHistoricActivityInstance - <==    Updates: 1
     * 2022-04-19 14:57:19.268 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.updateExecution - ==>  Preparing: update ACT_RU_EXECUTION SET REV_ = ?, IS_ACTIVE_ = ? where ID_ = ? and REV_ = ?
     * 2022-04-19 14:57:19.269 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.updateExecution - ==> Parameters: 4(Integer), false(Boolean), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 3(Integer)
     * 2022-04-19 14:57:19.304 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.updateExecution - <==    Updates: 1
     * 2022-04-19 14:57:19.305 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.updateExecution - ==>  Preparing: update ACT_RU_EXECUTION SET REV_ = ?, ACT_ID_ = ?, IS_ACTIVE_ = ?, TASK_COUNT_ = ? where ID_ = ? and REV_ = ?
     * 2022-04-19 14:57:19.306 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.updateExecution - ==> Parameters: 2(Integer), sid-5D7C6F7B-38B2-4163-8BD7-EE4297ECA3C2(String), false(Boolean), 0(Integer), bae2dd05-bfad-11ec-ba75-aaf288232d34(String), 1(Integer)
     * 2022-04-19 14:57:19.342 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.updateExecution - <==    Updates: 1
     * 2022-04-19 14:57:19.343 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl.updateHistoricProcessInstance - ==>  Preparing: update ACT_HI_PROCINST SET REV_ = ?, END_TIME_ = ?, DURATION_ = ?, END_ACT_ID_ = ? where ID_ = ? and REV_ = ?
     * 2022-04-19 14:57:19.344 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl.updateHistoricProcessInstance - ==> Parameters: 2(Integer), 2022-04-19 14:57:19.145(Timestamp), 16355145(Long), sid-5D7C6F7B-38B2-4163-8BD7-EE4297ECA3C2(String), e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 1(Integer)
     * 2022-04-19 14:57:19.892 [DEBUG] org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl.updateHistoricProcessInstance - <==    Updates: 1
     * 2022-04-19 14:57:19.899 [DEBUG] org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntityImpl.updateHistoricTaskInstance - ==>  Preparing: update ACT_HI_TASKINST SET REV_ = ?, END_TIME_ = ?, DURATION_ = ?, LAST_UPDATED_TIME_ = ? where ID_ = ? and REV_ = ?
     * 2022-04-19 14:57:19.900 [DEBUG] org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntityImpl.updateHistoricTaskInstance - ==> Parameters: 2(Integer), 2022-04-19 14:57:18.485(Timestamp), 96485(Long), 2022-04-19 14:57:18.485(Timestamp), bae797fa-bfad-11ec-ba75-aaf288232d34(String), 1(Integer)
     * 2022-04-19 14:57:19.937 [DEBUG] org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntityImpl.updateHistoricTaskInstance - <==    Updates: 1
     * 2022-04-19 14:57:19.938 [DEBUG] org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntityImpl.deleteVariableInstancesByExecutionId - ==>  Preparing: delete from ACT_RU_VARIABLE where EXECUTION_ID_ = ?
     * 2022-04-19 14:57:19.938 [DEBUG] org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntityImpl.deleteVariableInstancesByExecutionId - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:19.974 [DEBUG] org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntityImpl.deleteVariableInstancesByExecutionId - <==    Updates: 3
     * 2022-04-19 14:57:19.975 [DEBUG] org.flowable.engine.impl.persistence.entity.ByteArrayEntityImpl.deleteByteArrayNoRevisionCheck - ==>  Preparing: delete from ACT_GE_BYTEARRAY where ID_ = ?
     * 2022-04-19 14:57:19.976 [DEBUG] org.flowable.engine.impl.persistence.entity.ByteArrayEntityImpl.deleteByteArrayNoRevisionCheck - ==> Parameters: 315045e2-bf91-11ec-9b6a-2615652caa30(String)
     * 2022-04-19 14:57:20.517 [DEBUG] org.flowable.engine.impl.persistence.entity.ByteArrayEntityImpl.deleteByteArrayNoRevisionCheck - <==    Updates: 1
     * 2022-04-19 14:57:21.315 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.deleteTask - ==>  Preparing: delete from ACT_RU_TASK where ID_ = ? and REV_ = ?
     * 2022-04-19 14:57:21.316 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.deleteTask - ==> Parameters: bae797fa-bfad-11ec-ba75-aaf288232d34(String), 1(Integer)
     * 2022-04-19 14:57:21.351 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.deleteTask - <==    Updates: 1
     * 2022-04-19 14:57:21.352 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.deleteTasksByExecutionId - ==>  Preparing: delete from ACT_RU_TASK where EXECUTION_ID_ = ?
     * 2022-04-19 14:57:21.353 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.deleteTasksByExecutionId - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:21.387 [DEBUG] org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.deleteTasksByExecutionId - <==    Updates: 0
     * 2022-04-19 14:57:21.387 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.deleteActivityInstancesByProcessInstanceId - ==>  Preparing: delete from ACT_RU_ACTINST where PROC_INST_ID_ = ?
     * 2022-04-19 14:57:21.388 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.deleteActivityInstancesByProcessInstanceId - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String)
     * 2022-04-19 14:57:21.424 [DEBUG] org.flowable.engine.impl.persistence.entity.ActivityInstanceEntityImpl.deleteActivityInstancesByProcessInstanceId - <==    Updates: 18
     * 2022-04-19 14:57:21.424 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.deleteExecution - ==>  Preparing: delete from ACT_RU_EXECUTION where ID_ = ? and REV_ = ?
     * 2022-04-19 14:57:21.426 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.deleteExecution - ==> Parameters: bae2dd05-bfad-11ec-ba75-aaf288232d34(String), 2(Integer)
     * 2022-04-19 14:57:21.465 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.deleteExecution - <==    Updates: 1
     * 2022-04-19 14:57:21.466 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.deleteExecution - ==>  Preparing: delete from ACT_RU_EXECUTION where ID_ = ? and REV_ = ?
     * 2022-04-19 14:57:21.466 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.deleteExecution - ==> Parameters: e0314c6b-bf87-11ec-b2be-26459411c9ea(String), 4(Integer)
     * 2022-04-19 14:57:21.737 [DEBUG] org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl.deleteExecution - <==    Updates: 1
     */
    @Test
    public void completeTaskNode5() {
        String taskId = "bae797fa-bfad-11ec-ba75-aaf288232d34";
        taskService.complete(taskId);
    }

}
