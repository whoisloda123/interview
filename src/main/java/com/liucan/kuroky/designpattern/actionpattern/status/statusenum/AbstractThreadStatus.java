package com.liucan.kuroky.designpattern.actionpattern.status.statusenum;

import com.liucan.kuroky.designpattern.actionpattern.status.ThreadContext;

/**
 * 状态类，每个状态只负责关注相应状态应该做的事情，再切换到下个状态
 *
 * @author liucan
 * @version 19-3-31
 */
public abstract class AbstractThreadStatus {
    public abstract void handle(ThreadContext context);
}
