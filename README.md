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
## 直接缓冲区和非直接缓冲区
* 直接缓冲区：创建于物理内存上，避免了应用程序jvm内存与物理内存之间拷贝，但可控制性不强，回收开销大,使用allocateDirect方法创建
```java
ByteBuffer buf = ByteBuffer.allocateDirect();
```
* 非直接缓冲区，创建于jvm内存，使用java api编写，可控性强，采用gc回收，但需要进行操作系统与jvm之间拷贝采用allocate创建
```java
ByteBuffer buf = ByteBuffer.allocate();
```

# 通道
* 通道用于源节点与目标节点之间的连接，在java NIO中负责缓冲区的传输，Channel本身不传输数据，因此需要配合缓冲区进行传输、
* 通道主要实现类：
> java.nio.channels.Channel
>> FileChannel <br/>
>> SocketChannel <br/>
>> ServerSocketChannel<br/>
>> DatagramChannel<br/>

* 获取渠道
  * Java针对支持通道的类提供了getChannel方法
    * 本地io
      * FileInputStream/FileOutputStream
      * RandomAccessFile
  
    * 网络io
      * Socket
      * ServerSocket
      * DatagramSocket
  * Java 1.7 NIO 2中针对各个通道提供了静态方法Open()方法
  * Java 1.7 NIO 2中Files工具类的newByteChannel()方法
```java
//流方式
public static void test1(){
    long start = System.currentTimeMillis();
    try (
            FileInputStream fis = new FileInputStream("E:\\test\\test.zip");
            FileOutputStream fos = new FileOutputStream("E:\\test\\test1.zip");
            FileChannel inChannel = fis.getChannel();
            FileChannel outChannel = fos.getChannel();
    ) {
        ByteBuffer buf = ByteBuffer.allocate(1024 * 1024 * 1024);
        while (inChannel.read(buf) != -1){
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }
    }catch (Exception e){
        e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println("耗时：" + (end - start));
}
```
```java
//直接缓存（内存映射文件）
public static void test2(){
    long start = System.currentTimeMillis();
    try (
            FileChannel inChannel = FileChannel.open(Paths.get("E:\\test\\test.zip"), StandardOpenOption.READ);
            FileChannel outChannel = FileChannel.open(Paths.get("E:\\test\\test2.zip"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
    ) {
        MappedByteBuffer buf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        outBuf.put(bytes);
    }catch (Exception e){
        e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println("耗时：" + (end - start));
}
```
```java
//管道直接复制
public static void test3(){
    long start = System.currentTimeMillis();
    try (
            FileChannel inChannel = FileChannel.open(Paths.get("I:\\test\\test.zip"), StandardOpenOption.READ);
            FileChannel outChannel = FileChannel.open(Paths.get("I:\\test\\test3.zip"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
    ) {
        inChannel.transferTo(0, inChannel.size(), outChannel);
    }catch (Exception e){
        e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println("耗时：" + (end - start));
}
```
* 分散和聚集
  * 分散：将通道中数据依次读到多个缓冲区中
  * 聚集：将多个缓冲区中多个数据依次写入缓冲区中
```java
public static void test1(){
    try (
            RandomAccessFile in = new RandomAccessFile("E:\\test\\新建文本文档.txt", "rw");
            RandomAccessFile out = new RandomAccessFile("E:\\test\\新建文本文档1.txt", "rw");
            FileChannel inC = in.getChannel();
            FileChannel outC = out.getChannel();
    ){
        ByteBuffer[] bufs = {ByteBuffer.allocate(10), ByteBuffer.allocate(1024)};
        //分散
        inC.read(bufs);
        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));
        for (ByteBuffer buf : bufs) {
            buf.flip();
        }
        //聚集
        outC.write(bufs);
    }catch (Exception e){

    }

}
```

* 字符集Charset
  * 字符串-> 字节数组
  * 字节数组 -> 字符串
```java
 public static void test2() throws CharacterCodingException {
    String str = "奋青123";
    Charset utf8 = StandardCharsets.UTF_8;
    CharsetDecoder charsetDecoder = utf8.newDecoder();
    CharsetEncoder charsetEncoder = utf8.newEncoder();
    CharBuffer charBuffer = CharBuffer.allocate(1024);
    charBuffer.put(str);
    charBuffer.flip();
    ByteBuffer encode = charsetEncoder.encode(charBuffer);
    System.out.println(Arrays.toString(encode.array()));
    CharBuffer decode = charsetDecoder.decode(encode);
    System.out.println(decode);
    encode.flip();
    Charset gbk = Charset.forName("GBK");
    System.out.println(gbk.newDecoder().decode(encode));
    charBuffer.clear();
    charBuffer.put(str);
    charBuffer.flip();
    System.out.println(Arrays.toString(gbk.newEncoder().encode(charBuffer).array()));
}
```
## 阻塞式和非阻塞式
> 服务端
```java
@Test
public void server() throws IOException {

    //开一个服务端socket通道
    ServerSocketChannel ssChannel = ServerSocketChannel.open();
    //绑定端口
    ssChannel.bind(new InetSocketAddress(8080));
    //等待一个请求
    System.out.println("启动成功，等待请求");
    SocketChannel accept = ssChannel.accept();
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    //创建一个文件通道写出
    FileChannel fileChannel = FileChannel.open(Paths.get("new.png"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    while(accept.read(byteBuffer) != -1){
        byteBuffer.flip();
        fileChannel.write(byteBuffer);
        byteBuffer.clear();
    }
    accept.shutdownInput();
    byteBuffer.put("接受到数据，谢谢您！".getBytes());
    byteBuffer.flip();
    accept.write(byteBuffer);


    fileChannel.close();
    accept.close();
    ssChannel.close();
    System.out.println("服务器关闭");
}
```
> 客户端
```java
@Test
public void client() throws IOException {
    //开一个客户端socket通道
    SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
    System.out.println("通道启动成功，请求发送数据");
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    FileChannel fileChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);
    while (fileChannel.read(byteBuffer) != -1){
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();
    }
    socketChannel.shutdownOutput();

    int len;
    while ((len = socketChannel.read(byteBuffer)) != -1){
        byteBuffer.flip();
        System.out.println(new String(byteBuffer.array(), 0, len));
        byteBuffer.clear();
    }



    fileChannel.close();
    socketChannel.close();
    System.out.println("客户端关闭");
}
```
## 非堵塞式
> 客户端
```java
@Test
public void client() throws IOException {
    //打开通道
    SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
    //切换至非堵塞
    socketChannel.configureBlocking(false);
    //分配缓冲区
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    byteBuffer.put(LocalDateTime.now().toString().getBytes());
    byteBuffer.flip();
    socketChannel.write(byteBuffer);
    byteBuffer.clear();
    socketChannel.close();
}
```
> 服务端
```java
@Test
public void server() throws IOException {
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(9999));
    serverSocketChannel.configureBlocking(false);

    Selector selector = Selector.open();

    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    while (selector.select() > 0){
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()){
            SelectionKey next = iterator.next();
            if(next.isAcceptable()){
                ServerSocketChannel accept = (ServerSocketChannel) next.channel();
                SocketChannel accept1 = accept.accept();
                accept1.configureBlocking(false);
                accept1.register(selector, SelectionKey.OP_READ);
            }
            if(next.isReadable()){
                SocketChannel channel = (SocketChannel) next.channel();
                int len = 0;
                while ((len = channel.read(byteBuffer)) != -1){
                    byteBuffer.flip();
                    System.out.println(new String(byteBuffer.array(), 0, len));
                    byteBuffer.clear();
                }
            }
            iterator.remove();
        }
    }
}
```
## udp通道
> 客户端
```java
@Test
public void client() throws IOException {
    DatagramChannel datagramChannel = DatagramChannel.open();
    datagramChannel.configureBlocking(false);
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Scanner scanner = new Scanner(System.in);
    while (true){
        System.out.println("请输入:");
        String next = scanner.next();
        byteBuffer.put((dateFormat.format(new Date())  + "\n" + next).getBytes());
        byteBuffer.flip();
        datagramChannel.send(byteBuffer, new InetSocketAddress("127.0.0.1",9999));
        byteBuffer.clear();

    }
}
```
> 服务端
```java
@Test
public void server() throws IOException {
    DatagramChannel datagramChannel = DatagramChannel.open();
    datagramChannel.configureBlocking(false);
    datagramChannel.bind(new InetSocketAddress(9999));

    Selector selector = Selector.open();
    datagramChannel.register(selector, SelectionKey.OP_READ);
    while (selector.select() > 0){
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (iterator.hasNext()){
            SelectionKey next = iterator.next();
            if(next.isReadable()){

                datagramChannel.receive(byteBuffer);
                byteBuffer.flip();
                System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));
                byteBuffer.clear();
                System.out.println();
            }
            iterator.remove();
        }
    }
    datagramChannel.close();
}
```