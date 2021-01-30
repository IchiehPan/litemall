package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallCategoryMapper;
import org.linlinjava.litemall.db.domain.LitemallCategory;
import org.linlinjava.litemall.db.domain.LitemallCategoryExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallMultimallCategoryService {
    @Resource
    private LitemallCategoryMapper categoryMapper;
    private LitemallCategory.Column[] CHANNEL = {LitemallCategory.Column.id, LitemallCategory.Column.name, LitemallCategory.Column.iconUrl};

    public List<LitemallCategory> queryL1(Integer mallId) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);

        if (mallId != null) {
            criteria.andMallIdEqualTo(mallId);
        }

        return categoryMapper.selectByExample(example);
    }

    public List<LitemallCategory> queryByPid(Integer pid, Integer mallId) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.or().andPidEqualTo(pid).andDeletedEqualTo(false);
        if (mallId != null) {
            criteria.andMallIdEqualTo(mallId);
        }

        return categoryMapper.selectByExample(example);
    }

    public LitemallCategory findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    public List<LitemallCategory> querySelective(String id, String name, Integer page, Integer size, String sort, String order) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(id)) {
            criteria.andIdEqualTo(Integer.valueOf(id));
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return categoryMapper.selectByExample(example);
    }

    public int updateById(LitemallCategory category) {
        category.setUpdateTime(LocalDateTime.now());
        return categoryMapper.updateByPrimaryKeySelective(category);
    }

    public void deleteById(Integer id) {
        categoryMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallCategory category) {
        category.setAddTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insertSelective(category);
    }

    public List<LitemallCategory> queryChannel(Integer mallId) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
        if (mallId != null) {
            criteria.andMallIdEqualTo(mallId);
        }
        return categoryMapper.selectByExampleSelective(example, CHANNEL);
    }

    public List<LitemallCategory> queryL1WithoutRecommend(Integer mallId, int offset, int limit) {
        LitemallCategoryExample example = new LitemallCategoryExample();
        LitemallCategoryExample.Criteria criteria = example.or().andLevelEqualTo("L1").andNameNotEqualTo("推荐").andDeletedEqualTo(false);

        if (mallId != null) {
            criteria.andMallIdEqualTo(mallId);
        }

        PageHelper.startPage(offset, limit);
        return categoryMapper.selectByExample(example);
    }
}
