package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallBrand;
import org.linlinjava.litemall.db.service.LitemallBrandService;
import org.linlinjava.litemall.db.service.LitemallMultimallBrandService;
import org.linlinjava.litemall.db.service.LitemallMultimallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.ADMIN_INVALID_MALL;

@RestController
@RequestMapping("/admin/multimall/brand")
@Validated
public class AdminMultimallBrandController {
    private final Log logger = LogFactory.getLog(AdminMultimallBrandController.class);

    @Autowired
    private LitemallMultimallBrandService brandService;
    @Autowired
    private LitemallMultimallService multimallService;

    @RequiresPermissions("admin:multimallbrand:list")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "品牌管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String id, String name,
                       @NotNull Integer mallId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallBrand> brandList = brandService.querySelective(id, name, mallId, page, limit, sort, order);
        return ResponseUtil.okList(brandList);
    }

    private Object validate(LitemallBrand brand) {
        String name = brand.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        String desc = brand.getDesc();
        if (StringUtils.isEmpty(desc)) {
            return ResponseUtil.badArgument();
        }

        BigDecimal price = brand.getFloorPrice();
        if (price == null) {
            return ResponseUtil.badArgument();
        }

        Integer mallId = brand.getMallId();
        if (mallId == null || multimallService.findById(mallId) == null) {
            return ResponseUtil.fail(ADMIN_INVALID_MALL, "所属商铺不存在");
        }
        return null;
    }

    @RequiresPermissions("admin:multimallbrand:create")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "品牌管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallBrand brand) {
        Object error = validate(brand);
        if (error != null) {
            return error;
        }
        brandService.add(brand);
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:multimallbrand:read")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "品牌管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallBrand brand = brandService.findById(id);
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:multimallbrand:update")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "品牌管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallBrand brand) {
        Object error = validate(brand);
        if (error != null) {
            return error;
        }
        if (brandService.updateById(brand) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(brand);
    }

    @RequiresPermissions("admin:multimallbrand:delete")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "品牌管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallBrand brand) {
        Integer id = brand.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        brandService.deleteById(id);
        return ResponseUtil.ok();
    }

}
