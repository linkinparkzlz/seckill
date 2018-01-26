package com.seckill.service.impl;


import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessKilledDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessKilled;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.exception.RepeateKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

//@Component   @ Service @Dao @Controller


@Service
public class SeckillServiceImpl implements SeckillService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SuccessKilledDao successKilledDao;

    //注入Service依赖
    @Autowired
    private SeckillDao seckillDao;

    //MD5盐值字符串，用于混淆MD5
    public final String slat = "bndnfdfdsnfbdfnsfdsf34jfkkfdskfd43fdksfkdsf,54nsafdsfmdsfds";


    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }


    //优化秒杀暴露接口

    @Override
    public Exposer exportSeckillUrl(long seckillId) {

        //优化点：缓存优化
        /**
         * get from cache
         * if null
         * get db
         * else
         *    put cache
         */
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //放入redis中
                redisDao.putSeckill(seckill);
            }

        }


        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();

        //系统当前时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());

        }

        //转化特定字符串
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);


    }


    private String getMd5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;

    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求/或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务。如只有一条修改操作，只读操作不需要事务控制。
     */
    public SeckillExecution excuteSeckill(long seckillId, long userPhone, String md5) throws SeckillException, SeckillCloseException, RepeateKillException {

        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckil  data rewrite");
        }

        Date nowTime = new Date();

        //执行秒杀业务逻辑： 减库存 + 记录购买行为
        int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
        try {

            if (updateCount <= 0) {
                throw new SeckillCloseException("seckill is closed");
            } else {
                //记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                //唯一； seckillId,userPhone
                if (insertCount <= 0) {
                    //重复秒杀
                    throw new RepeateKillException("seckill is repeate ");

                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, "秒杀成功", successKilled);
                }

            }


        } catch (SeckillCloseException e) {
            throw e;

        } catch (RepeateKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常  转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }

    }
}
