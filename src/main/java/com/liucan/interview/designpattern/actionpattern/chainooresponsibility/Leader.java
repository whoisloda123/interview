package com.liucan.interview.designpattern.actionpattern.chainooresponsibility;

import lombok.Getter;

/**
 * 责任链（Chain of Responsibility）模式（职责链模式）：为了避免请求发送者与多个请求处理者耦合在一起，
 * 将所有请求的处理者通过前一对象记住其下一个对象的引用而连成一条链；
 * 当有请求发生时，可将请求沿着这条链传递，直到有对象处理它为止。
 * <p>
 * 一个请求有多个对象可以处理，但每个对象的处理条件或权限不同，如员工请假
 *
 * @author liucan
 * @version 19-3-30
 */
public abstract class Leader {
    @Getter
    protected Leader nextLeader;
    @Getter
    protected String name;

    protected int powerDays;

    /**
     * 处理请假
     *
     * @param days 请假天数
     */
    public abstract boolean handleLeave(int days);

    protected boolean approvalLeave(int days) {
        if (days <= powerDays) {
            System.out.println(name + "批准了请假，天数：" + days);
        } else {
            if (nextLeader == null) {
                System.out.println("没有人批准请假，天数：" + days);
                return false;
            }
            System.out.println(name + "没有权限批准请假，天数：" + days + "转到下个领导批准：" + nextLeader.getName());
            nextLeader.handleLeave(days);
        }
        return true;
    }
}
