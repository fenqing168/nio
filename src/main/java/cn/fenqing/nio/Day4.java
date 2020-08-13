package cn.fenqing.nio;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.util.Arrays;

/**
 * @author fenqing
 */
public class Day4 {

    public static void main(String[] args) throws CharacterCodingException {
        test2();
    }

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

}
