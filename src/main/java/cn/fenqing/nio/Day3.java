package cn.fenqing.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author fenqing
 */
public class Day3 {

    public static void main(String[] args) {
        test1();
        test2();
        test3();
    }

    public static void test1(){
        long start = System.currentTimeMillis();
        try (
                FileInputStream fis = new FileInputStream("e:\\test\\test.zip");
                FileOutputStream fos = new FileOutputStream("e:\\test\\test1.zip");
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

    public static void test2(){
        long start = System.currentTimeMillis();
        try (
                FileChannel inChannel = FileChannel.open(Paths.get("e:\\test\\test.zip"), StandardOpenOption.READ);
                FileChannel outChannel = FileChannel.open(Paths.get("e:\\test\\test2.zip"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
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

    public static void test3(){
        long start = System.currentTimeMillis();
        try (
                FileChannel inChannel = FileChannel.open(Paths.get("e:\\test\\test.zip"), StandardOpenOption.READ);
                FileChannel outChannel = FileChannel.open(Paths.get("e:\\test\\test3.zip"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
        ) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }catch (Exception e){
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start));
    }

}
