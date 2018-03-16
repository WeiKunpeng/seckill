package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/*接口 要站在使用者的角度设计
 *三个方面：方法定义粒度，参数，返回类型 return类型要友好,还可以抛出异常
 * weikunpeng@cug.edu.cn
 * 2018/3/7 23:42
 */
public interface SeckillService {
    /**
     * 查询所有秒杀记录
     * @return
     */

    List<Seckill> getSeckillList();

    /**
     * 通过id查询一条记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);


    /**
     * 秒杀开启时输出秒杀接口地址 否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
    throws SeckillException,RepeatKillException,SeckillCloseException;














}
