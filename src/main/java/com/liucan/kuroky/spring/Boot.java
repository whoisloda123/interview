package com.liucan.kuroky.spring;

/**
 * 1.为何 SpringBoot 中 main 方法执行完毕后程序不会直接退出
 *  a.在所有非守护线程（用户线程）都退出的情况下，jvm会自动退出
 *  b.是在启动的时候额外启动一个非守护线程，该线程一直不退出直到被主动通知关闭
 *
 * @author liucan
 * @date 2021/9/16
 */
public class Boot {
}
