package com.liucan.kuroky.dubbo;

/**
 * dubbo spi
 * @author liucan
 * @date 5/22/21
 */
public interface Dubbo {
    /* *
     *
     *  37.rpc(远程过程调用)
     *      一.rpc和restful api区别
     *      参考：https://blog.csdn.net/wangyunpeng0319/article/details/78651998
     *          a.RPC 就像本地方法调用，RESTful API 每一次添加接口都可能需要额外地组织开放接口的数据
     *          b.RESTful API 在应用层使用 HTTP 协议,RPC 传输既可以使用 TCP/UDP，协议一般使用二制度编码，大大降低了数据的大小，减少流量消耗
     *          c.对接异构第三方服务时，通常使用 HTTP/RESTful 等公有协议，对于内部的服务调用，应用选择性能更高的二进制私有协议
     *      二.thrift
     *      参考：https://blog.csdn.net/zkp_java/article/details/81879577
     *          a.thrift是一个典型 的CS结构,支持跨语言,thrift通过IDL(Interface Description Language)来关联客户端和服务端
     *          b.thrift使用socket进行数据传输
     *
     *      三.Finagle
     *      参考：https://www.cnblogs.com/junneyang/p/5383627.html
     *      https://www.infoq.cn/article/2014/05/twitter-finagle-intro
     *
     *      四.dubbo
     *      参考：https://blog.csdn.net/u010664947/article/details/80007767
     *
     *      五.spring cloud
     *      参考：https://blog.csdn.net/valada/article/details/80892573
     *      https://www.cnblogs.com/ityouknow/p/7508306.html
     *
     *  38.jooq
     *      参考：https://www.breakyizhan.com/springboot/3369.html
     *
     *  39.秒杀
     *      参考：https://www.cnblogs.com/wangzhongqiu/p/6557596.html
     *      a.秒杀系统特点是并发量极大，但实际秒杀成功的请求数量却很少
     *      b.设计思路
     *          将请求拦截在系统上游，降低下游压力
     *          后端接口，必须能够支持高并发请求，必须尽可能“快”，在最短的时间里返回用户的请求结果
     *
     *  76.rpc框架
     *  http://youzhixueyuan.com/implementation-principle-of-rpc-framework.html
     *  我说了主要是协议栈+数据格式+序列化方式，然后需要有服务注册中心管理生产者和消费者。
     *
     *
     */
}
