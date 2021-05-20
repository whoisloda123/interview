package com.liucan.interview.designpattern.actionpattern;

import com.liucan.interview.designpattern.actionpattern.chainooresponsibility.Banzhang;
import com.liucan.interview.designpattern.actionpattern.chainooresponsibility.Student;
import com.liucan.interview.designpattern.actionpattern.chainooresponsibility.Teacher;
import com.liucan.interview.designpattern.actionpattern.command.ConcreteCommand;
import com.liucan.interview.designpattern.actionpattern.command.Invoker;
import com.liucan.interview.designpattern.actionpattern.command.Recever;
import com.liucan.interview.designpattern.actionpattern.iterator.Aggregate;
import com.liucan.interview.designpattern.actionpattern.iterator.Iterator;
import com.liucan.interview.designpattern.actionpattern.mediator.ColleagueA;
import com.liucan.interview.designpattern.actionpattern.mediator.ColleagueB;
import com.liucan.interview.designpattern.actionpattern.mediator.Mediator;
import com.liucan.interview.designpattern.actionpattern.memento.MementoManager;
import com.liucan.interview.designpattern.actionpattern.memento.Originator;
import com.liucan.interview.designpattern.actionpattern.observer.Bear;
import com.liucan.interview.designpattern.actionpattern.observer.Bull;
import com.liucan.interview.designpattern.actionpattern.observer.OilFutures;
import com.liucan.interview.designpattern.actionpattern.status.ThreadContext;
import com.liucan.interview.designpattern.actionpattern.status.command.*;
import com.liucan.interview.designpattern.actionpattern.strategy.CrabCookingStrategyFactory;
import com.liucan.interview.designpattern.actionpattern.templatemethod.StudyAbroad;
import com.liucan.interview.designpattern.actionpattern.templatemethod.StudyInAmerica;
import com.liucan.interview.designpattern.actionpattern.visitor.*;

/**
 * 行为型模式:用于描述程序在运行时复杂的流程控制，即描述多个类或对象之间怎样相互协作共同完成单个对象都无法单独完成的任务
 *
 * 总共11种
 *
 * 模板方法（Template Method）模式：定义一个操作中的算法骨架，将算法的一些步骤延迟到子类中，使得子类在可以不改变该算法结构的情况下重定义该算法的某些特定步骤。
 * 策略（Strategy）模式：定义了一系列算法，并将每个算法封装起来，使它们可以相互替换，且算法的改变不会影响使用算法的客户。
 * 命令（Command）模式：将一个请求封装为一个对象，使发出请求的责任和执行请求的责任分割开。
 * 职责链（Chain of Responsibility）模式：把请求从链中的一个对象传到下一个对象，直到请求被响应为止。通过这种方式去除对象之间的耦合。
 * 状态（State）模式：允许一个对象在其内部状态发生改变时改变其行为能力。
 * 观察者（Observer）模式：多个对象间存在一对多关系，当一个对象发生改变时，把这种改变通知给其他多个对象，从而影响其他对象的行为。
 * 中介者（Mediator）模式：定义一个中介对象来简化原有对象之间的交互关系，降低系统中对象间的耦合度，使原有对象之间不必相互了解。
 * 迭代器（Iterator）模式：提供一种方法来顺序访问聚合对象中的一系列数据，而不暴露聚合对象的内部表示。
 * 访问者（Visitor）模式：在不改变集合元素的前提下，为一个集合中的每个元素提供多种访问方式，即每个元素有多个访问者对象访问。
 * 备忘录（Memento）模式：在不破坏封装性的前提下，获取并保存一个对象的内部状态，以便以后恢复它。
 * 解释器（Interpreter）模式：提供如何定义语言的文法，以及对语言句子的解释方法，即解释器。
 * @author liucan
 * @version 19-3-28
 */
public class ActionPattern {

    public void test() {
        //模板方法模式
        StudyAbroad tm = new StudyInAmerica();
        tm.templateMethod();

        //策略方式模式
        new CrabCookingStrategyFactory().getCrabCookingStrategy("braisedCrabs").cookingMethod();

        //命令模式
        Invoker invoker = new Invoker(new ConcreteCommand(new Recever()));
        invoker.invoke();

        //命令模式+备忘录模式,实现动作撤销和恢复
        Originator originator = new Originator(new MementoManager());
        originator.doCommand(new ConcreteCommand(new Recever("命令1")));
        originator.doCommand(new ConcreteCommand(new Recever("命令2")));
        originator.undo();
        originator.resume();

        //职责链模式
        Teacher teacher = new Teacher(null, "老师", 3);
        Banzhang banzhang = new Banzhang(teacher, "班长", 1);
        new Student().requsetLeave(banzhang, 1);
        new Student().requsetLeave(banzhang, 2);

        //状态模式
        ThreadContext threadContext = new ThreadContext();
        ThreadCommandReceiver commandReceiver = new ThreadCommandReceiver();
        threadContext.handle(new StartCommand(commandReceiver));
        threadContext.handle(new GetCpuCommand(commandReceiver));
        threadContext.handle(new SuspendCommand(commandReceiver));
        threadContext.handle(new ResumeCommand(commandReceiver));
        threadContext.handle(new GetCpuCommand(commandReceiver));
        threadContext.handle(new StopCommand(commandReceiver));

        //观察者模式
        OilFutures oilFutures = new OilFutures();
        oilFutures.addObserver(new Bear());
        oilFutures.addObserver(new Bull());
        oilFutures.setPrice(7.0f);

        //中介者模式
        Mediator mediator = new Mediator();
        ColleagueA colleagueA = new ColleagueA(mediator);
        ColleagueB colleagueB = new ColleagueB(mediator);
        colleagueA.send();
        colleagueB.send();

        //迭代器模式
        Aggregate aggregate = new Aggregate();
        aggregate.add(1);
        aggregate.add(2);
        Iterator iterator = aggregate.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        //访问者模式
        ObjectStructure objectStructure = new ObjectStructure();
        objectStructure.add(CommonEmployee.builder().job("码农").age(28).build());
        objectStructure.add(Manager.builder().performance("完成几个大项目").address("成都的").build());
        objectStructure.accept(new Visitor1());
        objectStructure.accept(new Visitor2());
    }
}
