package com.liucan.interview.designpattern.actionpattern.memento;

import com.liucan.interview.designpattern.actionpattern.command.Command;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备忘录（memento）模式（快照模式）：提供可以恢复用户操作的模式
 * <p>
 * 备忘录模式的主要角色如下。
 * 发起人（Originator）角色：记录当前时刻的内部状态信息，提供创建备忘录和恢复备忘录数据的功能，实现其他业务功能，它可以访问备忘录里的所有信息。
 * 备忘录（Memento）角色：负责存储发起人的内部状态，在需要的时候提供这些内部状态给发起人。
 * 管理者（Caretaker）角色：对备忘录进行管理，提供保存与获取备忘录的功能，但其不能对备忘录的内容进行访问与修改。
 * <p>
 * 此处用命令模式和备忘录模式实现用户操作的撤销和恢复
 *
 * @author liucan
 * @version 19-3-30
 */
//备忘录角色：负责存储发起人的内部状态
@Data
public class CommandMemento {
    private Command command;
    private LocalDateTime localDateTime;
}
