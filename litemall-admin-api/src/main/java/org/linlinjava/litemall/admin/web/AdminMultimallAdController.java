package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.service.LitemallAdService;
import org.linlinjava.litemall.db.service.LitemallMultimallAdService;
import org.linlinjava.litemall.db.service.LitemallMultimallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.ADMIN_INVALID_MALL;

@RestController
@RequestMapping("/admin/multimall/ad")
@Validated
public class AdminMultimallAdController {
    private final Log logger = LogFactory.getLog(AdminMultimallAdController.class);

    @Autowired
    private LitemallMultimallAdService adService;
    @Autowired
    private LitemallMultimallService multimallService;

    @RequiresPermissions("admin:multimallad:list")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "广告管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String name, String content,
                       @NotNull Integer mallId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallAd> adList = adService.querySelective(name, content, mallId, page, limit, sort, order);
        return ResponseUtil.okList(adList);
    }

    private Object validate(LitemallAd ad) {
        String name = ad.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        String content = ad.getContent();
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }
        Integer mallId = ad.getMallId();
        if (mallId == null || multimallService.findById(mallId) == null) {
            return ResponseUtil.fail(ADMIN_INVALID_MALL, "所属商铺不存在");
        }
        return null;
    }

    @RequiresPermissions("admin:multimallad:create")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "广告管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallAd ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        adService.add(ad);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:multimallad:read")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "广告管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallAd ad = adService.findById(id);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:multimallad:update")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "广告管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallAd ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        if (adService.updateById(ad) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:multimallad:delete")
    @RequiresPermissionsDesc(menu = {"推广管理（多商户）", "广告管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAd ad) {
        Integer id = ad.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        adService.deleteById(id);
        return ResponseUtil.ok();
    }

}
