import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Scanner;

/**
 * @author mobeiCanyue
 * Create  2021-12-25 12:01
 * Describe:
 */
public class Client {
    public static void runClient(Scanner sc) {
        String ip = NetFunction.ipMaker(sc);
        String port = NetFunction.portMaker(sc);

        Socket socket = NetFunction.socketMaker(ip, port);
        if (socket == null) {
            System.out.println("尝试连接失败，请检查你的网络或端口号占用情况...");
            System.exit(-1);
        }

        File file = NetFunction.fileMaker(sc);
        fileSending(socket, file);
    }

    public static void fileSending(Socket socket, File file) {
        try (
                DataInputStream dis1 = new DataInputStream(new FileInputStream(file));
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis2 = new DataInputStream(socket.getInputStream())
        ) {
            long length = file.length();//获取文件字节长度
            byte[] data = new byte[(int) length];

            dos.writeUTF(file.getName());//1.
            dos.writeLong(length);//2.

            dis1.readFully(data);//把文件读到内存

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            byte[] digest = md.digest();
            String md5 = NetFunction.byteArrayToHexString(digest);

            dos.writeUTF(md5);//3.传输哈希值
            dos.write(data);//4.传文件字节

            String s = dis2.readUTF();
            System.out.println("\n"+s);
            System.out.println("传输结束"+"\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
