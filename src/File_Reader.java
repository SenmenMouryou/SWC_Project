import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Senmen
 * 文件读取器
 * 读入二进制文件，将其内容转换为Byte类型数组
 */
public class File_Reader implements Runnable{

    //日志类
    private static Logger logger = Logger.getLogger(File_Reader.class.getName());

    //一次读取的字节数
    private int BUF_SIZE = 256;

    //容纳数据的Byte数组
    private byte[] data;
    public byte[] getData() {
        return data;
    }

    //文件名
    private String filename = "";

    //本读取器读取文件的起始位置
    private int start_Read_Pointer = 0;

    //本读取器读取的字节数
    private int bytes_To_Read = 0;

    //本读取器的线程编号
    private int thread_Id = 0;
    public int getThread_Id() {
        return thread_Id;
    }


    //读取完毕标识
    private boolean read_Finished_Flag = false;
    public boolean is_Read_Finished() {
        return read_Finished_Flag;
    }


    /**
     * 构造器
     * @param filename 文件名
     * @param start_Read_Pointer 本读取器读取文件的起始位置
     * @param bytes_To_Read 本读取器读取的字节数
     * @param thread_Id 本读取器的线程编号
     */
    public File_Reader(String filename, int start_Read_Pointer,
                       int bytes_To_Read, int thread_Id ){
        this.filename = filename;
        this.start_Read_Pointer = start_Read_Pointer;
        this.bytes_To_Read = bytes_To_Read;
        this.thread_Id = thread_Id;

        data = new byte[bytes_To_Read];

        logger.log(Level.INFO,"线程"+thread_Id+"的读取器初始化完毕，" +
                "其读取起始于"+start_Read_Pointer+
                "共分配到"+bytes_To_Read+"个字节的读取任务");
    }

    //文件的随机访问流
    private RandomAccessFile raf = null;

    /**
     * 打开文件
     */
    private void open_File() {
        try {
            raf = new RandomAccessFile(filename, "r");
            logger.log(Level.INFO,"文件"+filename+"打开成功");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE,"文件"+filename+"打开失败",e);
        }
    }

    /**
     * 设置文件指针
     */
    private void set_File_Pointer(){
        try {
            raf.seek(start_Read_Pointer);
            logger.log(Level.INFO,"文件指针设置成功，位置:"+start_Read_Pointer);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"文件指针设置失败",e);
        }

    }

    @Override
    public void run() {
        //打开文件
        open_File();
        //设置文件指针
        set_File_Pointer();

        //初始化读入byte的计数器
        int read_Count = 0;
        //计算读取次数
        int max_Read_Times = bytes_To_Read/BUF_SIZE+1;
        //下一个开始读的位置
        int next_pos = 0;

        //从文件中读取数据
        logger.log(Level.INFO,"线程"+thread_Id+"开始读取数据...");
        assert raf!=null;
        while(true){
            try {
                //一次读取BUF_SIZE个字节到data数组
                raf.read(data, next_pos, BUF_SIZE);
            }catch (IOException e){
                logger.log(Level.SEVERE,"字节数读取错误",e);
            }
            //更新计数器
            read_Count++;
            //更新下一个起读位置
            next_pos += BUF_SIZE;
            //若下一个起读位置是最后一处则在读取末尾后结束读入
            if(next_pos + BUF_SIZE >= bytes_To_Read){
                try {
                    //读取末尾
                    raf.read(data, next_pos, bytes_To_Read-next_pos);
                }catch (IOException e){
                    logger.log(Level.INFO,"字节读取错误",e);
                }
                read_Finished_Flag = true;
                logger.log(Level.INFO,"线程"+thread_Id+"读取完毕");
                break;
            }

            try{
                Thread.sleep(1);
            }catch (InterruptedException e){
                logger.log(Level.SEVERE, "线程中断异常",e);
            }
        }
    }
}
