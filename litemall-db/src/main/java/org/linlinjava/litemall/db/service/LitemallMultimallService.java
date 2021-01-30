package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallMultimallMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.domain.LitemallMultimall.Column;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LitemallMultimallService {
    Column[] columns = new Column[]{Column.id, Column.name, Column.iconUrl, Column.detail,};
    @Resource
    private LitemallMultimallMapper multimallMapper;

    /**
     * 获取某个店铺信息
     *
     * @param id
     * @return
     */
    public LitemallMultimall findById(Integer id) {
        LitemallMultimallExample example = new LitemallMultimallExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return multimallMapper.selectOneByExampleWithBLOBs(example);
    }

    public List<LitemallMultimall> querySelective(String name, Integer offset, Integer limit, String sort, String order) {
        LitemallMultimallExample example = new LitemallMultimallExample();
        LitemallMultimallExample.Criteria criteria1 = example.or();

        if (!StringUtils.isEmpty(name)) {
            criteria1.andNameLike("%" + name + "%");
        }
        criteria1.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(offset, limit);

        return multimallMapper.selectByExampleSelective(example, columns);
    }

    public List<LitemallMultimall> getAll() {
        LitemallMultimallExample example = new LitemallMultimallExample();
        example.or().andDeletedEqualTo(false);
        return multimallMapper.selectByExample(example);
    }

    public int updateById(LitemallMultimall multimall) {
        multimall.setUpdateTime(LocalDateTime.now());
        return multimallMapper.updateByPrimaryKeySelective(multimall);
    }

    public void deleteById(Integer id) {
        multimallMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallMultimall multimall) {
        multimall.setAddTime(LocalDateTime.now());
        multimall.setUpdateTime(LocalDateTime.now());
        multimallMapper.insertSelective(multimall);
    }
}
