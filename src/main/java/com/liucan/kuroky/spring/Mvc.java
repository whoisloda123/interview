package com.liucan.kuroky.spring;

/**
 * 一.容器启动
 *  1.springmvc和boot继承tomcat的ServletContainerInitializer（容器启动会调用该接口），在onStartup方法里面往servlet context里面添加
 *      a.servlet
 *      b.filter
 *      c.初始化参数等
 *
 * 二.容器初始化
 *  1.容器初始化过程会调用servlet的init接口来初始化servlet
 *      a.
 *      b.
 *      c.
 *
 * 三.servlet.service方法流程（重点）
 *
 * 三.filter和HandlerInterceptor区别？
 *
 * @author liucan
 * @version 2021/9/12
 */
public class Mvc {
}
