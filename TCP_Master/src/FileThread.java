import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;

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
            String fileName = dis.readUTF();
            dos1 = new DataOutputStream(new FileOutputStream(fileName));
            long length = dis.readLong();

            byte[] data = new byte[(int) length];
            dis.readFully(data);//解决了不用Buffer流文件传输过慢的问题
            dos1.write(data);

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            byte[] digest = md.digest();
            String s = byteArrayToHexString(digest);
            System.out.println(s);

            System.out.println("文件的哈希值:"+ s);

            dos2.writeUTF("来自服务器:[已收到文件:" + fileName + "]");
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



    /**
     * 字节数组转十六进制:  https://www.jianshu.com/p/b419163272c1
     * 将1个字节（1 byte = 8 bit）转为 2个十六进制位
     * 1个16进制位 = 4个二进制位 （即4 bit）
     * 转换思路：最简单的办法就是先将byte转为10进制的int类型，然后将十进制数转十六进制
     */
    private static String byteToHexString(byte b) {
        final String[] hexDigits = {"0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

        // byte类型赋值给int变量时，java会自动将byte类型转int类型，从低位类型到高位类型自动转换
        int n = b;

        // 将十进制数转十六进制
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;

        // d1和d2通过访问数组变量的方式转成16进制字符串；比如 d1 为12 ，那么就转为"c"; 因为int类型不会有a,b,c,d,e,f等表示16进制的字符
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 将字节数组里每个字节转成2个16进制位的字符串后拼接起来
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte value : b) {
            resultSb.append(byteToHexString(value));
        }
        return resultSb.toString().toUpperCase();
    }
}
