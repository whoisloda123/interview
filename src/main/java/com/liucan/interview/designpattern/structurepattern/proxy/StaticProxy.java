package com.liucan.interview.designpattern.structurepattern.proxy;

/**
 * 代理模式：访问对象不适合或者不能直接引用目标对象，代理对象作为访问对象和目标对象之间的中介
 * <p>
 * 代理模式的主要优点有：
 * 代理模式在客户端与目标对象之间起到一个中介作用和保护目标对象的作用；
 * 代理对象可以扩展目标对象的功能；
 * 代理模式能将客户端与目标对象分离，在一定程度上降低了系统的耦合度
 * <p>
 * 静态代理缺点：增加需要被代理的类，则需要增加相应的代理类，不灵活
 * <p>
 * 动态代理：
 * jdk动态代理：
 * 1.利用反射动态的生成代理类，InvocationHandler代理执行类只需要继承该类
 * 2.被代理的类必须要实现接口，否则抛异常
 * <p>
 * cglib动态代理:
 * 1.利用asm开源包，对代理对象类的class文件加载进来，通过修改其字节码生成子类来处理
 * 2.不需要实现接口,如果对象没有实现接口，只能用cglib动态代理
 * <p>
 * 区别
 * 1.jdk动态代理只能针对接口类实现代理，而cglib没有限制
 * 2.cglib动态代理针对类实现代理，通过生成子类来覆盖其中方法，所以说不能代理final修饰的方法
 * <p>
 *
 * 1、如果目标对象实现了接口，默认情况下会采用JDK的动态代理实现AOP
 * 2、如果目标对象实现了接口，可以强制使用CGLIB实现AOP
 *
 * 3、如果目标对象没有实现了接口，必须采用CGLIB库，spring会自动在JDK动态代理和CGLIB之间转换
 *
 * 为何jdk动态代理只会代理接口类？
 *  1.生成代理类的会继承Proxy类
 *  2.由于java不能多继承，这里已经继承了Proxy类了，不能再继承其他的类，所以JDK的动态代理不支持对实现类的代理，只支持接口的代理。
 *
 * @author liucan
 * @version 19-3-24
 */
public class StaticProxy implements Subject {
    private Subject realSubject = new RealSubject();

    public void perRequest() {
        System.out.println("开始执行request之前");
    }

    public void afterRequest() {
        System.out.println("执行request之后");
    }

    @Override
    public void Request() {
        perRequest();
        realSubject.Request();
        afterRequest();
    }
}
