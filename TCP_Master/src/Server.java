import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mobeiCanyue
 * Create  2021-12-25 12:01
 * Describe:
 */
public class Server {
    public static void runServer(Scanner sc) throws Exception {
        System.out.println("服务器开始运行...\n");
        System.out.println("当前服务器的IP地址是:" + InetAddress.getLocalHost().getHostAddress() + "\n");

        String port = NetFunction.portMaker(sc);

        System.out.println("文件将在当前项目下保存...\n");

        ServerSocket ss = new ServerSocket(Integer.parseInt(port));//服务器套接字

        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();//创建一个缓存线程池
        //ExecutorService fixedThreadPool = Executors.newFixedThreadPool(6);//创建一个固定线程线程大小的线程池
        while (true) {
            Socket socket = ss.accept();//服务器端口接受socket套节字
            //new Thread(new FileThread(socket)).start();//这种写法会频繁地创建和销毁线程,效率低
            cachedThreadPool.execute(new FileThread(socket));
        }
    }
}
