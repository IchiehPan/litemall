package org.linlinjava.litemall.wx.web;

import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 商品服务
 */
@RestController
@RequestMapping("/wx/multimall")
@Validated
public class WxMultimallController {
    private final Log logger = LogFactory.getLog(WxMultimallController.class);

    @Autowired
    private LitemallMultimallService multimallService;

    /**
     * 获取商铺列表，用于切换商铺
     *
     * @return 商铺列表
     */
    @GetMapping("getAll")
    public Object getAll() {
        List<LitemallMultimall> multimallList = multimallService.getAll();
        if (multimallList == null) {
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.okList(multimallList);
    }
}