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
        String ip = ipMaker(sc);
        String port = portMaker(sc);
        Socket socket = socketMaker(ip, port);
        if (socket == null) {
            System.out.println("尝试连接失败，请检查你的网络或端口号占用情况...");
            System.exit(-1);
        }

        File file = fileMaker(sc);

        fileSending(socket, file);
    }

    public static void fileSending(Socket socket, File file) {
        try (
                DataInputStream dis = new DataInputStream(new FileInputStream(file));
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());
            int len;
            byte[] data = new byte[1024 * 1024];
            while ((len = dis.read(data)) != -1) {
                dos.write(data, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String ipMaker(Scanner sc) {
        System.out.println("Please enter the IP address used for transmission, \nThe default (input -1) is the local loopback address:");
        String ip = sc.nextLine();
        if ("-1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public static String portMaker(Scanner sc) {
        System.out.println("Please enter the server transmission TCP port number, \nThe default (input -1) is 9980:");
        String port = sc.nextLine();
        if ("-1".equals(port)) {
            port = "9980";
        }
        return port;
    }

    public static Socket socketMaker(String ip, String port) {
        for (int i = 5; i >= 0; i--) {
            try {
                System.out.println("Perform connection verification...");
                Socket socket = new Socket(ip, Integer.parseInt(port));//如果连接失败就抛异常
                System.out.println("connection succeeded!");
                return socket;//如果连接成功就返回对象
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("\nTCP connection failed... remaining connections:" + i);
                try {
                    Thread.sleep(1000); //等一秒再尝试重连
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public static File fileMaker(Scanner sc) {
        System.out.println("Please enter the file name \n(Put it in the directory or enter the absolute path):");
        File file = new File(sc.nextLine());
        while (!file.exists() && !file.isFile()) {
            System.out.println("The file does not exist, \nPlease put it in the directory or enter the correct path!");
            file = new File(sc.nextLine());
        }
        return file;
    }
}
