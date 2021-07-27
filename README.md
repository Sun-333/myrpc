# myrpc
- 自定义Spring boot starter 实现myrpc框架的自动化配置，与插件式框架
- 对用户提供@RpcService、@RpcRference注解实现零侵入式的RPC功能实现
- 自定义RPC协议（协议头部信息、协议体信息）
- 通过netty实现底层网络传输与编解码
- 通过动态代理实现client stub
- 通过ComplateFuture实现异步的客户端调用
- 通过业务线程池将服务端对消息的编码、解码与handler处理进行隔离
- 默认提供Gzip的Rpc body消息内容的压缩
- 默认提供Protostuff作为消息的序列化与反序列化
- 默认提供zookeeper作为服务注册与发现中心
- 默认提供基于随机选举的Balance机制
- 客户端通过BeanPostProcess 扫描@RpcReference注解属性，并通过反射机制实现代理对象（序列化、反序列化、网络传输）的注入
- 服务端通过BeanPostProcess 扫描通过@RpcService注解标记的接口实现类 并发布服务、暴露接口

# 待完成功能与问题
- 基于责任链模式与动态代理实现Rpc的自定义拦截功能，提供Ppc服务端可配置的权限认证、请求监控等功能
- 目前可插拔框架只提供了序列化反序列化、压缩、负载均衡、服务发布的可插拔功能，AutoConfigure配置类需要完善
- 添加更多的负载均衡算法
- 客户端与服务端的通信契约只能是同一接口（接口限定名称、接口方法、入参数）并且服务端通过类似Spring IOC实现本地服务的发布与查找，扩展性较差。
- 单一依赖zookeeper作为服务注册，提供非AP型注册中心或局域网广播形式的服务注册与查找
- 目前自定义spring boot strater强依赖Spring boot 2.1.3.RELEASE 版本

# 说明
- 项目中提供myrpc客户端、myrpc服务端 使用示例
- 目前使用application.properties的配置功能不够完善，用户自定义插件的映入需要通过JAVA配置类进行引入
