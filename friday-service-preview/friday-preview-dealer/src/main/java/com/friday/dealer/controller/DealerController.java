package com.friday.dealer.controller;

import com.friday.dealer.pojo.dto.DealerDetailQueryDTO;
import com.friday.dealer.pojo.dto.DealerQueryDTO;
import com.friday.dealer.pojo.vo.MaintenanceDetailVO;
import com.friday.dealer.pojo.vo.DealerInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/7/18 10:40
 * @Description
 */
@RestController
@Api(tags = "经销商模块接口")
public class DealerController {

    @ApiOperation("1根据地理位置获取经销商列表")
    @PostMapping("/dealer/get-dealers-by-location")
    public List<DealerInfoVO> getDealersByLocation(@RequestBody @Valid DealerQueryDTO record) {
        return new ArrayList<>();
    }

    @ApiOperation("2获取指定经销商的维护详细信息")
    @PostMapping("/dealer/get-dealer-maintenance-detail")
    public MaintenanceDetailVO getDealerMaintenanceDetail(@RequestBody @Valid DealerDetailQueryDTO record) {
        return new MaintenanceDetailVO();
    }

}
