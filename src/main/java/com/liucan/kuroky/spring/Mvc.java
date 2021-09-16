package com.liucan.kuroky.spring;

/**
 * 一.容器启动
 *  1.springmvc 和 boot 继承 tomcat 的 ServletContainerInitializer（容器启动会调用该接口），在 onStartup 方法里面往 servlet context 里面添加
 *      a.servlet
 *      b.filter
 *      c.初始化参数等
 *
 * 二.servlet初始化（init方法）
 *  1.时机：容器初始化或者第一次收到请求（通过初始化参数配置）
 *  2.流程
 *      a.从servlet context里面通过key获取自定义的applicationContext，如果未获取到则创建并调用refresh来创建ioc，而spring boot则
 *          是用自定义的applicationContext
 *      b.从 applicationContext 获取 MultipartResolver HandlerMapping HandlerAdapters HandlerExceptionResolvers 等设置给 servlet
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
 *          通过请求找到对应的处理器 HandlerExecutionChain（HandlerInterceptor 和 HandlerMethod）
 *      h.RequestMappingHandlerAdapter
 *          处理适配器，通过 RequestMappingHandlerMapping 获取的 HandlerMethod 和请求封装成 ServletInvocableHandlerMethod 来进行调用
 *      i.HandlerMethod
 *          封装的 controller 方法， 在 RequestMappingHandlerMapping 初始化的时候会将所有的 controller bean 里面封装为 url 对应的 HandlerMethod
 *      j.ServletInvocableHandlerMethod
 *          1.真正调用 controller 方法的类，HandlerExceptionResolver 也会组装为该类
 *          2.invokeAndHandler 方法会调用 HandlerMethodArgumentResolver 设置参数， 调用真正方法， HandlerMethodReturnValueHandler 来
 *            处理返回值
 *      k.DispatcherServlet
 *          处理请求
 *      l.RequestBodyAdvice/ResponseBodyAdvice
 *          HttpMessageConverter拦截器
 *      m.ViewResolver
            通过 view name 找到对应的 view 视图

 *  2·配置流程
 *      a.通过 EnableWebMvc 来引入 DelegatingWebMvcConfiguration 添加mvc配置，该类会将mvc关键类注入到ioc里面如
 *        RequestMappingHandlerMapping，RequestMappingHandlerAdapter
 *      b.外部直接继承 WebMvcConfigurer 来配置即可
 *
 * 三.servlet.service方法流程（重点）
 *  1.检查是否是表单请求，是则用 multipartResolver 转换为 MultipartHttpServletRequest
 *  2.通过 HandlerMapping 找到对应的处理器 HandlerExecutionChain（HandlerInterceptor 和 HandlerMethod）
 *  3.调用 HandlerInterceptor.preHandle
 *  4.调用处理器适配器 RequestMappingHandlerAdapter.handle 传入 HandlerMethod 开始真正调用 controller 方法
 *      a.将 HandlerMethod 封装成 ServletInvocableHandlerMethod 调用真正方法
 *      b.HandlerMethodArgumentResolver 来设置参数
 *      c.调用真正的方法
 *      d.HandlerMethodReturnValueHandler 来处理返回值
 *  5.调用 HandlerInterceptor.postHandle
 *  6.处理返回结果或异常
 *      a.如果返回view name 则通过 ViewResolver 找到对应的 view 视图
 *      b.如果抛出异常，则通过 HandlerExceptionResolver 处理异常
 *  7.获取到view视图之后，调用 HandlerInterceptor.afterCompletion
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
