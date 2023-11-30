# FilelessAgentMemShell

## #1 简单介绍

FilelessAgentMemShell是一款用于自定义生成**用于注入Agent内存马**的classFile的小工具

Agent内存马的工作原理是利用Java Instrument API 动态修改 正在JVM中的运行着的类字节码，选择合适的宿主类，就可以在不添加新类的前提下实现内存马逻辑

过去，这项技术需要文件落地才可使用，好在 [**rebeyond**](https://xz.aliyun.com/u/8697) [**游望之**](https://xz.aliyun.com/u/40732) 提出了无文件落地利用方式，使得通过反序列化漏洞/JNDI注入来一步打入Agent内存马有了可能

本工具对于前人给出的EXP进行了小幅修改，可以根据**不同目标环境和应用场景**，简单快速地生成用于注入Agent内存马的classFile；

## #2 原理

有关Agent内存马以及其无文件落地技术的原理，可参考以下文章：

对于Java Agent技术以及其内存马的简单介绍 https://www.freebuf.com/articles/web/323621.html  

Java内存攻击技术漫谈 https://xz.aliyun.com/t/10075

论如何优雅的注入Java Agent内存马 https://xz.aliyun.com/t/11640

Linux下无文件Java agent探究 https://tttang.com/archive/1525

Linux下内存马进阶植入技术 https://xz.aliyun.com/t/10186

## #3 使用指导

建议结合...............食用，完成第一次模拟注入实验

### 编译

本项目基于**Java8**

小的更新不会发布release，可以自行编译

也可以在release中直接下载jar包

### 运行

`java -jar FilelessAgentMemShell.jar`

### 基础使用方法

`Agent内存马的工作原理是利用Java Instrument API 动态修改JVM中运行着的类字节码，从而在不添加新类的前提下实现内存马逻辑`

为此，你需要：

#### 选定宿主类

一般选择目标**请求处理逻辑**中的某一环

在Tomcat环境中，这个类可以是

- **org.apache.tomcat.websocket.server.WsFilter #doFilter()（推荐，代码少且无lambda表达式）**
- **org.apache.catalina.core.StandardContextValve #invoke()（推荐，代码少且无lambda表达式）**
- org.apache.catalina.core.ApplicationFilterChain #doFilter()（不推荐，代码多且有lambda表达式）
- org.springframework.web.servlet.DispatcherServlet（不推荐，代码多）
- javax.servlet.http.HttpServlet #service()（Tomcat9之后/Weblogic环境下，包前缀是`jakarta`/`weblogic`）(不推荐，代码多)

#### 编写替换类

你可以使用Javassist工具方便的生成新字节码，一般来说可以使用insertBefore在目标方法前添加shell逻辑，以在不影响正常逻辑的前提下实现内存马

如需选用包含lambda表达式的宿主类，你必须先使用**ASM框架**删除包含有lambda表达式的方法的所有内容，再使用Javassist等框架进行二次编辑

有关Javassist框架的使用方法，可参照文档：

**Javassist**   https://www.cnblogs.com/rickiyang/p/11336268.html

至于**ASM**框架，从未接触过到完全掌握需要较大时间成本，在本项目中其使用场景较为单一，所以也可以直接套用我在........中给出的示例代码

如果你完全不想接触这部分工具，也可以直接使用仓库中给出的**测试用**新类；未来或许会给出**实战可用**的针对常见类的新字节码，详见**更新计划**

此外，动态替换字节码意味着你可以做到任何事，对抗检测、循环复活......

#### 命令行参数

- -c className;eg:``org.apache.catalina.core.StandardContextValve`` (宿主类的**全限定类名**)
- -p Path; eg:`/path/test.class` （新类字节码路径）
- -o os; eg: `Linux/Windows`（目标操作系统）
- -t  templatesImpl; eg:`true/false`（是否使用templatesImpl来加载类）
- -i  ifbeyondJDK8; eg:`true/false`（目标JDK版本是否大于8，仅目标为Windows时需要填写）
- -b  eg: `32/64` (目标操作系统位数，仅对于Windows目标可指定)

例如，在使用本项目提供的漏洞环境与测试用新类进行模拟测试时，你应该指定如下参数

`java -jar .\FilelessAgentMemshellGenerator.jar` 

` -b 64 ` 

`-c "org.apache.tomcat.websocket.server.WsFilter" `

`-i false`

` -o "Windows"` 

` -p .\WsFilter.class` 

` -t false`

#### 提示

- 如果帮助信息出现乱码，请使用`chcp 936` 修改字符编码后重新运行项目
- 建议使用JDK低版本，以提高生成classFile的兼容性
- -c指定的类名**必须是全限定名**

#### 打入内存马

内存马仅仅是代码执行的一种效果，并且它只有在**动态代码执行上下文**中才能发挥作用

什么叫动态代码执行上下文？一个很简单的例子，CC6链（套娃调用invoke，最终执行Runtime.getRuntime().exec()）那条，就**不属于动态代码执行上下文**

详细请看Ruilin师傅的：http://rui0.cn/archives/1408  

而那些能动态加载字节码的sink，比如反序列化漏洞中的templatesImpl，低版本JNDI注入，就属于动态代码执行上下文

利用工具生成最终类，其静态代码块中包含自注入逻辑；使用反序列化漏洞/JNDI注入漏洞让目标**动态加载类**，这就完成了攻击

## #4 已知问题

### 乱码问题

使用中如遇乱码，可使用chcp 936切换字符编码

### 目标操作系统限制

**暂未测试** `Windows 32bit`

**暂不支持** `Linux 32bit`

## #5 开发进度&更新计划

- [ ] 提供自动化缩短payload长度的逻辑；提供分割最终字节码的逻辑，以适配分块传输
- [ ] 预置在常见宿主类中实现内存马逻辑的成品类文件
- [ ] 实现msf、ysomap等工具的cli交互模式；提供英文版ReadMe；开发图形化
- [ ] 联动其它攻击工具，更方便地一次打入Agent内存马
- [ ] 实现 [**cincly**](https://xz.aliyun.com/u/14775) 师傅提到的“借尸还魂”技术
- [ ] **提供随机类名，规避类的重复加载**问题
- [ ] 解决loadLibrary引起的一次攻击失败后续就不能再攻击问题
- [ ] 测试所有可用的JDK版本

## #6 背景

本人在学习及实操无文件落地注入Agent内存马相关技术时需要经常测试可用的宿主类、不同JDK版本，有时还需要对shell逻辑进行修改，重复的修改、编译十分浪费时间

我意识到可以编写一款简单的小工具来解决这个问题，经过一段时间的工作和反复修改，**FilelessAgentMemShell**诞生了


## #7 免责申明

**未经目标授权**使用本项目（FilelessAgentMemShell）给**目标**植入内存马**是非法的**，本项目**应用于且仅用于授权的安全测试**与学习研究目的

**任何使用及滥用本项目造成的后果与本人无关**
