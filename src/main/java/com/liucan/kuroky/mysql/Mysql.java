package com.liucan.kuroky.mysql;

/**
 *
 *  1.回表查询:根据在辅助索引树中获取的主键id，到主键索引树检索数据的过程称回表查询
 *
 *  2.mysql innodb文件结构物理上分为：日志文件和数据索引文件
 *      a.数据索引文件：.frm文件是表结构信息，.ibd文件是表数据和索引信息
 *
 *  3.InnoDB存储引擎,B+树的高度一般为2-4层，就可以满足千万级数据的存储。查找数据的时候
 *    一次页的查找代表一次IO，那我们通过主键索引查询的时候，其实最多只需要2-4次IO就可以了
 *    a.一个节点占用16byte（key为8byte+2个4byte指针）
 *    b.一次性取一页16kb的数据，可以取16kb/16byte=1000的数据，故2层b+树可以存1000*1000一百万数据，3层1000*1000*1000
 *
 *  2.checkpoint机制（每隔一段时间检测）
 *   a.当redo log重做日志不可用（事务已经持久化成功）时会直接删除
 *   b.当缓冲池不够用是，会将缓冲落盘
 *
 *  22.为何不推荐使用外建
 *     https://www.zhihu.com/question/39062169
 *      a.外键还会因为需要请求对其他表内部加锁而容易出现死锁情况
 *      b.有了外键，当做一些涉及外键字段的增，删，更新操作之后，需要触发相关操作去检查，而不得不消耗资源
 *      c.数据库需要维护外键的内部管理
 *
 *  35.mysql事务(innodb),mysql的引擎中只有innodb支持事务
 *      a.4大特征(ACID)：原子性（A）一致性（C）隔离性（I）持久性（D）
 *          4个隔离级别
 *              1.读未提交（read uncommitted）
 *              2.读已提交（read committed） 事务A提交了数据，事务才B可以读取到，此种方法没有脏数据，但会出现重复读取的时候，可能结果已经变了
 *              3.可重复读（repeatable read）  但是会出现幻像读，就是读取到结果，出现新增，如范围查询，结果变多了 MySQL默认级别
 *              4.串行化（serializable） 可以避免“幻像读”，每一次读取的都是数据库中真实存在数据，事务A与事务B串行，而不并发
 *       d.mvcc(多版本并发控制)
 *         https://zhuanlan.zhihu.com/p/364331468
 *          1.解决不可重复读和可重复读，每一行多了创建事务版本号和删除事务版本号和回滚指针
 *          2.通过undo log和redo log,read view来实现
 *            a.undo logs（逻辑日志）保存事务执行过程用来回滚和一致性查询
 *            c.redo logs(物理日志)用来持久化事务已经回滚，如断电后恢复
 *            b.read view保存所有还未执行完的事务id，快照读和当前读（被访问的tx_id是否在read view里面,如果在的话不能够被访问，否则可以）会用到
 *          3.过程
 *             a.insert-当前的A事务-create_version=1，delete_version=null
 *             b.update-新插入一行B事务-create_version=2,delete_version=null 同时A事务-delete_version=2
 *             d.delete 最新的一行C事务-create_version=2,delete_version=3
 *             e.select 如何查找出A事务的数据
 *                 a.创建版本小于等于当前版本 create_version <= 1，确保读取的行的是在当前事务版本之前的
 *                 b.删除版本大于等于当前版本 delete_version >=1,确保事务之前行没有被删除
 *             e.而回滚指针是执行上一个版本（undo logs）形成引用链条
 *          4.快照读和当前读
 *           快照读：读取的是快照版本（只在第一次通过read view获取最新的版本），也就是历史版本，可重复读实现
 *           当前读：读取的是最新版本（每次都是通过read view获取最新的版本）,不可重复读实现
 *           普通的SELECT就是快照,UPDATE、DELETE、INSERT、SELECT ...  LOCK IN SHARE MODE、SELECT ... FOR UPDATE是当前读
 *           Consistent read（一致性读）是READ COMMITTED和REPEATABLE READ隔离级别下普通SELECT语句默认的模式。
 *       h.MySQL采用了自动提交（AUTOCOMMIT）的机制，InnoDB存储引擎，是支持事务的，所有的用户活动都发生在事务中.普通的insert也是在事务中执行，只是自动提交的
 *       g.总结
 *         1.利用MVCC实现一致性非锁定读，保证同一个事务中多次读取相同的数据返回的结果是一样的，解决了不可重复读的问题
 *         2.利用Gap Locks和Next-Key可以阻止其它事务在锁定区间内插入数据，因此解决了幻读问题
 *
 *  40.B-tree，B-plus-tree
 *      https://www.cnblogs.com/vincently/p/4526560.html
 *      a.B-tree:升级版的二叉查找树，在二叉查找树的基础上，每个节点可以包含2个以上的key，且里面的key也是顺序的
 *      b.B-plus-tree
 *          1.非叶子节点只包含导航信息（子节点指针），不包含具体值，具体值保存在所有叶子节点，只有达到叶子结点才命中
 *          2.所有叶子节点是一颗从小到大的顺序链表，便于区间查找和遍历
 *      c.B+树的优点
 *          1.非叶子节点不会带上ROWID，这样，一个块中可以容纳更多的索引项，一是可以降低树的高度。二是一个内部节点可以定位更多的叶子节点
 *          2.叶子节点之间通过指针来连接，范围扫描将十分简单，而对于B树来说，则需要在叶子节点和内部节点不停的往返移动
 *
 *  41.innodb索引
 *      索引：https://www.cnblogs.com/fuyunbiyi/p/2429297.html
 *     a.聚簇索引：索引的叶节点就是数据节点。确定表中数据的物理顺序，一个表只能包含一个聚集索引
 *     b.非聚簇索引：叶节点仍然是索引节点，只不过有一个指针指向对应的数据块
 *     c.覆盖索引:查询语句覆盖了索引时（查询结果和条件里面都只是索引），只通过索引而不用通过获取行数据就可以获取到结果
 *
 *  49.MySQL存储引擎-InnoDB&MyISAM区别
 *  https://www.cnblogs.com/liqiangchn/p/9066686.html
 *      两者最大的区别就是InnoDB支持事务，和行锁，而MyISAM是不支持的
 *      4.MyisAM支持全文索引（FULLTEXT）、压缩索引，InnoDB不支持
 *      5.InnoDB关注事务和并发，MyISAM关注查询性能
 *
 *  50.Mysql中的各种锁以及死锁
 *      a.锁：
 *          3.行级锁（Record lock）：行锁是通过给索引项加锁实现的
 *            a.故如果是查询的话，只有在查询的时候使用到了索引并且查到了才会用行锁，否者表锁
 *              如：SELECT * FROM products WHERE id='3' FOR UPDATE;行锁
 *                  SELECT * FROM products WHERE id='-1' FOR UPDATE：无记录，无锁
 *                  SELECT * FROM products WHERE name='Mouse' FOR UPDATE;，无主键，表锁
 *            b.对于UPDATE、DELETE和INSERT语句，InnoDB会自动给涉及数据集加排他锁（X)；对于普通SELECT语句，InnoDB不会加任何锁
 *          4.间隙锁（gap lock）：锁住一个索引区间（开区间，不包括双端端点）,防止幻读，锁定一定范围内的数据,如select * from t1_simple where id > 4 for update;
 *          5.临键锁(Next-Key Locks):record lock + gap lock, 左开右闭区间，例如（5,8]
 *          6.意向锁:用来解决行锁和表锁互斥的问题：在意向锁存在的情况下，事务A必须先申请表的意向锁，成功后再申请一行的行锁
 *              如事务A行读锁，事务B表锁，是互斥的，但是如果查找表里面哪一行是行锁，效率很低，于是有了意向锁
 *          7.mysql实现悲观锁和乐观锁
 *          https://www.cnblogs.com/zhiqian-ali/p/6200874.html
 *              a.悲观锁：select for update实现，里面用行锁实现的
 *              b.乐观锁：通过版本号或时间戳来实现，先select出版本号保存在内存，然后update版本号，将其+1，条件是数据库里面的版本号和
 *                  内存里面保存的是一样的，如果一样则获取到锁，否者失败，时间戳实现一样的道理
 *                  其实和cas差不多
 *      b.死锁
 *          1.场景:
 *              a.不同表相同记录行锁冲突:2个事务执行操作2张表，动作一模一样，但是顺序不一样，就和多线程出现死锁一样的场景
 *              b.相同表记录行锁冲突:2个事务执行操作2条数据，动作一模一样，但是顺序不一样，就和多线程出现死锁一样的场景
 *          2.如果避免：
 *              a.以固定的顺序访问表和行
 *              b.为表添加合理的索引。因为操作索引是会使用行锁
 *              c.大事务拆小。大事务更倾向于死锁，如果业务允许，将大事务拆小
 *          3.如何定位死锁成因
 *              a.通过应用业务日志定位到问题代码，找到相应的事务对应的sql
 *              b.确定数据库隔离级别,可以确定数据库的隔离级别，我们数据库的隔离级别是RC，这样可以很大概率排除gap锁造成死锁的嫌疑
 *
 *  71.如何保证mysql和redis，数据一致性，解决数据库与缓存双写的时候数据不一致的情况
 *  https://www.cnblogs.com/lingqin/p/10279393.html
 *      a.延时双删策略
 *          1.如果先更新数据库，再删缓存，会出更新成功，删除缓存失败，造成数据不一致
 *          2.先删redis，再更新mysql，会出现再删除后，另外请求过来，然后拿到了老数据
 *          3.解决上面的问题，先删除缓存，再更新mysql，sleep一段时间（等待另外一个请求读取到老数据，然后更新到缓存里面，返回给前端后），再删除缓存
 *      b.订阅mysql binlog增量消息（只要更新数据就会更新binlog） + mq如kafka + redis
 *          1.订阅mysql binlog增量消息 ，通过kafka发送给redis，然后更新
 *
 *  72.数据库水平切分和垂直切分
 *  https://uule.iteye.com/blog/2122627
 *  https://blog.csdn.net/5hongbing/article/details/78024897
 *      a.垂直切分：垂直一刀，根据不同的业务拆分到不同的数据库，或者比较大的数据单独放一个表
 *          优点：拆分简单，业务明确
 *          缺点.事务不好处理，过度切分导致系统复杂，存在性能问题
 *      b.水平拆分：水平一刀，分表操作，
 *          优点：事务处理比较简单，不会存在性能问题
 *          缺点：分表逻辑不好控制，数据迁移比较麻烦（可采用一致性hash算法），跨节点join，排序等等比较麻烦
 *          一般拆分到数据1000万
 *      c.数据切分应引发的问题
 *          1.分布式事务（垂直切分）
 *          2.跨节点Join的问题，排序等等问题
 *
 *  73.sql优化
 *     a.开启慢查询日志
 *          set global slow_query_log = ON;查找到执行慢的sql语句
 *          mysqldumpslow工具搜索慢查询日志中的SQL语句
 *     b.通过explain select查看慢sql计划,重要字段
 *          select_type：查询类型，比如：普通查询simple、联合查询(union、union all)、子查询等复杂查询
 *          table:表名
 *          type：单位查询的连接类型或者理解为访问类型，const，ref，index
 *          key：真正使用到的索引
 *          extra：的额外的信息，如using index（覆盖索引就是这个），using where
 *     c.通过show profile：查看执行的sql的会话中资源消耗情况，如cpu，io，sql执行消耗时间，默认关闭的
 *     d.索引优化
 *          频繁出现在where 条件判断，order排序，group by分组字段
 *          尽量创建组合索引，而不是单列索引
 *          表记录很少不需创建索引
 *          一个表的索引个数不能过多
 *          主键索引建议使用自增的长整型，避免使用很长的字段
 *
 *  83.为什么组合索引是最左原则，向右匹配直至遇到范围查询1(>、<、between、like)就停止匹配
 *      a.组合索引在b+树里面存储的结构和普通的索引是一样的,只是是多个索引的集合
 *      b.组合索引（a,b,c）,优先按照a列排序，a列相同的话再按照b列排序，b列相同再按照c列排序,故是最左匹配，相对于建立了a,ab,abc索引
 *      c.书写SQL条件的顺序，不一定是执行时候的where条件顺序。优化器会帮助我们优化成索引可以识别的形式
 *
 *  63.mysql集群主从复制，主从同步
 *   https://segmentfault.com/a/1190000038967218
 *    a.通过binlog和relay log实现
 *    b.binlog有3种存储格式
 *       statement类型：记录执行的sql
 *       row类型：执行被修改的行
 *       mixed类型：statement和row都有，因为row可能记录的数据很多
 *    c.master执行完事务后记录binlog，主启动binlog dump线程将binlog event发送给从，从启动一个io线程复制binlog到relay log，从启动SQL线程，
 *      将Relay中的数据进行重放，
 *    d.同步方式
 *        异步：写入binlog认为同步成功
 *        同步：所有从收到binlog日志，且收到所有从成功的事务消息才认为成功
 *        半同步：只收到一个从事务执行成功的消息
 *    e.主从延迟原因和方案
 *       https://zhuanlan.zhihu.com/p/259250733
 *       原因：slave的重放SQL线程是单线程的，重放过程中如果遇到锁也会等待
 *       如何判断：show slave status -> Seconds_Behind_Master，0 ：该值为零正常，NULL ：表示io_thread或是sql_thread有任何一个发生故障，该线程的Running状态是No
 *       如何尽量避免
 *          性能比Master更好的机器作为Slave，增加Slave的数量，降低单台Slave上的读压力
 *          关闭Slave的sync_binlog（写入binlog）参数，innodb_flushlog（写日志）参数
 *
 *  65.mysql集群方案
 *    https://blog.csdn.net/weixin_43750212/article/details/104778156
 *
 *  64.日志类型：https://www.cnblogs.com/myseries/p/10728533.html
 *   a.二进制日志（bin log）：用于记录任何修改数据库内容的语句，用于主从同步和本机数据恢复
 *   b.中继日志（relay log）：和bin日志一样的，只是从数据库用来同步bin日志的
 *   c.回滚日志（undo log，逻辑日志）：保证事务的原子性，用于实现事务
 *   d.重做日志（redo log，物理日志）：确保事务的持久性，用于在执行事务中崩溃，重启后恢复或回滚数据
 *   e.慢查询日志（slow query log）：记录查询时间大于设置的时间的慢查询日志
 *
 *   65.ACID靠什么保证
 *    A原子性由undo log日志保证，它记录了需要回滚的日志信息，事务回滚时撤销已经执行成功的sql
 *    C一致性一般由代码层面来保证
 *    I隔离性由MVCC来保证
 *    D持久性由内存+redo log来保证，mysql修改数据同时在内存和redo log记录这次操作，事务提交的时候通过redo log刷盘，宕机的时候可以从redo log恢复
 *
 *   66.UNION和UNION ALL的区别
 *      union会将2个结果集合并去掉重复的项，而union all则只是简单的合并不会去掉重复项
 *
 *   66.interview：https://zhuanlan.zhihu.com/p/164519371
 */
public interface Mysql {

}
