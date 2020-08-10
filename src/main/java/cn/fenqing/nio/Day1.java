package cn.fenqing.nio;

import java.nio.ByteBuffer;

/**
 * @author fenqing
 */
public class Day1 {

    public static void main(String[] args) {
        test1();
        test2();
    }

    public static void test1(){
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println("===========================");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        buf.put(str.getBytes());
        System.out.println("==============put=============");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        buf.flip();
        System.out.println("==============flip=============");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        System.out.println("==============get=============");
        System.out.println(new String(bytes, 0, bytes.length));
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        buf.rewind();
        System.out.println("==============rewind=============");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        buf.clear();
        System.out.println("==============clear=============");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

    }

    public static void test2(){
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println("===========================");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        buf.put(str.getBytes());
        System.out.println("==============================");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        buf.flip();
        System.out.println("==============================");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        byte[] bytes = new byte[2];
        buf.get(bytes, 0, 2);
        System.out.println("==============================");
        System.out.println(new String(bytes, 0, bytes.length));
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());
        buf.mark();

        bytes = new byte[2];
        buf.get(bytes, 0, 2);
        System.out.println("==============================");
        System.out.println(new String(bytes, 0, bytes.length));
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

        buf.reset();
        System.out.println("==============================");
        System.out.println("position:" + buf.position());
        System.out.println("limit:" + buf.limit());
        System.out.println("capacity:" + buf.capacity());

    }

}
