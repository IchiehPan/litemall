package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.dto.GoodsAllinone;
import org.linlinjava.litemall.admin.service.AdminGoodsService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallBrand;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.domain.LitemallMultimall;
import org.linlinjava.litemall.db.service.LitemallMultimallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/multimall")
@Validated
public class AdminMultimallController {
    private final Log logger = LogFactory.getLog(AdminMultimallController.class);

    @Autowired
    private LitemallMultimallService multimallService;

    /**
     * 查询店铺
     *
     * @param name
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:multimall:list")
    @RequiresPermissionsDesc(menu = {"店铺管理（多商户）", "店铺管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallMultimall> multimallList = multimallService.querySelective(name, page, limit, sort, order);
        return ResponseUtil.okList(multimallList);
    }

    /**
     * 编辑店铺
     *
     * @param multimall
     * @return
     */
    @RequiresPermissions("admin:multimall:update")
    @RequiresPermissionsDesc(menu = {"店铺管理（多商户）", "店铺管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallMultimall multimall) {
        Object error = validate(multimall);
        if (error != null) {
            return error;
        }
        if (multimallService.updateById(multimall) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(multimall);
    }

    /**
     * 删除店铺
     *
     * @param multimall
     * @return
     */
    @RequiresPermissions("admin:multimall:delete")
    @RequiresPermissionsDesc(menu = {"店铺管理（多商户）", "店铺管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallMultimall multimall) {
        Integer id = multimall.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        multimallService.deleteById(multimall.getId());
        return ResponseUtil.ok();
    }

    /**
     * 添加商品
     *
     * @param multimall
     * @return
     */
    @RequiresPermissions("admin:goods:create")
    @RequiresPermissionsDesc(menu = {"店铺管理（多商户）", "店铺管理"}, button = "上架")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallMultimall multimall) {
        Object error = validate(multimall);
        if (error != null) {
            return error;
        }
        multimallService.add(multimall);
        return ResponseUtil.ok(multimall);
    }

    /**
     * 店铺详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:multimall:read")
    @RequiresPermissionsDesc(menu = {"店铺管理（多商户）", "店铺管理"}, button = "详情")
    @GetMapping("/read")
    public Object detail(@NotNull Integer id) {
        LitemallMultimall multimall = multimallService.findById(id);
        return ResponseUtil.ok(multimall);
    }

    private Object validate(LitemallMultimall multimall) {
        String name = multimall.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        String detail = multimall.getDetail();
        if (StringUtils.isEmpty(detail)) {
            return ResponseUtil.badArgument();
        }

        return null;
    }

}
