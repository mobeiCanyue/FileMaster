import java.io.*;
import java.net.Socket;

/**
 * @author mobeiCanyue
 * Create  2021-12-25 11:45
 * Describe:
 */
class FileThread implements Runnable {
    private final Socket socket;
    DataOutputStream dos = null;

    public FileThread(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try (
                DataInputStream dis = new DataInputStream(socket.getInputStream())
        ) {
            System.out.println(Thread.currentThread().getName()+"传输开始 ...\n");
            String fileName = dis.readUTF();
            dos = new DataOutputStream(new FileOutputStream(fileName));
            long length = dis.readLong();

            byte[] data = new byte[(int) length];
            for (int i = 0; i < length; i++) {
                data[i] = dis.readByte();
            }
            dos.write(data);
            System.out.println(Thread.currentThread().getName()+"传输完成,接收到来自:" + socket.getInetAddress() + "的文件:");
            System.out.println(new File(fileName).getAbsoluteFile()+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
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
