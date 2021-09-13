package com.liucan.kuroky.spring;

/**
 * 一.容器启动
 *  1.springmvc和boot继承tomcat的ServletContainerInitializer（容器启动会调用该接口），在onStartup方法里面往servlet context里面添加
 *      a.servlet
 *      b.filter
 *      c.初始化参数等
 *
 * 二.mvc关键类和相关配置
 *  1.概念
 *      a.Filter
 *      b.HandlerInterceptor
 *      c.HandlerMethodArgumentResolver
 *      d.HandlerMethodReturnValueHandler
 *      e.HttpMessageConverter
 *      f.HandlerExceptionResolver
 *      g.RequestMappingHandlerMapping
 *      h.RequestMappingHandlerAdapter
 *      i.ServletInvocableHandlerMethod
 *      j.DispatcherServlet
 *      k.RequestBodyAdvice/ResponseBodyAdvice
 *  2·配置流程
 *
 * 二.servlet初始化（init方法）
 *  1.时机：容器初始化或者第一次收到请求（通过初始化参数配置）
 *  2.配置加载
 *      a.通过EnableWebMvc注解添加
 *  2.流程
 *      a.从servlet context里面通过key获取自定义的applicationContext，如果未获取到则创建并调用refresh来创建ioc，而spring boot则
 *          是用自定义的applicationContext
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
