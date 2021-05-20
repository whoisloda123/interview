package com.liucan.interview.designpattern.creativepattern;

import com.liucan.interview.designpattern.creativepattern.abstractfactory.*;
import com.liucan.interview.designpattern.creativepattern.builder.Farm;
import com.liucan.interview.designpattern.creativepattern.factorymethod.AbstractFactory;
import com.liucan.interview.designpattern.creativepattern.factorymethod.ConcreteFactory1;
import com.liucan.interview.designpattern.creativepattern.factorymethod.ConcreteFactory2;
import com.liucan.interview.designpattern.creativepattern.factorymethod.Product;
import com.liucan.interview.designpattern.creativepattern.singleton.HungrySingleton;
import com.liucan.interview.designpattern.creativepattern.singleton.LazySingleton;

/**
 * 创建型模式：关注创建和使用分离
 * 类型：
 * 1.单例（Singleton）模式
 * 2.原型（Prototype）模式:一个对象作为原型，通过对其进行复制而克隆出多个和原型类似的新实例
 * 3.工厂方法（FactoryMethod）模式:定义一个用于创建产品的接口，由子类决定生产什么产品
 * 4.抽象工厂（AbstractFactory）模式：提供一个创建产品族的接口，其每个子类可以生产一系列相关的产品
 * 5.建造者（Builder）模式：将一个复杂对象分解成多个相对简单的部分，然后根据不同需要分别创建它们，最后构建成该复杂对象
 *
 * @author liucan
 * @version 19-3-21
 */
public class CreativPatterns {

    public void test() throws CloneNotSupportedException {
        //单例模式
        LazySingleton lazySingleton = LazySingleton.getInstance();
        HungrySingleton hungrySingleton = HungrySingleton.getInstance();

        //原型模式
        Realizetype realizetype = new Realizetype();
        Realizetype clone = (Realizetype) realizetype.clone();

        //工厂方法模式
        AbstractFactory factory = new ConcreteFactory1();
        Product product = factory.newProduct();
        product.show();

        factory = new ConcreteFactory2();
        product = factory.newProduct();
        product.show();

        //抽象工厂模式
        FarmFactory farmFactory = new CdFarmFactory();
        Anima anima = farmFactory.newAnima();
        Plant plant = farmFactory.newPlant();

        farmFactory = new CqFarmFactory();
        anima = farmFactory.newAnima();
        plant = farmFactory.newPlant();

        //建造者模式(此处是工厂方法模式结合，也可以不用建FarmBuilder抽象类,然后在buildAnima方法上面设置入参)
        Farm cdFarm = Farm.cdFarmBuilder()
                .buildAnima()
                .buildPlant()
                .build();
        Farm cqFarm = Farm.cqFarmBuilder()
                .buildAnima()
                .build();
    }
}
