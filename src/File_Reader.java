import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * @author Senmen
 * 文件读取器
 * 读入二进制文件，将其内容转换为Byte类型数组
 */
public class File_Reader implements Runnable{

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

    /**
     * 构造器
     * @param filename 文件名
     * @param start_Read_Pointer 本读取器读取文件的起始位置
     * @param bytes_To_Read 本读取器读取的字节数
     */
    public File_Reader(String filename, int start_Read_Pointer,
                       int bytes_To_Read ){
        this.filename = filename;
        this.start_Read_Pointer = start_Read_Pointer;
        this.bytes_To_Read = bytes_To_Read;

        data = new byte[bytes_To_Read];
    }

    //文件的随机访问流
    private RandomAccessFile raf;

    /**
     * 打开文件
     */
    private void open_File() {
        try {
            raf = new RandomAccessFile(filename, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置文件指针
     */
    private void set_File_Pointer(){
        try {
            raf.seek(start_Read_Pointer);
        } catch (IOException e) {
            e.printStackTrace();
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
        while(true){
            try {
                data[read_Count] = read_byte_From_File();
            }catch (IOException e){
                e.printStackTrace();
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
                e.printStackTrace();
            }
            //计数器超出本读取器的读取范围则结束读入
            if(read_Count>=bytes_To_Read){
                break;
            }
        }
    }
}
