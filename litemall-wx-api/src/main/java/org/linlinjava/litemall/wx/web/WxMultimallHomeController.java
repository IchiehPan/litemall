package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.domain.LitemallGoods;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.HomeCacheManager;
import org.linlinjava.litemall.wx.service.WxGrouponRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 首页服务
 */
@RestController
@RequestMapping("/wx/multimall/home")
@Validated
public class WxMultimallHomeController {
    private final Log logger = LogFactory.getLog(WxMultimallHomeController.class);

    @Autowired
    private LitemallMultimallAdService adService;

    @Autowired
    private LitemallMultimallGoodsService goodsService;

    @Autowired
    private LitemallMultimallBrandService brandService;

    @Autowired
    private LitemallMultimallTopicService topicService;

    @Autowired
    private LitemallMultimallCategoryService categoryService;

    @Autowired
    private WxGrouponRuleService grouponService;

    @Autowired
    private LitemallMultimallCouponService couponService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    @GetMapping("/cache")
    public Object cache(@NotNull String key) {
        if (!key.equals("litemall_cache")) {
            return ResponseUtil.fail();
        }

        // 清除缓存
        HomeCacheManager.clearAll();
        return ResponseUtil.ok("缓存已清除");
    }

    /**
     * 首页数据
     *
     * @param userId 当用户已经登录时，非空。为登录状态为null
     * @return 首页数据
     */
    @GetMapping("/index")
    public Object index(@LoginUser Integer userId, @NotNull Integer mallId) {
        String homeCacheIndex = HomeCacheManager.INDEX + ", " + mallId;

        //优先从缓存中读取
        if (HomeCacheManager.hasData(homeCacheIndex)) {
            return ResponseUtil.ok(HomeCacheManager.getCacheData(homeCacheIndex));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Callable<List> bannerListCallable = () -> adService.queryIndex(mallId);

        Callable<List> channelListCallable = () -> categoryService.queryChannel(mallId);

        Callable<List> couponListCallable;
        if (userId == null) {
            couponListCallable = () -> couponService.queryList(mallId, 0, 3);
        } else {
            couponListCallable = () -> couponService.queryAvailableList(userId, mallId, 0, 3);
        }


        Callable<List> newGoodsListCallable = () -> goodsService.queryByNew(mallId, 0, SystemConfig.getNewLimit());

        Callable<List> hotGoodsListCallable = () -> goodsService.queryByHot(mallId, 0, SystemConfig.getHotLimit());

        Callable<List> brandListCallable = () -> brandService.query(mallId, 0, SystemConfig.getBrandLimit());

        Callable<List> topicListCallable = () -> topicService.queryList(mallId, 0, SystemConfig.getTopicLimit());

        //团购专区
        Callable<List> grouponListCallable = () -> grouponService.queryList(0, 5);

        Callable<List> floorGoodsListCallable = () -> this.getCategoryList(mallId);

        FutureTask<List> bannerTask = new FutureTask<>(bannerListCallable);
        FutureTask<List> channelTask = new FutureTask<>(channelListCallable);
        FutureTask<List> couponListTask = new FutureTask<>(couponListCallable);
        FutureTask<List> newGoodsListTask = new FutureTask<>(newGoodsListCallable);
        FutureTask<List> hotGoodsListTask = new FutureTask<>(hotGoodsListCallable);
        FutureTask<List> brandListTask = new FutureTask<>(brandListCallable);
        FutureTask<List> topicListTask = new FutureTask<>(topicListCallable);
        FutureTask<List> grouponListTask = new FutureTask<>(grouponListCallable);
        FutureTask<List> floorGoodsListTask = new FutureTask<>(floorGoodsListCallable);

        executorService.submit(bannerTask);
        executorService.submit(channelTask);
        executorService.submit(couponListTask);
        executorService.submit(newGoodsListTask);
        executorService.submit(hotGoodsListTask);
        executorService.submit(brandListTask);
        executorService.submit(topicListTask);
        executorService.submit(grouponListTask);
        executorService.submit(floorGoodsListTask);

        Map<String, Object> entity = new HashMap<>();
        try {
            entity.put("banner", bannerTask.get());
            entity.put("channel", channelTask.get());
            entity.put("couponList", couponListTask.get());
            entity.put("newGoodsList", newGoodsListTask.get());
            entity.put("hotGoodsList", hotGoodsListTask.get());
            entity.put("brandList", brandListTask.get());
            entity.put("topicList", topicListTask.get());
            entity.put("grouponList", grouponListTask.get());
            entity.put("floorGoodsList", floorGoodsListTask.get());
            //缓存数据
            HomeCacheManager.loadData(homeCacheIndex, entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
        return ResponseUtil.ok(entity);
    }

    private List<Map> getCategoryList(Integer mallId) {
        List<Map> categoryList = new ArrayList<>();
        List<LitemallCategory> catL1List = categoryService.queryL1WithoutRecommend(mallId, 0, SystemConfig.getCatlogListLimit());
        for (LitemallCategory catL1 : catL1List) {
            List<LitemallCategory> catL2List = categoryService.queryByPid(catL1.getId(), mallId);
            List<Integer> l2List = new ArrayList<>();
            for (LitemallCategory catL2 : catL2List) {
                l2List.add(catL2.getId());
            }

            List<LitemallGoods> categoryGoods;
            if (l2List.size() == 0) {
                categoryGoods = new ArrayList<>();
            } else {
                categoryGoods = goodsService.queryByCategory(l2List, 0, SystemConfig.getCatlogMoreLimit());
            }

            Map<String, Object> catGoods = new HashMap<>();
            catGoods.put("id", catL1.getId());
            catGoods.put("name", catL1.getName());
            catGoods.put("goodsList", categoryGoods);
            categoryList.add(catGoods);
        }
        return categoryList;
    }

    /**
     * 商城介绍信息
     *
     * @return 商城介绍信息
     */
    @GetMapping("/about")
    public Object about() {
        Map<String, Object> about = new HashMap<>();
        about.put("name", SystemConfig.getMallName());
        about.put("address", SystemConfig.getMallAddress());
        about.put("phone", SystemConfig.getMallPhone());
        about.put("qq", SystemConfig.getMallQQ());
        about.put("longitude", SystemConfig.getMallLongitude());
        about.put("latitude", SystemConfig.getMallLatitude());
        return ResponseUtil.ok(about);
    }
}