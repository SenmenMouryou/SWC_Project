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
    private BufferedInputStream buf_In = null;

    /**
     * 打开文件
     */
    private void open_File() {
        try {
//            raf = new RandomAccessFile(filename, "r");
            buf_In = new BufferedInputStream(new FileInputStream(filename));
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
//          raf.seek(start_Read_Pointer);
            buf_In.skip(start_Read_Pointer);
            logger.log(Level.INFO,"文件指针设置成功，位置:"+start_Read_Pointer);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"文件指针设置失败",e);
        }

    }

    /**
     * 从文件中读取1 byte数据
     * @return 读取的1字节数据
     */
    private byte read_byte_From_File() throws IOException {
        byte has_Read = 0;

        has_Read = raf.readByte();

        return has_Read;
    }


    @Override
    public void run() {
        //打开文件
        open_File();
        //设置文件指针
        set_File_Pointer();

        //初始化读入byte的计数器
        int read_Count = 0;

        //从文件中读取数据
        logger.log(Level.INFO,"线程"+thread_Id+"开始读取数据...");
        assert buf_In!=null;
        while(true){
            try {
                read_byte_From_File();
            }catch (IOException e){
                logger.log(Level.WARNING,"字节数读取错误，替换为上一个合法数据",e);
                //错误处理：如果读取失败，将数据设为上一个合法数据，如果是第一个则设为0
                if(read_Count>0) {
                    data[read_Count] = data[read_Count - 1];
                }
                else{
                    data[read_Count] = 0;
                }
            }

            //更新计数器
            read_Count++;

            try{
                Thread.sleep(1);
            }catch (InterruptedException e){
                logger.log(Level.SEVERE, "线程中断异常",e);
            }
            //计数器超出本读取器的读取范围则结束读入
            if(read_Count>=bytes_To_Read){
                read_Finished_Flag = true;
                logger.log(Level.INFO,"线程"+thread_Id+"读取完毕");
                break;
            }
        }
    }
}
