package com.liucan.kuroky.netty;

/**
 * @author liucan
 * @date 5/22/21
 */
public interface Netty {
    /* *
     *
     *   52.nio,aio,bio的区别
     *     https://www.cnblogs.com/barrywxx/p/8430790.html
     *     https://blog.csdn.net/lisha006/article/details/82856906
     *      1.BIO:同步阻塞模式
     *      2.NIO：同步非阻塞模式
     *          a.有selector，channel（通道：可写可读），buffer（载体）
     *          b.采用IO多路复用模型，epoll模型代替select模型
     *              select模型：采用轮寻方式，轮寻到有准备好的io后，返回进行后续操作
     *              epoll模型：采用注册通知方式，当有准备好的io后，会回调通知相应的处理
     *      3.AIO:异步非阻塞模式
     *          不需要轮询等待，而是通过操作系统处理后，会自动通知服务处理相应的请求，相对于轮训操作交给操作系统
     */
}
