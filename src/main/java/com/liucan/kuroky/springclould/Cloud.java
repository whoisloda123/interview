package com.liucan.kuroky.springclould;

/**
 * eurka 和 zk的区别
 * @author liucan
 * @date 5/22/21
 */
public interface Cloud {
    /* *
     *
     *  77.spring如何解决循环依赖的？
     *  https://blog.csdn.net/lzhcoder/article/details/84144381
     *      a.场景：
     *          属性循环依赖：可以解决
     *          构造器依赖：不能解决
     *      b.如何检测：
     *          Bean在创建的时候可以给该Bean打标，如果递归调用回来发现正在创建中的话，即说明了循环依赖了。
     *      c.属性循环依赖如何解决
     *          1.单例对象创建过程：实例化->填充属性->初始化
     *          2.在创建A的时候会把A对应的ObjectFactory放到缓存中(早期引用，只实例化了，但没有填充属性)，在创建A的过程，发现依赖B，然后创建B，发现B
     *              里面依赖A，这个时候如果发现A正在创建中，会从缓存里面拿到创建好的A（没有填充属性）
     *          3.实例化完成后，会填充属性，初始化
     * 2.微服务注册中心
     * https://mp.weixin.qq.com/s/YDHk8uwN8VAkt8NXEs4lRg
     *
     * 3.could hystrix:https://zhuanlan.zhihu.com/p/150889569
     */
}
