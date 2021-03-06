import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author SENMEN
 * 读取器类的测试用例，
 * 构建一个容纳byte数据的.bin文件使用读取器进行读取
 * 最后匹配读取前后的数据
 * 构造对象后，调用成员函数test_Start()开始测试
 */
public class File_Reader_Test {
    //日志类
    private static Logger logger = Logger.getLogger(File_Reader_Test.class.getName());

    private static final int DATA_LENGTH = 1000;

    private byte[] data = new byte[DATA_LENGTH];

    private String filename = "Test/file_Reader_Test.bin";
    private String file_Path = "Test";

    //将被测试的读取器
    private File_Reader file_Reader_To_Test;

    public File_Reader_Test(){
        init_Data();
        write_Data_To_File();
        assert check_Data_Written()==true;

        //初始化读取器
        file_Reader_To_Test = new File_Reader(filename, 0, DATA_LENGTH, 0);
        logger.log(Level.INFO,"读取器已初始化");

    }

    /**
     * 开始测试用例函数
     * @return 当测试结果合法时返回true，不合法时返回false
     */
    public boolean test_Start(){
        //开启新的线程使用读取器读取刚创建的文件file_Reader_Test.bin
        Thread test_Thread = new Thread(file_Reader_To_Test);

        logger.log(Level.INFO,"开启读取线程："+file_Reader_To_Test.getThread_Id());
        test_Thread.start();

        int reading_Timmer = 0;
        int TIME_INTERVAL_READING = 100;

        //输出读取结果
        while(true){
            if(file_Reader_To_Test.is_Read_Finished()){
                logger.log(Level.INFO,"读取完成");

                //检验读取结果的正确性
                for(int i=0; i<file_Reader_To_Test.getData().length; i++){
                    if(file_Reader_To_Test.getData()[i]!=data[i]){
                        logger.log(Level.SEVERE,"检验出读取错误，在第"+i+"个字节处\n" +
                                "文件中的数据为："+data[i]+"\n"+
                                "读取后的数据为："+file_Reader_To_Test.getData()[i]);
                        return false;
                    }
                }
                logger.log(Level.INFO,"测试完毕，读取器组件运行正常");
                break;
            }
            else{
                if(reading_Timmer % TIME_INTERVAL_READING==0){
                    logger.log(Level.INFO,"读取中...");
                }
                reading_Timmer++;
            }
        }

        return true;

    }

    /**
     * 初始化将写入文件的byte数组
     */
    private void init_Data(){
        for( int i = 0; i < DATA_LENGTH; i++ ){
            byte newByte = (byte)(i%128);
            data[i] = newByte;
        }
    }

    /**
     * 将数组data写入.bin文件
     * @return true 写入文件成功; false 写入文件失败
     */
    private boolean write_Data_To_File(){
        try {
            //创建目录
            File file_path = new File(file_Path);
            if(file_path.mkdirs()){
                logger.log(Level.INFO,"测试文件存放目录已创建");
            }
            else{
                logger.log(Level.SEVERE,"测试文件存放目录创建失败");
            }

            OutputStream out = new FileOutputStream(filename);

            for(int i = 0; i < data.length; i++){
                out.write(data[i]);
            }

            System.out.println();
            out.close();

            logger.log(Level.INFO,"测试文件写入完毕");
            return true;

        }catch (IOException e){
            logger.log(Level.SEVERE,"测试数据写入文件失败！",e);
            return false;
        }

    }

    /**
     * 重新读取写完的文件，检查其正确性
     * @return 文件正确为真，反之为假
     */
    private boolean check_Data_Written(){

        try{
            InputStream in = new FileInputStream(filename);

            byte[] data_Reread = new byte[DATA_LENGTH];

            int data_Read_Length = in.read(data_Reread);

            //检查文件长度
            if(data_Read_Length!=DATA_LENGTH){
                logger.log(Level.SEVERE,"测试文件长度出错！");
                return false;
            }

            //检查数据正确性

            for(int i=0;i<DATA_LENGTH;i++){
                if(data[i]!=data_Reread[i]){
                    logger.log(Level.SEVERE,"测试文件内容出错！");
                    return false;
                }
            }

        }catch (FileNotFoundException e){
            logger.log(Level.SEVERE,"测试文件创建失败！",e);
        }catch (IOException e){
            logger.log(Level.SEVERE,"测试文件字节数读取失败！",e);
        }

        return true;
    }


}
