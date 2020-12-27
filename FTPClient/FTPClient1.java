import java.io.*;
import java.net.*;

class FTClient {
    public static void main(String data[]) {
        try {
            String server = data[0];
            int port = Integer.parseInt(data[1]);
            String filePath = data[2];
            File file = new File(filePath);
            if (file.exists() == false) {
                System.out.println("File not found :" + filePath);
                return;
            }
            String fileName = file.getName();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(fileName);
            System.out.println(fileName);
            byte[] fileNameByteArray;
            fileNameByteArray = baos.toByteArray();
            int lengthOfFileName = fileNameByteArray.length;
            int headerSize = 20;
            byte header[];
            header = new byte[headerSize];
            int k = headerSize - 1;
            long f = lengthOfFileName;
            System.out.println(lengthOfFileName);
            while (k >= 0) {
                header[k] = (byte) (f % 10);
                f = f / 10;
                k--;
            }
            Socket socket = new Socket(server, port);
            OutputStream outputStream;
            outputStream = socket.getOutputStream();
            outputStream.write(header, 0, headerSize);
            outputStream.flush();
            byte response[] = new byte[headerSize];
            InputStream inputStream = socket.getInputStream();
            inputStream.read(response);
            int i;
            int bufferSize = 1024;
            int numberOfBytesToWrite = bufferSize;
            i = 0;
            while (i < fileNameByteArray.length) {
                if (i + bufferSize > fileNameByteArray.length) {
                    numberOfBytesToWrite = fileNameByteArray.length - i;
                }
                outputStream.write(fileNameByteArray, i, numberOfBytesToWrite);
                outputStream.flush();
                inputStream.read(header);
                i = i + bufferSize;
            }
            long lengthOfFile = file.length();
            System.out.println(lengthOfFile);
            k = headerSize - 1;
            f = lengthOfFile;
            while (k >= 0) {
                header[k] = (byte) (f % 10);
                f = f / 10;
                k--;
            }
            outputStream.write(header, 0, headerSize);
            outputStream.flush();
            inputStream.read(response);
            System.out.println("Header with length of file sent " + lengthOfFile);
            FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fileInputStream);
            byte contents[] = new byte[1024];
            int bytesRead;
            i = 0;
            int number = 0;
            while (i < lengthOfFile) {
                bytesRead = bis.read(contents);
                if (bytesRead < 0) break;
                outputStream.write(contents, 0, bytesRead);
                outputStream.flush();
                i = i + bytesRead;
            }
            fileInputStream.close();
            inputStream.read(response);
            socket.close();
            System.out.println("File sent");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}