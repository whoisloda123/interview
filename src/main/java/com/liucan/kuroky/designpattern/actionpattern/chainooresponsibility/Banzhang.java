package com.liucan.kuroky.designpattern.actionpattern.chainooresponsibility;

/**
 * @author liucan
 * @version 19-3-30
 */
public class Banzhang extends Leader {

    public Banzhang(Leader nextLeader, String name, int powerDays) {
        this.nextLeader = nextLeader;
        this.name = name;
        this.powerDays = powerDays;
    }

    @Override
    public boolean handleLeave(int days) {
        return approvalLeave(days);
    }
}
