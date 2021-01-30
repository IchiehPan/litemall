package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallComment;
import org.linlinjava.litemall.db.service.LitemallCommentService;
import org.linlinjava.litemall.db.service.LitemallMultimallCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/multimall/comment")
@Validated
public class AdminMultimallCommentController {
    private final Log logger = LogFactory.getLog(AdminMultimallCommentController.class);

    @Autowired
    private LitemallMultimallCommentService commentService;

    @RequiresPermissions("admin:multimallcomment:list")
    @RequiresPermissionsDesc(menu = {"商品管理（多商户）", "评论管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String userId, String valueId,
                       @NotNull Integer mallId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallComment> commentList = commentService.querySelective(userId, valueId, mallId, page, limit, sort, order);
        return ResponseUtil.okList(commentList);
    }

    @RequiresPermissions("admin:multimallcomment:delete")
    @RequiresPermissionsDesc(menu = {"商品管理（多商户）", "评论管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallComment comment) {
        Integer id = comment.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        commentService.deleteById(id);
        return ResponseUtil.ok();
    }

}
