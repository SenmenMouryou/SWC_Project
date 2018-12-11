import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    //日志类
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
//        Main_Window window = new Main_Window();
//        File_Reader_Controller_Test test = new File_Reader_Controller_Test();
//        test.test_Start();
        test_IO();

    }

    private static void test_IO(){
        int FILE_LENGTH = 1000000000;

        String filename = "Test/file_Reader_Test_Main.bin";

        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename));

            logger.log(Level.INFO,"文件开始写入");

            for(int i=0; i<FILE_LENGTH; i++){
                byte byte_To_Write = (byte)i;
                outputStream.write(byte_To_Write);
            }

            logger.log(Level.INFO,"文件写入完毕");

            outputStream.close();

        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE,"文件写入流创建失败：文件路径不存在",e);
        } catch (IOException e){
            logger.log(Level.SEVERE,"文件写入出错",e);
        }

        try {
            RandomAccessFile raf = new RandomAccessFile(filename,"r");
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(filename));

            byte[] bytes = new byte[FILE_LENGTH];

            logger.log(Level.INFO,"文件开始读取");

            buf.skip((long)0);
            buf.read(bytes, 0, FILE_LENGTH);

            raf.close();
            logger.log(Level.INFO,"文件读取完毕");

            for(int i=0; i<5; i++){
                System.out.print(bytes[i]+" ");
                if(i%10==0){
                    System.out.println();
                }
            }


        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE,"文件读取流创建失败",e);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"文件读取错误",e);
        }

    }
}
