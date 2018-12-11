import java.io.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author SENMEN
 * 读取控制器的测试用例
 * 构建一个容纳byte数据的.bin文件，使用读取控制器进行读取
 * 最后匹配读取前后的数据
 * 构造对象后，调用成员函数test_Start()开始测试
 */

public class File_Reader_Controller_Test {
    //日志类
    private static Logger logger = Logger.getLogger(File_Reader_Test.class.getName());


    private static final int DATA_LENGTH = 1000;

    private byte[] data = new byte[DATA_LENGTH];

    private String filename = "Test/file_Reader_Test.bin";
    private String file_Path = "Test";

    //将被测试的读取器
    private File_Reader_Controller file_Reader_Controller_To_Test;

    public File_Reader_Controller_Test(){
        init_Data();
        write_Data_To_File();
        assert check_Data_Written()==true;

        //初始化读取控制器
        file_Reader_Controller_To_Test = File_Reader_Controller.getInstance();
        logger.log(Level.INFO,"读取控制器已初始化");

    }

    /**
     * 开始测试用例函数
     * @return 当测试结果合法时返回true，不合法时返回false
     */
    public boolean test_Start(){

        //初始化并运行读取控制器
        file_Reader_Controller_To_Test.init_File_Readers_Array(filename);
        file_Reader_Controller_To_Test.run_Readers_Threads();

        //比较读取出的数据和正确的文件长度
        int read_Data_Length = 0;
        for(Iterator<byte[]> it = file_Reader_Controller_To_Test.get_Data_Array().iterator();
            it.hasNext();){
            byte[] pres_Bytes = it.next();
            read_Data_Length += pres_Bytes.length;
        }
        if(read_Data_Length != DATA_LENGTH){
            logger.log(Level.WARNING,"检测出错误：读取的数据长度不正确,原长度为"+DATA_LENGTH+
                    "读取的数据长度为"+read_Data_Length);
            return false;
        }
        else{
            logger.log(Level.INFO,"读取的数据长度正确");
        }

        //逐一匹配读取出的数据和原数据内容是否一致
        byte[] data_To_Test = new byte[DATA_LENGTH];
        int data_To_Test_Walker = 0;

        logger.log(Level.INFO,"开始从读取器中转移字节数据");
        for(Iterator<byte[]> it = file_Reader_Controller_To_Test.get_Data_Array().iterator();
            it.hasNext();){
            byte[] pres_Bytes = it.next();
            for(int i=0; i<pres_Bytes.length; i++){
                data_To_Test[data_To_Test_Walker++] = pres_Bytes[i];
            }
        }

        logger.log(Level.INFO,"数据转移完毕，开始匹配");

        boolean return_Flag = true;
        for(int i=0; i<DATA_LENGTH; i++){
            if(data_To_Test[i] != data[i]){
                logger.log(Level.WARNING,"检测出错误：读取的第"+i+"个数据不正确" +
                        "原数据为"+data[i]+"读取后数据为"+data_To_Test[i]);

                for(int j=0; j<DATA_LENGTH; j++){
                    System.out.print("读取前："+data[j]+" ");
                    System.out.println("读取后："+data_To_Test[j]);
                }

                return false;
            }
        }

        logger.log(Level.INFO,"测试完毕，读取控制器组件运行正常");

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
        logger.log(Level.INFO,"测试数组初始化完毕");
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
                logger.log(Level.WARNING,"测试文件存放目录创建失败");
            }

            OutputStream out = new FileOutputStream(filename);

            out.write(data);

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
