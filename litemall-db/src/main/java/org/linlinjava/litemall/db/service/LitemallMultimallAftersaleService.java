package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallAftersaleMapper;
import org.linlinjava.litemall.db.domain.LitemallAftersale;
import org.linlinjava.litemall.db.domain.LitemallAftersaleExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class LitemallMultimallAftersaleService {
    @Resource
    private LitemallAftersaleMapper aftersaleMapper;

    public LitemallAftersale findById(Integer id) {
        return aftersaleMapper.selectByPrimaryKey(id);
    }

    public LitemallAftersale findById(Integer userId, Integer id) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        example.or().andIdEqualTo(id).andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return aftersaleMapper.selectOneByExample(example);
    }

    public List<LitemallAftersale> queryList(Integer userId, Short status, Integer page, Integer limit, String sort, String order) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        LitemallAftersaleExample.Criteria criteria = example.or();
        criteria.andUserIdEqualTo(userId);
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        } else {
            example.setOrderByClause(LitemallAftersale.Column.addTime.desc());
        }

        PageHelper.startPage(page, limit);
        return aftersaleMapper.selectByExample(example);
    }

    public List<LitemallAftersale> querySelective(Integer orderId, String aftersaleSn, Short status, Integer mallId, Integer page, Integer limit, String sort, String order) {
        LitemallAftersaleExample example = new LitemallAftersaleExample();
        LitemallAftersaleExample.Criteria criteria = example.or();
        if (orderId != null) {
            criteria.andOrderIdEqualTo(orderId);
        }
        if (!StringUtils.isEmpty(aftersaleSn)) {
            criteria.andAftersaleSnEqualTo(aftersaleSn);
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        if (mallId != null) {
            criteria.andMallIdEqualTo(mallId);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        } else {
            example.setOrderByClause(LitemallAftersale.Column.addTime.desc());
        }

        PageHelper.startPage(page, limit);
        return aftersaleMapper.selectByExample(example);
    }

    public void add(LitemallAftersale aftersale) {
        aftersale.setAddTime(LocalDateTime.now());
        aftersale.setUpdateTime(LocalDateTime.now());
        aftersaleMapper.insertSelective(aftersale);
    }

    public void deleteById(Integer id) {
        aftersaleMapper.logicalDeleteByPrimaryKey(id);
    }

    public void updateById(LitemallAftersale aftersale) {
        aftersale.setUpdateTime(LocalDateTime.now());
        aftersaleMapper.updateByPrimaryKeySelective(aftersale);
    }
}
