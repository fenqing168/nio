package cn.fenqing.nio;

import java.nio.ByteBuffer;

/**
 * @author fenqing
 */
public class Day2 {

    public static void main(String[] args) {
        test1();
    }

    public static void test1(){
        //建立直接缓冲区
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
        System.out.println(buf.isDirect());

        buf = ByteBuffer.allocate(1024);
        System.out.println(buf.isDirect());
    }

}
