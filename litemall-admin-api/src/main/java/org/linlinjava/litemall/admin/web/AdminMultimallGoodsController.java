package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.dto.GoodsAllinone;
import org.linlinjava.litemall.admin.service.AdminGoodsService;
import org.linlinjava.litemall.admin.service.AdminMultimallGoodsService;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/multimall/goods")
@Validated
public class AdminMultimallGoodsController {
    private final Log logger = LogFactory.getLog(AdminMultimallGoodsController.class);

    @Autowired
    private AdminMultimallGoodsService multimallGoodsService;

    /**
     * 查询商品
     *
     * @param goodsId
     * @param goodsSn
     * @param name
     * @param mallId  商户ID
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:multimallgoods:list")
    @RequiresPermissionsDesc(menu = {"商品管理（多商户）", "商品管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer goodsId, String goodsSn, String name,
                       @NotNull Integer mallId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        return multimallGoodsService.list(goodsId, goodsSn, name, mallId, page, limit, sort, order);
    }

    @GetMapping("/catAndBrand")
    public Object list2(@NotNull Integer mallId) {
        return multimallGoodsService.list2(mallId);
    }

    /**
     * 编辑商品
     *
     * @param goodsAllinone
     * @return
     */
    @RequiresPermissions("admin:multimallgoods:update")
    @RequiresPermissionsDesc(menu = {"商品管理（多商户）", "商品管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody GoodsAllinone goodsAllinone) {
        return multimallGoodsService.update(goodsAllinone);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
    @RequiresPermissions("admin:multimallgoods:delete")
    @RequiresPermissionsDesc(menu = {"商品管理（多商户）", "商品管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallGoods goods) {
        return multimallGoodsService.delete(goods);
    }

    /**
     * 添加商品
     *
     * @param goodsAllinone
     * @return
     */
    @RequiresPermissions("admin:multimallgoods:create")
    @RequiresPermissionsDesc(menu = {"商品管理（多商户）", "商品管理"}, button = "上架")
    @PostMapping("/create")
    public Object create(@RequestBody GoodsAllinone goodsAllinone) {
        return multimallGoodsService.create(goodsAllinone);
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:multimallgoods:read")
    @RequiresPermissionsDesc(menu = {"商品管理（多商户）", "商品管理"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return multimallGoodsService.detail(id);

    }

}
