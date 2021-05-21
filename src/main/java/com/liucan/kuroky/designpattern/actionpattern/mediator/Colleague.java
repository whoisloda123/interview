package com.liucan.kuroky.designpattern.actionpattern.mediator;

/**
 * @author liucan
 * @version 19-3-31
 */
public abstract class Colleague {
    protected Mediator mediator;

    public Colleague(Mediator mediator) {
        this.mediator = mediator;
    }

    public abstract void send();

    public abstract void receive();
}
