package com.liucan.kuroky.mybatis;

/**
 *
 * 一.mybatis的#{}和${}区别
 *  参考：http://www.mybatis.cn/archives/70.html
 *     #{}:只是占位符，在sql语句预编译阶段会用？替换，执行的时候才传入值，可防止sql注的过入
 *     ${}:是直接替换，然后sql语句预编译，这样有可能会sql注入，因为SQL注入是发生在预编译程中
 *     使用#{}可以有效的防止SQL注入，提高系统安全性。原因在于：预编译机制。预编译完成之后，SQL的结构已经固定，
 *         即便用户输入非法参数，也不会对SQL的结构产生影响，从而避免了潜在的安全风险\
 * 二.mybatis一级缓存和二级缓存
 *  1.一级缓存
 *      a.sqlSession级别的缓存，默认开启，该对象中有一个数据结构（HashMap）用于存储缓存数据
 *      b.如中间sqlSession去执行commit操作（执行插入、更新、删除),，则会被清除
 *      c.同一个sqlSession和查询条件则不用查数据库
 *  2.二级缓存
 *      a.Mapper（namespace）级别的缓存。多个SqlSession去操作同一个Mapper的sql语句，多个SqlSession可以共用二级缓存，二级缓存是跨SqlSession的
 *      b.如果调用相同namespace下的mapper映射文件中的增删改SQL，并执行了commit操作。此时会清空该namespace下的二级缓存
 *      c.<setting name="cacheEnabled" value="true"/>总开关开启，然后在mapper里面加<cache></cache>
 *  3.应用场景
 *      a.对于访问响应速度要求高，但是实时性不高的查询
 */
public interface Mybatis {
}
