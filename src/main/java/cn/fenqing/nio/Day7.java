package cn.fenqing.nio;


import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author fenqing
 */
public class Day7 {

    public static void main(String[] args) throws IOException {
         new Day7().client();
    }

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
}
