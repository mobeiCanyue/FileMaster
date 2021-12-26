import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;

/**
 * @author mobeiCanyue
 * Create  2021-12-25 11:45
 * Describe:
 */
class FileThread implements Runnable {
    private final Socket socket;
    DataOutputStream dos1 = null;

    public FileThread(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try (
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos2 = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println(Thread.currentThread().getName() + "传输开始 ...\n");

            String fileName = dis.readUTF();//1.文件名
            dos1 = new DataOutputStream(new FileOutputStream(fileName));

            long length = dis.readLong();//2.文件字节长度

            byte[] data = new byte[(int) length];

            String raw_md5 = dis.readUTF();//3.

            dis.readFully(data);//4.解决了不用Buffer流文件传输过慢的问题

            dos1.write(data);

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            byte[] digest = md.digest();
            String md5 = NetFunction.byteArrayToHexString(digest);

            System.out.println("文件的MD5哈希值:" + md5 +"\n");

            if (raw_md5.equals(md5)) {
                dos2.writeUTF("来自服务器:[已收到文件:" + fileName + "]");
            } else {
                dos2.writeUTF("来自服务器:[接受文件失败]");
            }
            System.out.println(Thread.currentThread().getName() + "传输完成,接收到来自:" + socket.getInetAddress() + "的文件:");
            System.out.println(new File(fileName).getAbsoluteFile() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos1 != null) {
                try {
                    dos1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
