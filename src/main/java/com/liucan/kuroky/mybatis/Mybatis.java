package com.liucan.kuroky.mybatis;

/**
 * @author liucan
 * @date 5/22/21
 */
public interface Mybatis {
    /* *
     *
     *  44.mybatis的#{}和${}区别
     *   参考：http://www.mybatis.cn/archives/70.html
     *      #{}:只是占位符，在sql语句预编译阶段会用？替换，执行的时候才传入值，可防止sql注的过入
     *      ${}:是直接替换，然后sql语句预编译，这样有可能会sql注入，因为SQL注入是发生在预编译程中
     *      使用#{}可以有效的防止SQL注入，提高系统安全性。原因在于：预编译机制。预编译完成之后，SQL的结构已经固定，
     *          即便用户输入非法参数，也不会对SQL的结构产生影响，从而避免了潜在的安全风险\
     */
}
