package com.liucan.kuroky.designpattern.actionpattern.mediator;

import java.util.ArrayList;
import java.util.List;

/**
 * 中介者模式：定义一个中介对象来封装一系列对象之间的交互，使原有对象之间的耦合松散，且可以独立地改变它们之间的交互。中介者模式又叫调停模式
 * 相对于封装了对象之间的交互
 * <p>
 * 主要角色。
 * 抽象中介者（Mediator）角色：它是中介者的接口，提供了同事对象注册与转发同事对象信息的抽象方法。
 * 具体中介者（ConcreteMediator）角色：实现中介者接口，定义一个 List 来管理同事对象，协调各个同事角色之间的交互关系，因此它依赖于同事角色。
 * 抽象同事类（Colleague）角色：定义同事类的接口，保存中介者对象，提供同事对象交互的抽象方法，实现所有相互影响的同事类的公共功能。
 * 具体同事类（Concrete Colleague）角色：是抽象同事类的实现者，当需要与其他同事对象交互时，由中介者对象负责后续的交互
 * <p>
 * 如QQ 服务器都可以采用“中介者模式”来实现
 *
 * @author liucan
 * @version 19-3-31
 */
public class Mediator {

    public List<Colleague> colleagues = new ArrayList<>();

    public void register(Colleague colleague) {
        if (colleague == null) {
            throw new NullPointerException();
        }
        if (!colleagues.contains(colleague)) {
            colleagues.add(colleague);
        }
    }

    /**
     * 转发同事的请求
     *
     * @param colleague 需要被转发的同事
     */
    public void relay(Colleague colleague) {
        for (Colleague colleague1 : colleagues) {
            if (!colleague1.equals(colleague)) {
                colleague1.receive();
            }
        }
    }
}
