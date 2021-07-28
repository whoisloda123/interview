package com.liucan.kuroky.es;

/**
 * 一、索引库维护
 *  0、创建索引库
 * 	    put /{index}{	 可以有mappings  }
 *
 *  1、field的属性
 * 	    type：数据类型
 * 		    数值类型：integer、long、float等
 * 		    字符串类型：keyword、text
 * 			    keyword：不分词，可以创建索引。
 * 				例如身份证号、订单号等。不需要分词，可以创建索引。就会把field中的内容作为一个完整的关键词，创建索引。
 * 			text：分词的数据类型。可以指定分词器。（稍后会讲）
 * 		日期类型等
 * 	    store：是否存储。
 * 		    取决于是否展示给用户看，或者业务需要。不存储不影响分词，不影响查询，可以节约存储空间。不存储的后果就是无法展示。
 * 	    index:是否索引，如果不分词也可以选择是否创建索引，不创建索引就无法查询此字段的内容。
 * 	    analyzer：定义分词器，默认是标准分词器（standardAnalyzer）
 *
 *  2、不设置mapping也可以向索引库中添加文档，es会根据文档的数据自动识别field的类型。
 * 	    推荐先设置好mapping然后再添加数据。因为es识别的数据类型未必是我们想要的数据类型。
 * 	    mapping一旦设置无法修改。只能新增。
 *
 * 一、中文分词器
 * 1、查看分词器的分词效果
 * 	post _analyze
 *        {
 * 		"text":"elasticSearch是一个全文检索引擎",
 * 		"analyzer":"standard"
 *    }
 * 2、IK分词器的安装
 * 	1）先下载IKAnalyzer插件
 * 		https://github.com/medcl/elasticsearch-analysis-ik/releases 
 * 	2）解压缩，放到es的安装目录下的plugin目录下即可
 * 	3）重启ES
 *
 * 	IK提供两种分词算法：
 * 		ik_smart
 * 		ik_max_word
 * 3、使用中文分词器
 * 	需要在设置索引库mapping时指定field使用中文分词器。
 * 	一旦mapping设置好之后是不能修改的。
 *
 * 二、ES的集群概念
 * 	ES集群的搭建不需要第三方工具，只需要有多个ES节点即可，需要配置一个cluster.name参数。只要cluster.name相同就认为是集群中的一个节点。
 * 	1、集群的使用方法
 * 		集群的使用和单机版是完全相同的，没有任何区别。
 * 		客户端连接集群时，只需要连接到集群的任意节点即可。
 *
 * 三、集群的核心原理
 * 1、集群节点的类型
 * 	默认情况下集群中的节点都是相同的，没有区别。
 * 	1）master节点（master-eligible）
 * 		参与选举，选举出来的节点就是master节点，集群中只有一个。负责整个集群状态的维护。
 * 		默认每个节点都是master-eligible节点，都有被选举成master的资格。
 * 		配置独立的master节点：config/elasticsearch.yml
 * 			node.master: true
 * 			node.data: false
 * 	    负责集群节点上下线，shard分片的重新分配,创建、删除索引,接收集群状态(cluster state)的变化，并推送给所有节点
 * 	2）data节点（数据节点）
 * 		只存储数据，并不参与master的选举。默认情况下每个节点都有data节点的角色。
 * 		node.master: false
 * 		node.data: true
 * 	3）协调节点
 * 		请求的转发工作。用于查询负载均衡。将查询请求分发给多个node服务器，并对结果进行汇总处理。协调节点的作用
 * 		配置独立的协调节点：
 * 			node.master: false
 * 			node.data: false
 *
 * 	默认情况下每个节点都具备三种角色。
 * 	集群里面index可以分为多个分区，每个分区可以有多个副本，不同的分区在相同或者不同的集群节点，和kafka集群是一样的
 *
 * 2、ClusterState
 * 	Cluster State是指集群中的各种状态和元数据(meta data)信息
 *
 * 3、集群master选举
 * 	1）选举的时机
 * 		1、集群中没有master，第一次组建集群时，需要选举master。
 * 		2、集群中master挂了。
 * 	2）选举的过程
 * 		1、node2和node3两个节点中谁的clusterstate版本高，谁的优先级高
 * 		2、如果节点的clusterstate相同，谁的nodeid越小优先级越高
 * 4、集群脑裂
 * 	由于网络问题，会讲集群中的不同的节点分离，变成两个子网，每个子网中都可以选举出自己的master节点，就造成了集群脑裂
 * 	解决方案：配置集群中超过半数的节点的最小值。
 * 		# 决定选举一个master最少需要多少master候选节点。默认是1。
 * 		# 这个参数必须大于等于为集群中master候选节点的quorum数量，也就是大多数。
 * 		# quorum算法：master候选节点数量 / 2 + 1
 * 		# 例如一个有3个节点的集群，minimum_master_nodes 应该被设置成 3/2 + 1 = 2（向下取整）
 * 		discovery.zen.minimum_master_nodes:2
 *
 * 		2个节点：2
 * 		3个节点：2
 * 		4个节点：3
 * 		5个节点：3
 * 		6个节点：4
 *
 *
 * 	可以使用命令，动态调整minimum_master_nodes参数的值：
 * 	PUT /_cluster/settings { "persistent" : { "discovery.zen.minimum_master_nodes" : 2 } }
 * 	重启依然生效。
 * 5、集群的扩展
 * 	一旦索引的分片设置完毕后不能修改，但是可以修改副本的数量。
 * 	更新副本数量：
 * 		# 更新副本数量
 * 		PUT /blogs/_settings
 *        {
 * 			"number_of_replicas" : 2
 *        }
 * 	极限情况下每个节点上只存储一个副本，此时性能应该是最好的。
 * 	副本的作用是解决高可用和负载均衡。一般副本数量就是1-2两个即可。
 *
 * @author liucan
 * @date 2021/7/28
 */
public class Es {
}
