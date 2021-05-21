package com.liucan.kuroky.zk;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * 一.redis分布式锁
 * 相关资料https://www.cnblogs.com/linjiqin/p/8003838.html
 * 二.redis分布式锁需要注意的事项
 *  https://cloud.tencent.com/developer/article/1349732
 *  1.加锁
 *      正确：set（key, value, nx, px, time）
 *      错误：setnx和setex执行
 *      原因：没有保证2条指令的原子性，setnx后redis挂了，造成锁永远也解锁不了
 *  2.解锁
 *      正确：执行lua脚本语句，if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end
 *      错误：判断key的value和期望的比较相等后，然后在删除key
 *      原因：没有保证2条指令的原子性，在判断value相等后，刚好key过期然后其他地方获取到锁设置了新的value，然后导致删除了其他的锁
 */
@Slf4j
public class RedisDistributedLock {
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 尝试获取分布式锁
     *
     * @param jedis       Redis客户端
     * @param lockKey     锁
     * @param randomValue 请求随机值
     * @param expireTime  超期时间
     * @return 是否获取成功
     */
    public static boolean tryDistributedLock(Jedis jedis, String lockKey, String randomValue, int expireTime) {
        boolean bLock = false;
        try {
            String result = jedis.set(lockKey, randomValue, "nx", "px", expireTime);
            if (result.equals("ok")) {
                bLock = true;
            }
        } catch (Exception e) {
            log.error("[分布式锁]尝试获取分布式锁异常，lockKey：{},randomValue:{}", lockKey, randomValue, e);
        }
        return bLock;
    }

    /**
     * 获取分布式锁
     *
     * @param jedis       Redis客户端
     * @param lockKey     锁
     * @param randomValue 请求随机值
     * @param expireTime  超期时间
     * @return 是否获取成功
     */
    public static void DistributedLock(Jedis jedis, String lockKey, String randomValue, int expireTime) {
        while (!RedisDistributedLock.tryDistributedLock(jedis, lockKey, randomValue, expireTime)) ;
    }

    /**
     * 释放分布式锁
     *
     * @param jedis       Redis客户端
     * @param lockKey     锁
     * @param randomValue 请求标识
     * @return 是否释放成功
     */
    public static boolean DistributedUnlock(Jedis jedis, String lockKey, String randomValue) {
        boolean bResult = false;
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(randomValue));
        if (RELEASE_SUCCESS.equals(result)) {
            bResult = true;
        }
        return bResult;
    }
}
