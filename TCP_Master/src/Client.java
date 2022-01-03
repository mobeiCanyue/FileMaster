import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author mobeiCanyue
 * Create  2021-12-25 12:01
 * Describe:
 */
public class Client {
    public static void runClient(Scanner sc) {
        //输入IP 和端口号
        String ip = NetFunction.ipMaker(sc);
        String port = NetFunction.portMaker(sc);

        Socket socket = NetFunction.socketMaker(ip, port);
        if (socket == null) {
            System.out.println("尝试连接失败，请检查你的网络或端口号占用情况...");
            System.exit(-1);//如果socket为空就代表连接失败
        }

        File file = NetFunction.fileMaker(sc);
        fileSending(socket, file);
    }

    public static void fileSending(Socket socket, File file) {
        try (
                DataInputStream dis1 = new DataInputStream(new FileInputStream(file));//输入流,读取本地文件
                DataInputStream dis2 = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())//输出流,发送数据流
        ) {
            long length = file.length();//获取文件字节长度,非常好用, 直接避免了重复冗余的for循环读文件
            //---------大文件小文件通用第一步----------
            dos.writeUTF(file.getName());//1.写文件名
            dos.writeLong(length);//2.写文件字节长度
            //-------------------------------------
            if (length < Integer.MAX_VALUE / 2) {
                System.out.println("小文件传输");
                byte[] data = new byte[(int) length];//创建文件字节那么长的字节数组
                dis1.readFully(data);//把文件读到字节数组
                String md5 = NetFunction.checkSum_Hash("MD5", data);//计算哈希值
                dos.write(data);//3.传文件字节
                dos.writeUTF(md5);//4.传输哈希值
                String message = dis2.readUTF();
                System.out.println(message);
            } else {
                /*
                 * 大文件传输不接收服务器反馈
                 */
                System.out.println("大文件传输");
                int len;
                byte[] data = new byte[1024 * 1024 *100];
                while ((len = dis1.read(data)) != -1) {
                    dos.write(data, 0, len);//3.传文件字节
                }
                System.out.println("客户端发送完毕");
            }
            System.out.println("传输结束" + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            NetFunction.closeStream(socket);
        }
    }
}
