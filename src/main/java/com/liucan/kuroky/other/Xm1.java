package com.liucan.kuroky.other;

/**
 * 项目注意事项：
 * <ol>
 *     <li>代码扩展性</li>
 *     <li>打印日志规范</li>
 *     <li>代码规范</li>
 * </ol>
 *
 * 个人亮点：
 * <ol>
 *     <li>喜欢对之前代码进行优化，如威胁情报</li>
 *     <li>习惯发现利于和分享对团队效率有提示的工具和方法</li>
 *     <ul>
 *         <li>idea 各种插件</li>
 *         <li>让打包部署更方便，如 jenkins，docker，helm 打包</li>
 *         <li>部署 api 接口服务</li>
 *     </ul>
 *     <li>看源码提升自己的技术，spring 全家桶，mybatis 等</li>
 *     <li>学习新技术，如 flink</li>
 * </ol>
 *
 * ms主要事项：
 * <ol>
 *     <li>不露痕迹地说出msg爱听的话</li>
 *     <li>一定要主动，说出亮点，msg没有义务挖掘你的亮点</li>
 *     <li>引导msg朝你的优势地方去</li>
 * </ol>
 * 中心系统项目：
 * <p>
 * 分析探针端上传的流量和警报日志信息，以及下发警报规则和相关配置等，采用 spring cloud 微服务治理，部署到 docker 容器，通过 k8s 容器编排，
 * 依赖 mysql， redis， es， kafka，flink，clickhouse 等第三方组件
 * a.优化中心系统:
 * <p>
 * 之前是单机系统，代码量多且模块单一，业务复杂，扩展新业务不灵活，部署不方便需要对其整体包括框架，部署，打包进行优化。
 * <p>对框架和部署进行优化：
 * <ol>
 *     <li>微服务化，引入 spring cloud，使用 nacos 的注册中心和配置中心,通过 open feign 做 rpc， ribbon 做负载均衡，
 *     hystrix 当做熔断, spring cloud gateway 做网关</li>
 *     <li>拆分系统，将 gradle 单模块改成 maven 多模块结构，方便开发和模块剥离，maven 的插件比 gradle 多且使用简单</li>
 *     <li>先将业务比较简单的剥离出来，如大屏展示，，bfc-device-ping</li>
 *     <li>剥离业务不是很复杂的，如bfc-device-api（提供给探针端查询）服务，甚至重新某些业务如 通用配置同步业务，后面新增加的系统直接用微服务</li>
 *     <li>部署的话，将微服务打包成 docker 镜像，放入容器运行，开始通过 docker-compose 镜像管理，后面通过另外小组研发的 k8s 管理平台来进行打包部署，
 *     我们只需要上传镜像，然后上传 helm charts 包就可以了</li>
 *     <li>对探针端</li>
 *     <li>对威胁情报库优化：</li>
 *     <li>对第三方推送优化：因为 c++ 发送过来的数据量很大，探针端收到后不能丢掉</li>
 * </ol>
 * 对业务进行优化：
 * <p>探针端上传的警报日志和流量信息是通过 kafka 上传上来，然后中心专门有个消费微服务，从 kafka 里面消费信息，然后如 es， 警报日志的量还行，因为是聚合之后的数据，主要
 * 是原始流量日志，特别是 http 的很多，而且要进行聚合，打标签，之前的消费微服务有点吃力
 * <ol>
 *     <li>需要解决数据量多，进行聚合的问题，和入库的性能</li>
 *     <li>调研了流式处理框架 flink 和 kafka stream 并对其的性能进行测试，flink 从 kafka 获取数据进行流聚合过滤等操作后再入 kafka，
 *     处理 dns 日志能达到 10w/s，而 kafka stream 5w 左右</li>
 *     <li>最后采用 flink 来对数据进行过滤统计</li>
 *     <li>clickhouse 是另外一个同事调研的，主要处理入库性能，能达到 10多万/s</li>
 * </ol>
 * <p>对之前的探针端和中心端的通用配置同步的业务进行重写
 * <p>重构项目注意事项点或步骤：
 * <ol>
 *     <li>先将</li>
 * </ol>
 * 遇到的问题和难点：
 * <ol>
 *     <li></li>
 * </ol>
 * @author liucan
 * @version 2021/12/16
 */
public class Xm1 {
}
