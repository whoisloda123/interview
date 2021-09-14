package com.liucan.kuroky.spring;

/**
 * 一.容器启动
 *  1.springmvc 和 boot 继承 tomcat 的 ServletContainerInitializer（容器启动会调用该接口），在 onStartup 方法里面往 servlet context 里面添加
 *      a.servlet
 *      b.filter
 *      c.初始化参数等
 *
 * 二.mvc关键类和相关配置
 *  1.概念:
 *      a.Filter
 *          tomcat 里面过滤所有请求，过滤了才会走到 servlet.service 方法
 *      b.HandlerInterceptor
 *          spring mvc 里面的过滤
 *      c.HandlerMethodArgumentResolver
 *          解析 controller 方法参数，如 RequestParamMapMethodArgumentResolver 处理 @RequestParam，RequestResponseBodyMethodProcessor
 *          处理 @RequestBody 和 @ResponseBody
 *      d.HandlerMethodReturnValueHandler
 *          处理 controller 方法返回值，如 RequestResponseBodyMethodProcessor 用来将返回值转换为json放入返回体
 *      e.HttpMessageConverter
 *          在 RequestResponseBodyMethodProcessor 里面调用，用来转换http请求体和返回体，如 GsonHttpMessageConverter 用来请求json的转换
 *      f.HandlerExceptionResolver
 *          1.处理 controller 方法异常，通过 @ControllerAdvice 和 @Exception
 *          2.将异常和方法保存map，然后通过异常找到对应处理方法，将其封装为 ServletInvocableHandlerMethod.invokeAndHandle 来进行调用
 *      g.RequestMappingHandlerMapping
 *          通过请求找到对应的 HandlerExecutionChain（HandlerInterceptor 和 HandlerMethod）
 *      h.RequestMappingHandlerAdapter
 *          通过 RequestMappingHandlerMapping 获取的 HandlerMethod 和请求封装成 ServletInvocableHandlerMethod 来进行调用
 *      i.ServletInvocableHandlerMethod
 *          1.真正调用 controller 方法的类，HandlerExceptionResolver 也会组装为该类
 *          2.invokeAndHandler 方法会调用 HandlerMethodArgumentResolver 设置参数， 调用真正方法， HandlerMethodReturnValueHandler 来
 *            处理返回值
 *      j.DispatcherServlet
 *          处理请求
 *      k.RequestBodyAdvice/ResponseBodyAdvice
 *          HttpMessageConverter拦截器
 *      l.ViewResolver
            通过url找到对应的view

 *  2·配置流程
 *      a.通过 EnableWebMvc 来引入 DelegatingWebMvcConfiguration 添加mvc配置，该类会将mvc关键类注入到ioc里面如
 *        RequestMappingHandlerMapping，RequestMappingHandlerAdapter
 *      b.外部直接继承 WebMvcConfigurer 来配置即可
 *
 * 二.servlet初始化（init方法）
 *  1.时机：容器初始化或者第一次收到请求（通过初始化参数配置）
 *  2.流程
 *      a.从servlet context里面通过key获取自定义的applicationContext，如果未获取到则创建并调用refresh来创建ioc，而spring boot则
 *          是用自定义的applicationContext
 *      b.从 applicationContext 获取 MultipartResolver HandlerMapping HandlerAdapters HandlerExceptionResolvers 等
 *
 * 三.servlet.service方法流程（重点）
 *
 * 三.filter和HandlerInterceptor区别
 *  1.filter是tomcat提供的，过滤了才会走到servlet.service方法
 *  2.是servlet里面的拦截controller方法，在调用controller方法之前之后等拦截
 *  3.filter可用于用户过滤，请求解码等，HandlerInterceptor用于业务拦截处理
 *
 * @author liucan
 * @version 2021/9/12
 */
public class Mvc {
}
