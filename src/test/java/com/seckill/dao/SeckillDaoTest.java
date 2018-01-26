package com.seckill.dao;

import com.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合,junit启动时加载springIOC 容器
 */

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit  spring  配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryAll() throws Exception {

        //JAVA没有保留形参的记录

        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }

    }

    /**
     * Seckill{seckillId=1000, name='1000元秒杀iphone7', number=100, startTime=Wed Nov 01 00:00:00 CST 2017, endTime=Fri Dec 29 00:00:00 CST 2017, createTime=Sat Dec 30 21:42:25 CST 2017}
     * Seckill{seckillId=1001, name='1000元秒杀ipad2', number=200, startTime=Wed Nov 01 00:00:00 CST 2017, endTime=Fri Dec 29 00:00:00 CST 2017, createTime=Sat Dec 30 21:42:25 CST 2017}
     * Seckill{seckillId=1002, name='1000元秒杀小米6', number=400, startTime=Wed Nov 01 00:00:00 CST 2017, endTime=Fri Dec 29 00:00:00 CST 2017, createTime=Sat Dec 30 21:42:25 CST 2017}
     * Seckill{seckillId=1003, name='1000元秒杀红米note', number=600, startTime=Wed Nov 01 00:00:00 CST 2017, endTime=Fri Dec 29 00:00:00 CST 2017, createTime=Sat Dec 30 21:42:25 CST 2017}
     *
     * @throws Exception
     */

    @Test
    public void reduceNumber() throws Exception {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println(updateCount);
    }

    @Test
    public void queryById() throws Exception {


        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
        /**
         * 1000元秒杀iphone7
         Seckill{seckillId=1000, name='1000元秒杀iphone7', number=100, startTime=Wed Nov 01 00:00:00 CST 2017, endTime=Fri Dec 29 00:00:00 CST 2017, createTime=Sat Dec 30 21:42:25 CST 2017}

         */
    }


}


















