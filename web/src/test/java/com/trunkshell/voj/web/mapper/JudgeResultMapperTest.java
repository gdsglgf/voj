package com.trunkshell.voj.web.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.trunkshell.voj.web.model.JudgeResult;

/**
 * JudgeResultMapper测试类.
 * @author Xie Haozhe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration({"classpath:test-spring-context.xml"})
public class JudgeResultMapperTest {
    /**
     * 测试用例: 测试getJudgeResultUsingId(int)方法
     * 测试数据: Accept评测结果(JudgeResult)的评测结果组唯一标识符
     * 预期结果: 返回评测结果(JudgeResult)的评测结果组对象
     */
    @Test
    public void testGetJudgeResultUsingIdExists() {
        JudgeResult judgeResult = judgeResultMapper.getJudgeResultUsingId(2);
        Assert.assertNotNull(judgeResult);
        
        String judgeResultSlug = judgeResult.getJudgeResultSlug();
        Assert.assertEquals("AC", judgeResultSlug);
    }
    
    /**
     * 测试用例: 测试getJudgeResultUsingId(int)方法
     * 测试数据: 不存在的评测结果组唯一标识符
     * 预期结果: 返回空引用
     */
    @Test
    public void testGetJudgeResultUsingIdNotExists() {
        JudgeResult judgeResult = judgeResultMapper.getJudgeResultUsingId(0);
        Assert.assertNull(judgeResult);
    }
    
    /**
     * 测试用例: 测试getJudgeResultUsingSlug(String)方法
     * 测试数据: 普通评测结果(JudgeResult)的评测结果组唯一英文缩写
     * 预期结果: 返回评测结果(JudgeResult)的评测结果组对象
     */
    @Test
    public void testGetJudgeResultUsingSlugExists() {
        JudgeResult judgeResult = judgeResultMapper.getJudgeResultUsingSlug("AC");
        Assert.assertNotNull(judgeResult);
        
        int judgeResultId = judgeResult.getJudgeResultId();
        Assert.assertEquals(2, judgeResultId);
    }
    
    /**
     * 测试用例: 测试getJudgeResultUsingSlug(String)方法
     * 测试数据: 不存在的评测结果组唯一英文缩写
     * 预期结果: 返回空引用
     */
    @Test
    public void testGetJudgeResultUsingSlugNotExists() {
        JudgeResult judgeResult = judgeResultMapper.getJudgeResultUsingSlug("Not-Exists");
        Assert.assertNull(judgeResult);
    }
    
    /**
     * 待测试的JudgeResultMapper对象.
     */
    @Autowired
    private JudgeResultMapper judgeResultMapper;
}
