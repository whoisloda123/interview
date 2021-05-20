package com.liucan.interview.designpattern.actionpattern.memento;

import java.util.LinkedList;

/**
 * 管理者（Caretaker）角色：对备忘录进行管理，提供保存与获取备忘录的功能,但其不能对备忘录的内容进行访问与修改
 *
 * @author liucan
 * @version 19-3-30
 */
public class MementoManager {

    private int currentMemento = -1;

    private LinkedList<CommandMemento> commandMementoList = new LinkedList<>();

    public void addMemento(CommandMemento commandMemento) {
        commandMementoList.add(commandMemento);
        currentMemento++;
    }

    public CommandMemento undo() {
        if (currentMemento == 0) {
            return null;
        }
        return commandMementoList.get(--currentMemento);
    }

    public CommandMemento resume() {
        if (currentMemento == commandMementoList.size()) {
            return commandMementoList.getLast();
        }
        return commandMementoList.get(++currentMemento);
    }
}
