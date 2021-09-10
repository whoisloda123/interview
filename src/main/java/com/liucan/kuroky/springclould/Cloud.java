package com.liucan.kuroky.springclould;

/**
 *
 * 1.spring如何解决循环依赖的
 *  https://developer.aliyun.com/article/766880
 *  a.三级缓存的作⽤：
 *      第⼀级缓存：存储创建完全成功的单例Bean。
 *      第三级缓存： 主要设计⽤来解决循环依赖问题的，它是存储只执⾏了实例化步骤的bean（还未依
 *          赖注⼊和初始化bean操作），但是该缓存的key是beanname， value是ObjectFactory，⽽不是你想存储的bean（将只完成实例化的bean的引⽤交给ObjectFactory持有）。
 *          ObjectFactory的作⽤：保存提前暴露的Bean的引⽤的同时，针对该Bean进⾏BeanPostProcessor操作，也就是说，在这有⼀个步骤下，可能针对提前暴露的Bean产⽣代理对象。
 *      第⼆级缓存：主要设计⽤来解决循环依赖时，既有代理对象⼜有⽬标对象的情况下，如何保存代理
 *          对象。同时还要有⼈保存⽬标对象的引⽤，然后会在最后的部分，使⽤代理对象的引⽤去替换⽬标对象的引⽤。
 *  b.为何用三级，其实二级也可以
 *  如果要使用二级缓存解决循环依赖，意味着所有Bean在实例化后就要完成AOP代理，这样违背了Spring设计的原则
 *  设计之初就是通过AnnotationAwareAspectJAutoProxyCreator这个后置处理器来在Bean生命周期的最后一步来完成AOP代理，而不是在实例化后就立马进行AOP代理。
 *
 * 2.微服务注册中心
 * https://mp.weixin.qq.com/s/YDHk8uwN8VAkt8NXEs4lRg
 *
 * 3.could hystrix:https://zhuanlan.zhihu.com/p/150889569
 *
 * 4.spring事务传播机制：https://segmentfault.com/a/1190000020386113
 *
 * @author liucan
 * @date 5/22/21
 */
public interface Cloud {

}
