package com.liucan.kuroky.spring;

/**
 * 1.为何 SpringBoot 中 main 方法执行完毕后程序不会直接退出
 *  a.在所有非守护线程（用户线程）都退出的情况下，jvm会自动退出
 *  b.是在启动的时候额外启动一个非守护线程，该线程一直不退出直到被主动通知关闭
 *
 * 2.spring/spring mvc /spring boot/ spring cloud里面的父子容器
 *  a.子容器里面能够获取父容器里面bean，getBean里面会从父容器里面获取
 *  b.传统的spring mvc项目里面DispatchServlet里面有web子容器（包含了controller，service等），外面配置的为root
 *  c.spring boot 里面无父子容器，只有一个root容器
 *  d.spring cloud 里面有 Bootstrap 父容器，主要用于先于获取并初始化配置中心的context
 *  e.父子容器主要作用是资源隔离：https://www.jianshu.com/p/4f04edff5fdc
 *
 * @author liucan
 * @date 2021/9/16
 */
public class Boot {
}
