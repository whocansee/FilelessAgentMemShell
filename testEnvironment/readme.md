## demo.jar

SpringBoot 2.7.0 搭建的简易漏洞环境

在`/memShell/readObject?a=`路由处，可通过a参数传递base64编码后的序列化数据

在`/memShell/jndi?b=`路由处，可通过b参数传递base64编码后的 jndi url

## WsFilter.class

**`org.apache.tomcat.websocket.server.WsFilter`** 的新字节码

具体修改:  在`doFilter()`方法开始处插入了`Runtime.getRuntime.exec("calc")` 

