import java.io.*;
import java.net.Socket;

/**
 * @author mobeiCanyue
 * Create  2021-12-25 11:45
 * Describe:
 */
class FileThread implements Runnable {
    private final Socket socket;
    DataOutputStream dos1 = null;//写文件到本地的
    DataOutputStream dos2 = null;//给客户端反馈的

    public FileThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                DataInputStream dis1 = new DataInputStream(socket.getInputStream())
        ) {
            System.out.print(Thread.currentThread().getName() + "传输开始 ...");
            System.out.println("客户端的地址为:" + socket.getLocalAddress() + "\n");

            //---------大文件小文件通用第一步----------
            String fileName = dis1.readUTF();//1.文件名
            File file = new File(fileName);
            dos1 = new DataOutputStream(new FileOutputStream(fileName));//写文件的
            long length = dis1.readLong();//2.文件字节长度
            //-------------------------------------

            if (length < Integer.MAX_VALUE / 2) {//如果小于1G
                System.out.println("小文件传输");
                byte[] data = new byte[(int) length];

                //3.读文件
                // (1)用这个方法,解决了循环读取的问题,大大提高效率
                // (2)解决了不用Buffer流导致文件传输过慢的问题
                dis1.readFully(data);
                String raw_md5 = dis1.readUTF();//4.接收文件哈希值
                dos1.write(data);//写文件

                String md5 = NetFunction.checkSum_Hash("MD5", data);
                System.out.println(Thread.currentThread().getName() + "文件的MD5哈希值:" + md5);

                //给客户端反馈
                dos2 = new DataOutputStream(socket.getOutputStream());//给客户端发消息的
                if (raw_md5.equals(md5)) {
                    System.out.println(Thread.currentThread().getName() + "哈希值校验成功,文件传输无误");
                    dos2.writeUTF("来自服务器:[已收到文件:" + fileName + "]");
                } else {
                    System.out.println(Thread.currentThread().getName() + "传输文件失败,哈希值与文件不同");
                    dos2.writeUTF("来自服务器:[传输文件失败,哈希值与文件不同]");
                }
            } else {
                System.out.println("大文件传输");
                int len;
                byte[] data = new byte[1024 * 1024 *100];
                while ((len = dis1.read(data)) != -1) {
                    dos1.write(data, 0, len);
                }
                System.out.println("接收完毕");
                /*
                 * 大文件传输不接收服务器反馈
                 */
            }
            System.out.print(Thread.currentThread().getName() + "传输完成,接收到来自:" + socket.getInetAddress() + "的文件:");
            System.out.println(file.getAbsoluteFile() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n与客户端连接异常...\n");
        } finally {
            NetFunction.closeStream(dos1);
            NetFunction.closeStream(dos2);
            NetFunction.closeStream(socket);
        }
    }
}
