import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Senmen
 * 二进制文件读取器的容器，可容纳多个读取器多线程读取
 * 自身使用线程安全的单例
 * 读入文件并提供输出接口
 */

public class File_Reader_Container {

    private volatile static File_Reader_Container instance;
    /**
     * 私有构造器，使用单例模式
     */
    private File_Reader_Container(){
        System.out.println("File_reader singleton has loaded");
    }
    /**
     * 双重检验锁法实现的单例
     * 线程安全、懒启动
     * 参考 https://www.cnblogs.com/cielosun/p/6582333.html
     * @return 取得的File_Reader实例
     */
    public static File_Reader_Container getInstance(){
        if(instance==null){
            synchronized (File_Reader_Container.class){
                if(instance==null){
                    instance = new File_Reader_Container();
                }
            }
        }
        return instance;
    }


    //读取器的最大个数，即开启的最大线程数
    private static final int MAX_READER_NUMBER = 1;

    //读取器容器数组
    private ArrayList<File_Reader> file_Reader_Array
             = new ArrayList<File_Reader>();

    /**
     * 运行文件读取器将读取到的数据存入
     * 开启多个线程，每个线程开启一个读取器File_Reader
     * 将读好的
     */
    public void run_File_Reader(){

        //初始化读取器数组

        //

    }

    /**
     * 取得从文件中读取的数据列表
     * @return 封装好的byte数据列表
     */
    public ArrayList<byte[]> get_Data_Array(){
        ArrayList<byte[]> dummy = new ArrayList<byte[]>();
        return dummy;
    }

}
