import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Senmen
 * 二进制文件读取器的控制器，
 * 可控制多个读取器多线程读取，线程数从配置文件Source/settings.property中获得
 * 自身使用线程安全的单例
 * 读入文件并以byte[]数组储存
 */

public class File_Reader_Controller {
    //日志类
    private static Logger logger = Logger.getLogger(File_Reader_Test.class.getName());

    //单例实现
    private volatile static File_Reader_Controller instance;
    /**
     * 私有构造器，使用单例模式
     */
    private File_Reader_Controller(){
        try {
            //从配置文件中读取开启的线程数
            READER_QUANTITY = Integer.parseInt(Property_Manager.read_Property("READER_QUANTITY"));
            //从配置文件中读取允许的文件最大长度
            MAX_FILE_SIZE = Integer.parseInt(Property_Manager.read_Property("MAX_FILE_SIZE"));
        } catch (IOException e) {
            logger.log(Level.SEVERE,"配置文件写入失败",e);
        }

        logger.log(Level.INFO,"File_Reader_Controller 单例已加载");
    }
    /**
     * 双重检验锁法实现的单例
     * 线程安全、懒启动
     * 参考 https://www.cnblogs.com/cielosun/p/6582333.html
     * @return 取得的File_Reader实例
     */
    public static File_Reader_Controller getInstance(){
        if(instance==null){
            synchronized (File_Reader_Controller.class){
                if(instance==null){
                    instance = new File_Reader_Controller();
                }
            }
        }
        return instance;
    }

    //读取器的个数，即开启的线程数
    private int READER_QUANTITY = 4;
    //待读取的文件最大允许大小
    private int MAX_FILE_SIZE = Integer.MAX_VALUE;

    //待读取的文件名
    private String filename = "";
    public String get_Filename() {
        return filename;
    }

    //读取器容器数组
    private ArrayList<File_Reader> file_Reader_Array
             = new ArrayList<File_Reader>(READER_QUANTITY);

    //读取后的字节存放数组
    private ArrayList<byte[]> data_Array = new ArrayList<>(READER_QUANTITY);
    public ArrayList<byte[]> get_Data_Array() {
        return data_Array;

    }

    /**
     * 初始化文件读取器方法的返回码表
     * 0 正常返回 -1 读取错误;
     */
    private enum Init_File_Reader_Return{
        RETURN_NORMAL(0),
        READING_ERR(-1);

        private int return_Code;

        private Init_File_Reader_Return(int return_Code){
            this.return_Code = return_Code;
        }
        public int get_Return_Code() {
            return return_Code;
        }
    }

    /**
     * 初始化读取器数组
     * @param filename 读取的文件名
     * @return 返回码,见初始化文件读取器方法的返回码表
     */
    public int init_File_Readers_Array(String filename){
        this.filename = filename;

        //检查文件大小是否合法
        if(get_File_Length()<=0){
            logger.log(Level.SEVERE,"文件长度不合法！");
            return Init_File_Reader_Return.READING_ERR.get_Return_Code();
        }

        //初始化读取器们，并将其存入读取器数组

        int bytes_to_read = 0;
        int start_Pointer = 0;
        int rest_File_Length = get_File_Length();
        file_Reader_Array.clear();
        for(int thread_ID=0;thread_ID<READER_QUANTITY;thread_ID++){
            //确定此读取器读取起始点 = 文件总字节 - 文件剩余字节
            start_Pointer = get_File_Length() - rest_File_Length;
            //确定此读取器读入字节数 = 文件剩余字节/未分配的线程数
            bytes_to_read = rest_File_Length/(READER_QUANTITY-thread_ID);
            //更新文件剩余字节数
            rest_File_Length-=bytes_to_read;
            //初始化读取器
            File_Reader file_reader = new File_Reader(filename, start_Pointer, bytes_to_read, thread_ID);
            //将此读取器存入数组
            file_Reader_Array.add(file_reader);
            logger.log(Level.INFO,"读取器"+thread_ID+"已存入数组");
        }

        return Init_File_Reader_Return.RETURN_NORMAL.get_Return_Code();
    }

    /**
     * 创建线程，运行已声明的读取器
     * 读取完毕后，将读取的内容存入byte数组
     */
    public void run_Readers_Threads(){
        for(int thread_ID=0; thread_ID<READER_QUANTITY; thread_ID++){
            try {
                Thread thread = new Thread(file_Reader_Array.get(thread_ID));
                //开始读取
                thread.start();
                logger.log(Level.INFO,"线程"+thread_ID+"已开启");
            }catch (Exception e){
                logger.log(Level.SEVERE,"线程创建失败！",e);
            }
        }

        boolean read_All_Finished_Flag = false;
        //循环检测读取是否完成，完成后跳出
        do {
            read_All_Finished_Flag = true;
            for (int i = 0; i < READER_QUANTITY; i++) {
                read_All_Finished_Flag = (read_All_Finished_Flag && file_Reader_Array.get(i).is_Read_Finished());
            }
        }while(!read_All_Finished_Flag);

        logger.log(Level.INFO,"读取已全部完成");

        //将读取完毕的字节存入data_Array
        for(int i=0; i<READER_QUANTITY; i++) {
            data_Array.add(file_Reader_Array.get(i).getData());
        }
    }

    /**
     * 读取文件长度的返回码表
     * -1 读取错误; -2 文件过大
     */
    private enum Get_File_Length_Return{
        READING_ERR(-1),TOO_LARGE_FILE(-2);

        private int return_Code;

        private Get_File_Length_Return(int return_Code){
            this.return_Code = return_Code;
        }
        public int get_Return_Code() {
            return return_Code;
        }
    }

    /**
     * 取得文件长度
     * @return  文件大小;-1文件过大;-2读取出错
     */
    private int get_File_Length(){
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            int file_Length_Illegal = 0;
            //取得文件长度
            if(file.length() > MAX_FILE_SIZE){
                logger.log(Level.WARNING,"读取的文件过大！");
                return Get_File_Length_Return.TOO_LARGE_FILE.get_Return_Code();
            }else{
                file_Length_Illegal = (int)file.length();
                return file_Length_Illegal;
            }

        }
        else{
            logger.log(Level.SEVERE,"读取文件长度时出错！");
            return Get_File_Length_Return.READING_ERR.get_Return_Code();
        }
    }


}
