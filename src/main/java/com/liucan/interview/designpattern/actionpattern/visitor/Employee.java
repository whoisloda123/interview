package com.liucan.interview.designpattern.actionpattern.visitor;

/**
 * 访问者（visitor）模式：将数据结构中（不同类型）的元素操作分离出来封装成独立的类，不改变数据结构前提下新增这些元素的新操作
 *      新增访问不需要修改原来结构，将数据结构和数据操作分离
 *      一般针对于列表里面不同的数据类型
 *
 * 主要角色
 *      抽象访问者（Visitor）角色：定义一个访问具体元素的接口，为每个具体元素类对应一个访问操作 visit() ，该操作中的参数类型标识了被访问的具体元素。
 *      具体访问者（ConcreteVisitor）角色：实现抽象访问者角色中声明的各个访问操作，确定访问者访问一个元素时该做什么。
 *      抽象元素（Element）角色：声明一个包含接受操作 accept() 的接口，被接受的访问者对象作为 accept() 方法的参数。
 *      具体元素（ConcreteElement）角色：实现抽象元素角色提供的 accept() 操作，其方法体通常都是 visitor.visit(this) ，
 *          另外具体元素中可能还包含本身业务逻辑的相关操作。
 *      对象结构（Object Structure）角色：是一个包含元素角色的容器，提供让访问者对象遍历容器中的所有元素的方法，通常由 List、Set、Map 等聚合类实现。
 *
 * @author liucan
 * @version 19-3-31
 */
public abstract class Employee {
    //允许一个访问者过来访问
    public abstract void accept(IVisitor visitor);
}
