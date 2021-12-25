import java.io.*;
import java.net.Socket;

/**
 * @author mobeiCanyue
 * Create  2021-12-25 11:45
 * Describe:
 */
class FileThread implements Runnable {
    private final Socket socket;

    public FileThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                DataInputStream dis = new DataInputStream(socket.getInputStream())
        ) {
            String fileName = dis.readUTF();
            long length = dis.readLong();
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName));

            byte[] data = new byte[(int) length];
            for (int i = 0; i < length; i++) {
                data[i] = dis.readByte();
            }
            dos.write(data);
            System.out.println("Transfer is complete, get the file from\n" + socket.getInetAddress());
            System.out.println(new File(fileName).getAbsoluteFile());
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
