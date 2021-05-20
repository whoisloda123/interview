package com.liucan.interview.designpattern.structurepattern.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK动态代理实现InvocationHandler接口
 * 只能针对实现了接口的类进行代理,否则会抛异常
 *
 * @author liucan
 * @version 19-3-24
 */
public class JdkDynamicProxy implements InvocationHandler {

    private Object target; //需要代理的目标对象

    public JdkDynamicProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("jdk动态代理开始执行函数了");
        Object invoke = method.invoke(target, args);
        System.out.println("jdk动态代理执行函数结束了");
        return invoke;
    }

    public static Object getProxy(Object target) {
        //JDK动态代理只能针对实现了接口的类进行代理，newProxyInstance 函数所需参数就可看出
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new JdkDynamicProxy(target));
    }
}
