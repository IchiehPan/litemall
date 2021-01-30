package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.vo.CategoryVo;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.service.LitemallCategoryService;
import org.linlinjava.litemall.db.service.LitemallMultimallCategoryService;
import org.linlinjava.litemall.db.service.LitemallMultimallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.linlinjava.litemall.admin.util.AdminResponseCode.ADMIN_INVALID_MALL;

@RestController
@RequestMapping("/admin/multimall/category")
@Validated
public class AdminMultimallCategoryController {
    private final Log logger = LogFactory.getLog(AdminMultimallCategoryController.class);

    @Autowired
    private LitemallMultimallCategoryService categoryService;
    @Autowired
    private LitemallMultimallService multimallService;

    @RequiresPermissions("admin:multimallcategory:list")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "类目管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(@NotNull Integer mallId) {
        List<CategoryVo> categoryVoList = new ArrayList<>();

        List<LitemallCategory> categoryList = categoryService.queryByPid(0, mallId);
        for (LitemallCategory category : categoryList) {
            CategoryVo categoryVO = new CategoryVo();
            categoryVO.setId(category.getId());
            categoryVO.setDesc(category.getDesc());
            categoryVO.setIconUrl(category.getIconUrl());
            categoryVO.setPicUrl(category.getPicUrl());
            categoryVO.setKeywords(category.getKeywords());
            categoryVO.setName(category.getName());
            categoryVO.setLevel(category.getLevel());

            List<CategoryVo> children = new ArrayList<>();
            List<LitemallCategory> subCategoryList = categoryService.queryByPid(category.getId(), mallId);
            for (LitemallCategory subCategory : subCategoryList) {
                CategoryVo subCategoryVo = new CategoryVo();
                subCategoryVo.setId(subCategory.getId());
                subCategoryVo.setDesc(subCategory.getDesc());
                subCategoryVo.setIconUrl(subCategory.getIconUrl());
                subCategoryVo.setPicUrl(subCategory.getPicUrl());
                subCategoryVo.setKeywords(subCategory.getKeywords());
                subCategoryVo.setName(subCategory.getName());
                subCategoryVo.setLevel(subCategory.getLevel());

                children.add(subCategoryVo);
            }

            categoryVO.setChildren(children);
            categoryVoList.add(categoryVO);
        }

        return ResponseUtil.okList(categoryVoList);
    }

    private Object validate(LitemallCategory category) {
        String name = category.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        String level = category.getLevel();
        if (StringUtils.isEmpty(level)) {
            return ResponseUtil.badArgument();
        }
        if (!level.equals("L1") && !level.equals("L2")) {
            return ResponseUtil.badArgumentValue();
        }

        Integer pid = category.getPid();
        if (level.equals("L2") && (pid == null)) {
            return ResponseUtil.badArgument();
        }

        Integer mallId = category.getMallId();
        if (mallId == null || multimallService.findById(mallId) == null) {
            return ResponseUtil.fail(ADMIN_INVALID_MALL, "所属商铺不存在");
        }

        return null;
    }

    @RequiresPermissions("admin:multimallcategory:create")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "类目管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallCategory category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }
        categoryService.add(category);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:multimallcategory:read")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "类目管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallCategory category = categoryService.findById(id);
        return ResponseUtil.ok(category);
    }

    @RequiresPermissions("admin:multimallcategory:update")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "类目管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallCategory category) {
        Object error = validate(category);
        if (error != null) {
            return error;
        }

        if (categoryService.updateById(category) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:multimallcategory:delete")
    @RequiresPermissionsDesc(menu = {"商场管理（多商户）", "类目管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallCategory category) {
        Integer id = category.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        categoryService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:multimallcategory:list")
    @GetMapping("/l1")
    public Object catL1(@NotNull Integer mallId) {
        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1(mallId);
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        for (LitemallCategory category : l1CatList) {
            Map<String, Object> d = new HashMap<>(2);
            d.put("value", category.getId());
            d.put("label", category.getName());
            data.add(d);
        }
        return ResponseUtil.okList(data);
    }
}
