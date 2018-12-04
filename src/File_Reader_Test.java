import java.io.*;

public class File_Reader_Test {

    private static final int DATA_LENGTH = 1000;

    private byte[] data = new byte[DATA_LENGTH];

    private String filename = "../Test/file_Reader_Test.bin";

    public File_Reader_Test(){
        init_Data();
        write_Data_To_File();
    }

    /**
     * 初始化将写入文件的byte数组
     */
    private void init_Data(){
        for( byte i = 0; i < DATA_LENGTH; i++ ){
            byte newByte = i;
            data[i] = newByte;
        }
    }

    /**
     * 将数组data写入.bin文件
     */
    private void write_Data_To_File(){
        try {
            OutputStream out = new FileOutputStream(filename);

            for(int i = 0; i < data.length; i++){
                out.write(data[i]);
            }


            out.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void check_Data_Written(){
        try{
            InputStream in = new FileInputStream(filename);
        }catch (FileNotFoundException e){
            System.err.println("测试文件创建失败！");
            log
        }
    }

}
