# nio 笔记
## buffer 
* buffer除了boolean以外，其他基本类型都有封装buffer
> IntBuffer 

> ByteBuffer 

> LongBuffer 

> ShortBuffer 

> CharBufferr 

> FloatBuffer 

> DoubleBuffer

* buffer通过静态方法allocate(int) 创建一个实例
```java
ByteBuffer buf = ByteBuffer.allocate(1024);
```
* buffer有四个重要属性
> position: 代表当前游标位置 

> limit: 表示可操作数据最大位置
 
> capacity： 表示缓冲区最大容量

> mark: 标记位置，用于恢复，默认-1
* put方法，将数据填充到buffer里,position会随之改变,但不可超过limit
* flip方法，转换成读取模式，position重置为0，limit变成position原来的值
```java
public final Buffer flip() {
    limit = position;
    position = 0;
    mark = -1;
    return this;
}
```
* get方法，将数据通过指定格式读取出，并移动position游标，游标不可超过limit
* rewind方法，将缓冲区的position游标恢复至0，即可重复读
* clear方法，清除缓冲区，（数据并未清除，只是将各个游标恢复至最初状态，即数据被遗忘）
* mark方法，标记当前缓冲区的position位置，用于恢复
* reset方法，恢复至mark标记位置