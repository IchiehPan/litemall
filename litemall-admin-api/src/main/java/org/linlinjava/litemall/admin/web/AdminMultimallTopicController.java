package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallTopic;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.db.service.LitemallMultimallService;
import org.linlinjava.litemall.db.service.LitemallMultimallTopicService;
import org.linlinjava.litemall.db.service.LitemallTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.ADMIN_INVALID_MALL;

@RestController
@RequestMapping("/admin/multimall/topic")
@Validated
public class AdminMultimallTopicController {
    private final Log logger = LogFactory.getLog(AdminMultimallTopicController.class);

    @Autowired
    private LitemallMultimallTopicService topicService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallMultimallService multimallService;

    @RequiresPermissions("admin:multimalltopic:list")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "专题管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String title, String subtitle,
                       @NotNull Integer mallId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort(accepts = {"id", "add_time", "price"}) @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallTopic> topicList = topicService.querySelective(title, subtitle, mallId, page, limit, sort, order);
        return ResponseUtil.okList(topicList);
    }

    private Object validate(LitemallTopic topic) {
        String title = topic.getTitle();
        if (StringUtils.isEmpty(title)) {
            return ResponseUtil.badArgument();
        }
        String content = topic.getContent();
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }
        BigDecimal price = topic.getPrice();
        if (price == null) {
            return ResponseUtil.badArgument();
        }
        Integer mallId = topic.getMallId();
        if (mallId == null || multimallService.findById(mallId) == null) {
            return ResponseUtil.fail(ADMIN_INVALID_MALL, "所属商铺不存在");
        }
        return null;
    }

    @RequiresPermissions("admin:multimalltopic:create")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "专题管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallTopic topic) {
        Object error = validate(topic);
        if (error != null) {
            return error;
        }
        topicService.add(topic);
        return ResponseUtil.ok(topic);
    }

    @RequiresPermissions("admin:multimalltopic:read")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "专题管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallTopic topic = topicService.findById(id);
        Integer[] goodsIds = topic.getGoods();
        List<LitemallGoods> goodsList = null;
        if (goodsIds == null || goodsIds.length == 0) {
            goodsList = new ArrayList<>();
        } else {
            goodsList = goodsService.queryByIds(goodsIds);
        }
        Map<String, Object> data = new HashMap<>(2);
        data.put("topic", topic);
        data.put("goodsList", goodsList);
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:multimalltopic:update")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "专题管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallTopic topic) {
        Object error = validate(topic);
        if (error != null) {
            return error;
        }
        if (topicService.updateById(topic) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(topic);
    }

    @RequiresPermissions("admin:multimalltopic:delete")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "专题管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallTopic topic) {
        topicService.deleteById(topic.getId());
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:multimalltopic:batch-delete")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "专题管理"}, button = "批量删除")
    @PostMapping("/batch-delete")
    public Object batchDelete(@RequestBody String body) {
        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");
        topicService.deleteByIds(ids);
        return ResponseUtil.ok();
    }
}
