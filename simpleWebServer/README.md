# simpleServer
#### 1.简介

Nginx一个具有高性能的【HTTP】和【反向代理】的【WEB服务器】，同时也是一个【POP3/SMTP/IMAP代理服务器】

##### 优点：

​	轻量 ，安装包只有几兆

​	高性能，使用异步非阻塞IO，多进程

​	配置灵活，配置方式丰富，支持多种第三方插件

​	开源，社区活跃

##### 反向代理：

​	意义：

​			a）:缓存，减少服务器压力，提升响应速度

​			b）:负载均衡

​			c）:故障转移

​			d）:访问控制

##### 负载均衡：

​	意义：

​			a）:实现流量均衡

​			b）:灵活实现服务器横向扩展

​			c）:高可用

##### 安装nginx
    sudo brew install nginx
    配置文件目录：/usr/local/etc/nginx
    通过include命令将如下conf以绝对路径导入到/usr/local/etc/nginx/nginx.conf，然后重启nginx即可生效
    停止nginx：nginx -s stop
    启动nginx: nginx

#### 2.配置解析

**反向代理实例:

​		通过本地8088端口代理服务器8089端口

​		通过8088端口+uri(/jack)代理8089端口，通过80端口+uri(/rose)代理8090端口

```
listen 127.0.0.1:8000; // listen localhost:8000 监听指定的IP和端口
listen 127.0.0.1;	监听指定IP的所有端口
listen 8000;	监听指定端口上的连接
listen *:8000;	监听指定端口上的连接
```

```
server_name www.itcast.cn www.itheima.cn;    精确匹配
server_name  *.itcast.cn	www.itheima.*;     通配符匹配  
server_name ~^www\.(\w+)\.com$;              正则表达式
```

精确匹配>正则表达式>模糊匹配

同一级中匹配度高的优先

同一级中同时匹配到两个则按顺序在前面的优先
配置实例：proxy2.conf

```
location /abc{
		default_type text/plain;
		return 200 "access success";
	}                                   模式匹配

location =/abc{
		default_type text/plain;
		return 200 "access success";
	}                                   精确匹配

location ~^/abc\w${
		default_type text/plain;
		return 200 "access success";
	}                                   正则表达式
```

反向代理相关配置指令：

​		proxy_set_header    添加请求头信息

​		proxy_pass   需要代理到哪个url

​		proxy_method   转发时的协议方法

​		proxy_hide_header    转发时隐藏报文头信息

​		proxy_pass_header    转发报文头信息

​		proxy_pass_request_headers     是否转发报文头

​		proxy_redirect    当服务器返回301，302时可以重设HTTP头部的location字段(proxy_redirect.conf)

​		proxy_next_upstream   当前server返回指定状态时继续调用下一个server处理	(next_upstream.conf)

```
error             # 与服务器建立连接，向其传递请求或读取响应头时发生错误;
timeout           # 在与服务器建立连接，向其传递请求或读取响应头时发生超时;
invalid_header    # 服务器返回空的或无效的响应;
http_500          # 服务器返回代码为500的响应;
http_502          # 服务器返回代码为502的响应;
http_503          # 服务器返回代码为503的响应;
http_504          # 服务器返回代码504的响应;
http_403          # 服务器返回代码为403的响应;
http_404          # 服务器返回代码为404的响应;
http_429          # 服务器返回代码为429的响应（1.11.13）;
non_idempotent    # 通常，请求与 非幂等 方法（POST，LOCK，PATCH）不传递到请求是否已被发送到上游服务器（1.9.13）的下一个服务器; 启用此选项显式允许重试此类请求;
off               # 禁用将请求传递给下一个服务器。
```
⚠️注意：该指令默认只对幂等方法进行重试，非幂等方法重试需要加上non-idempotent



​		deny     拒绝的域名

​		allow    允许的域名



**负载均衡实例：**

​

| 算法名称   | 说明             |
| ---------- | ---------------- |
| 轮询       | 默认方式         |
| weight     | 权重方式         |
| ip_hash    | 依据ip分配方式   |
| least_conn | 依据最少连接方式 |
| url_hash   | 依据URL分配方式  |
| fair       | 依据响应时间方式 |

应用层：为应用程序提供网络服务。

表示层：对数据进行格式化、编码、加密、压缩等操作。

会话层：建立、维护、管理会话连接。

传输层：建立、维护、管理端到端的连接，常见的有TCP/UDP。

网络层：IP寻址和路由选择

数据链路层：控制网络层与物理层之间的通信。

物理层：比特流传输。

实例:七层负载均衡实例（基于URL和IP进行负载均衡）

Balance_by_uri.conf

Simple_balance.conf

故障转移和自动恢复：(proxy.conf)

```json
max_fails   最大失败次数
fail_timeout	故障时间
```



**Rewrite功能：**

​	使用[nginx]提供的全局变量或自己设置的变量，结合正则表达式和标志位实现url重写以及重定向

意义：

​	 可以调整用户浏览的URL，看起来更规范，合乎开发及产品人员的需求。

​	 为了让搜索引擎搜录网站内容及用户体验更好，企业会将动态URL地址伪装成静态地址提供服务。

​	 网址换新域名后，让旧的访问跳转到新的域名上。例如，访问京东的360buy.com会跳转到jd.com

​	 根据特殊变量、目录、客户端的信息进行URL调整等



用法：

​

| rewrite | <regex> | <replacement> | [flag]   |
| ------- | ------- | ------------- | -------- |
| 关键字  | 正则    | 替代内容      | flag标记 |

last #本条规则匹配完成后，继续向下匹配新的location URI规则

break #本条规则匹配完成即终止，不再匹配后面的任何规则

redirect #返回302临时重定向，浏览器地址会显示跳转后的URL地址

permanent #返回301永久重定向，浏览器地址栏会显示跳转后的URL地址



实例：rewrite.conf,rewrite_by_condition.conf



**防盗链：**

​	盗链是指服务提供商自己不提供服务的内容，通过技术手段绕过其它有利益的最终用户界面（如广告），直接在自己的网站上向最终用户提供其它服务提供商的服务内容，骗取最终用户的浏览和点击率。受益者不提供资源或提供很少的资源，而真正的服务提供商却得不到任何的收益

​	通过引用其他服务方的资源，达到通过他人资源获利的目的

​	Http协议头中的referer用于表示当前url是从哪里链接过来的，如果是直接输入url进入这个字段就是空

​	如果是通过其他网页跳转，那这个字段就是网页的地址

nginx对于防盗链的解决方案：

​	valid_referers该指令用来指定请求头中的referer的匹配规则，如果匹配成功则将全局变量$invalid_referer设置为1，否则为0

​	语法：

​		valid_referers  [none|blocked|server_names]

​

```
none:表示无Referer值的情况。
blocked:表示Referer值被防火墙进行伪装。
server_names:表示一个或多个主机名称
```

配置实例：

​	referes.conf

```
curl -e "http://www.baidu.com/hello" www.mysvr.com:8088/hello
```

#### 其他常见配置：

​			try_files    查找文件，无文件则转发

​			add_header		添加响应头信息

​			auth_request     auth.conf

​			include     导入外部配置文件

​			default_type    指定支持的报文类型(auth.conf)

​			sendfile           使用内核内存直接复制方式减少上下文切换次数，提供响应速度（零拷贝技术）

​			tcp_nopush    启动Nagle算法，优化分包发送机制，提供网络传输速率

​			tcp_nodelay			禁止Nagle算法算法，即时发送







#### 3.高可用

​		nginx+keepalive实现nginx高可用架构
​	   配置成功：但外网无法访问，容器内虚拟ip能够自动漂移
​	   1.安装docker，拉取centos镜像

​				docker pull centos

​	   2.根据centos镜像启动容器

​				docker run -itd --name nginx-keep centos

​	   3.在容器内安装keepalived，nginx以及常用命令

​			yum install net-tools

​			yum install -y gcc openssl-devel popt-devel

​			yum install keepalived

​			rpm -Uvh http://nginx.org/packages/centos/7/noarch/RPMS/nginx-release-centos-7-0.el7.ngx.noarch.rpm

​			yum install nginx

​	   4.将容器提交为镜像

​			docker   commit nginx-keep   nginx-keep

​	   5.在宿主机新建keepalived,nginx配置文件，根据刚才提交的镜像创建容器（keep-master），并将宿主机的					keepalived,nginx配置文件映射到容器的/etc/keepalived,/etc/nginx目录

​			docker run -itd --privileged --name keep-master -v ~simpleServer/keepalived.conf:/etc/keepalived/keepalived.conf -v ~simpleServer/nginx_check.sh:/etc/keepalived/nginx_check.sh -v /lib/modules:/lib/modules nginx-keep /usr/sbin/init
​	   6.进入容器，启动keepalived和nginx

​		   docker exec -it keep-master /bin/bash

​           systemctl daemon-reload
​           systemctl start keepalived.service
​           systemctl status keepalived.service

​           nginx

​	   7.再新建一个容器(keep-slave),按照5，6，7，8步骤操作，注意配置文件略有不同

​			keepalived.conf文件中priority配置备机应小于主机

​	   8.在keep-master容器执行ip a可以看到，已绑定虚拟ip(172.17.0.100)，此时在keep-slave中是没有绑定虚拟ip的

​

```
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
70: eth0@if71: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default 
    link/ether 02:42:ac:11:00:03 brd ff:ff:ff:ff:ff:ff link-netnsid 0
    inet 172.17.0.3/16 brd 172.17.255.255 scope global eth0
       valid_lft forever preferred_lft forever
    inet 172.17.0.100/32 scope global eth0
       valid_lft forever preferred_lft forever
```

在keep-master中执行curl 172.17.0.100 即可正常访问nginx，

然后将keep-master的keepalived停止，测试虚拟ip已经自动漂移到keep-slave

#### 4.性能调优

​		进程配置

​				nginx进程模型：

​	相关参数：

​		worker_processes   一般与cpu逻辑核数一致

​		worker_cpu_affinity 进程绑定

​		accept_mutex    使用锁技术让worker进程串行执行，避免惊群现象

连接数配置

​	相关参数：

​		worker_connections

nginx并发数计算：

​		作为HTTP服务器：worker_processes*worker_connections/2

​		作为反向代理服务器：worker_processes*worker_connections/4

事件处理模型选择 select poll epoll

缓存:

​	强缓存：

​			浏览器本地缓存，由Expires，Cache-Control字段控制，浏览器通过这两个字段判断是否需要使用本地缓存，同时启用时Cache-Control优先级更高

​	协商缓存：

​			由server控制是否需要使用缓存，浏览器首次请求时server会返回Etag`和Last-Modified字段，当再次请求时会发送If-Modified-Since(为server上次返回的Last-Modified值)和Etag字段，由server判断资源是否改动，如果无改动则返回304，且不会再返回资源，			Etag为资源的唯一标示，当Etag字段与当前资源不一致时也会判定为资源变更，并重新返回最新资源

nginx相关配置：

​		add_header

​		proxy_cache_path

​		proxy_cache

​		proxy_cache_valid

​		proxy_ignore_headers

实例：cache.conf

​	

