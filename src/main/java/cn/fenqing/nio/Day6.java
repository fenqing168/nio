package cn.fenqing.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

/**
 * @author fenqing
 */
public class Day6 {

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

}
