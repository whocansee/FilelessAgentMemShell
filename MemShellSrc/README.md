## EXP来源

https://xz.aliyun.com/t/11640

对于Windows目标  [rebeyond](https://xz.aliyun.com/u/8697)

对于Linux目标  [**游望之**](https://xz.aliyun.com/u/40732) 

## 修改

### Windows

原EXP中，rebeyond将类名设置为`WindowsVirtualMachine`，从而能够调用`enqueue`方法

但这无法应对JDK8以上的环境，也不能无视目标是否已经加载过这个类

参考其在[另一篇文章](https://xz.aliyun.com/t/10075#toc-4)中提出的方法：

**自定义类调用系统Native库函数**，再将这个类硬编码在最终类中，在类里**再写一个**类加载器，调用它去解码并加载类

这个操作会多生成一个内部类，而在实际注入中并不能一次性加载多个类，因此我**做了一些修改 :**

**直接让最终类继承ClassLoader**，然后再在其静态代码块中加载自定义的类，从而能够无视目标类加载情况一次完成注入

### Linux

没有进行专门的修改

### 适配生成器

- 向多个可能的内存地址都填充值


```java
unsafe.putByte(native_jvmtienv + 377, (byte) 2);
unsafe.putByte(native_jvmtienv + 361, (byte) 2);
unsafe.putByte(native_jvmtienv + 369, (byte) 2);
```

- 将注入逻辑从待调用的方法中移到了静态代码块中

- 向类名和字节码中填充了占位符

- 增添了几个适用于templatesImpl或Java 8以上目标环境的EXP版本
