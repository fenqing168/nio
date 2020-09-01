package cn.fenqing.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author fenqing
 */
public class Day5 {

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

}
