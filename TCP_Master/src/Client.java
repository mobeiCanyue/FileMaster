import java.io.*;
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

        File file = fileMaker(sc);

        Socket socket = socketMaker(ip, port);
        if (socket == null) {
            System.out.println("尝试连接失败，请检查你的网络或端口号占用情况...");
            System.exit(-1);
        }



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
            System.out.println("传输结束");
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

    public static String ipMaker(Scanner sc) {
        System.out.println("请输入用于传输的IP地址, 默认为(输入-1)为本地回环地址:");
        String ip = sc.nextLine();
        if ("-1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public static String portMaker(Scanner sc) {
        System.out.println("请输入用于传输的端口号, 默认(输入-1)为9980:");
        String port = sc.nextLine();
        if ("-1".equals(port)) {
            port = "9980";
        }
        return port;
    }

    public static Socket socketMaker(String ip, String port) {
        for (int i = 5; i >= 0; i--) {
            try {
                System.out.println("开始连接验证...");
                Socket socket = new Socket(ip, Integer.parseInt(port));//如果连接失败就抛异常
                System.out.println("连接成功 ! ! !");
                return socket;//如果连接成功就返回对象
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("\nTCP 连接失败... 剩余的连接次数:" + i);
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
        System.out.println("请输入文件名称 (放在当前目录下或者输入绝对路径):");
        File file = new File(sc.nextLine());
        while (!file.exists() && !file.isFile()) {
            System.out.println("文件不存在, 请放在当前目录下或者输入正确的路径 !");
            file = new File(sc.nextLine());
        }
        return file;
    }
}
