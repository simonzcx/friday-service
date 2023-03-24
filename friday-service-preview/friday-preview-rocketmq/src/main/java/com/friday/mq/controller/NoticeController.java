package com.friday.mq.controller;

import com.friday.mq.dto.NotificationMsgCodeDTO;
import com.friday.mq.dto.NotificationMsgDTO;
import com.friday.mq.producer.ReserveNoticeProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/30 14:26
 * @Description
 */
@RestController
public class NoticeController {

    private static final String SERVICE_TYPE = "dealer";

    private static final String TYPE_RESERVE = "reserve";

    private static final String HU_RESERVE_MSG_CODE = "oemServiceId023_123456_796";

    private static final String APP_RESERVE_MSG_CODE = "oemServiceId023_123456_802";

    @Autowired
    private ReserveNoticeProducer reserveNoticeProducer;

    @GetMapping("/notice/reserve")
    public void reserveNotice() {
        List<String> deviceIds = new ArrayList<String>() {{
            add("LAVTDMER6PA000010");
        }};
        List<String> userIds = new ArrayList<String>() {{
            add("c56ed878-8ffd-4ed7-bdff-c3aa5225b251");
        }};
        List<String> codes = new ArrayList<String>() {{
            add("reserve-1664439986000");
        }};

        NotificationMsgDTO notificationMsgDTO = getInstance(TYPE_RESERVE);
        notificationMsgDTO.setMessageCode(new NotificationMsgCodeDTO() {{
            setHu(HU_RESERVE_MSG_CODE);
            setApp(APP_RESERVE_MSG_CODE);
        }});
        notificationMsgDTO.setDeviceIdList(deviceIds);
        notificationMsgDTO.setUserIdList(userIds);
        reserveNoticeProducer.notice(notificationMsgDTO, codes);
    }

    /**
     * 获取通知实体
     *
     * @param type 业务类型
     * @return 通知实体
     */
    private NotificationMsgDTO getInstance(String type) {
        return new NotificationMsgDTO() {{
            long currentTimeMillis = System.currentTimeMillis();
            setTimestamp(currentTimeMillis);
            setSendFailAndRetryCount(3);
            setNeedCompensator(Boolean.FALSE);
            setCompensatorCount(0);
            setServiceType(SERVICE_TYPE);
            setType(type);
            setRequestId(type + currentTimeMillis);
        }};
    }
}
