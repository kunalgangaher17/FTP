import java.io.*;
import java.net.*;

class FTServer {
    private ServerSocket serverSocket;
    private int portNumber;

    FTServer(int portNumber) {
        this.portNumber = portNumber;
        try {
            serverSocket = new ServerSocket(portNumber);
            startListening();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public void startListening() {
        try {
            Socket ck;
            while (true) {
                System.out.println("Server is listening on port :" + portNumber);
                ck = serverSocket.accept();
                System.out.println("Request arrived");
                new RequestProcessor(ck);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String data[]) {
        int portNumber = Integer.parseInt(data[0]);
        FTServer cs = new FTServer(portNumber);
    }
}

class RequestProcessor extends Thread {
    private Socket ck;

    RequestProcessor(Socket ck) {
        this.ck = ck;
        start();
    }

    public void run() {
        try {
            int headerSize = 20;
            byte header[] = new byte[headerSize];
            InputStream inputStream = ck.getInputStream();
            inputStream.read(header);
            byte response[] = {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
            OutputStream outputStream = ck.getOutputStream();
            outputStream.write(response, 0, headerSize);
            outputStream.flush();
            int lengthOfProcessName;
            int e, f;
            lengthOfProcessName = 0;
            e = headerSize - 1;
            f = 1;
            while (e >= 0) {
                lengthOfProcessName = lengthOfProcessName + (header[e] * f);
                e--;
                f = f * 10;
            }
            System.out.println(lengthOfProcessName + "((((");
            int bufferSize = 1024;
            byte bytes[] = new byte[bufferSize];
            int byteRead = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int byteCount;
            while (true) {
                byteCount = inputStream.read(bytes);
                System.out.println(byteCount);
                if (byteCount < 0) break;
                baos.write(bytes, 0, byteCount);
                byteRead += byteCount;
                if (byteRead == lengthOfProcessName) break;
            }
            System.out.println("Serialized form of Process name received");
            bytes = baos.toByteArray();
            outputStream.write(response, 0, headerSize);
            outputStream.flush();
            String process;
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            process = (String) ois.readObject();
            System.out.println("Process :" + process);
            if (process.equalsIgnoreCase("download")) {
                inputStream = ck.getInputStream();
                headerSize = 20;
                header = new byte[headerSize];
                inputStream.read(header);
                outputStream = ck.getOutputStream();
                outputStream.write(response, 0, headerSize);
                outputStream.flush();
                int lengthOfFileName;
                lengthOfFileName = 0;
                e = headerSize - 1;
                f = 1;
                while (e >= 0) {
                    lengthOfFileName = lengthOfFileName + (header[e] * f);
                    e--;
                    f = f * 10;
                }
                System.out.println(lengthOfFileName + "((((");
                bufferSize = 1024;
                bytes = new byte[bufferSize];
                byteRead = 0;
                baos = new ByteArrayOutputStream();
                while (true) {
                    byteCount = inputStream.read(bytes);
                    System.out.println(byteCount);
                    if (byteCount < 0) break;
                    baos.write(bytes, 0, byteCount);
                    byteRead += byteCount;
                    if (byteRead == lengthOfFileName) break;
                }
                System.out.println("Serialized form of file name received");
                bytes = baos.toByteArray();
                outputStream.write(response, 0, headerSize);
                outputStream.flush();
                String fileName;
                bais = new ByteArrayInputStream(bytes);
                ois = new ObjectInputStream(bais);
                fileName = (String) ois.readObject();
                System.out.println("Reciving file :" + fileName);
//file name receive
                File file = new File(fileName);
                long lengthOfFile = file.length();
                int k = headerSize - 1;
                long f1 = lengthOfFile;
                while (k >= 0) {
                    header[k] = (byte) (f1 % 10);
                    f1 = f1 / 10;
                    k--;
                }
                outputStream.write(header, 0, headerSize);
                outputStream.flush();
                System.out.println("Header with length of file sent " + lengthOfFile);
                FileInputStream fileInputStream;
                fileInputStream = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fileInputStream);
                byte contents[] = new byte[1024];
                int bytesRead;
                int i = 0;
                int number = 0;
                while (i < lengthOfFile) {
                    bytesRead = bis.read(contents);
                    if (bytesRead < 0) break;
                    outputStream.write(contents, 0, bytesRead);
                    outputStream.flush();
                    i = i + bytesRead;
                }
                fileInputStream.close();
                System.out.println("bytes of file sent " + i);
                inputStream.read(response);
                ck.close();
                System.out.println("File sent");
            } else {
                if (process.equalsIgnoreCase("upload")) {
                    outputStream = ck.getOutputStream();
                    headerSize = 20;
                    header = new byte[headerSize];
                    inputStream.read(header);
                    outputStream.write(response, 0, headerSize);
                    outputStream.flush();
                    int lengthOfFileName;
                    lengthOfFileName = 0;
                    e = headerSize - 1;
                    f = 1;
                    while (e >= 0) {
                        lengthOfFileName = lengthOfFileName + (header[e] * f);
                        e--;
                        f = f * 10;
                    }
                    System.out.println(lengthOfFileName + "((((");
                    bufferSize = 1024;
                    bytes = new byte[bufferSize];
                    byteRead = 0;
                    baos = new ByteArrayOutputStream();
                    byteCount = 0;
                    while (true) {
                        byteCount = inputStream.read(bytes);
                        System.out.println(byteCount);
                        if (byteCount < 0) break;
                        baos.write(bytes, 0, byteCount);
                        byteRead += byteCount;
                        if (byteRead == lengthOfFileName) break;
                    }
                    System.out.println("Serialized form of file name received");
                    bytes = baos.toByteArray();
                    outputStream.write(response, 0, headerSize);
                    outputStream.flush();
                    String fileName;
                    bais = new ByteArrayInputStream(bytes);
                    ois = new ObjectInputStream(bais);
                    fileName = (String) ois.readObject();
                    System.out.println("Reciving file :" + fileName);
                    inputStream.read(header);
                    outputStream.write(response, 0, headerSize);
                    outputStream.flush();
                    long lengthOfFile;
                    lengthOfFile = 0;
                    e = headerSize - 1;
                    f = 1;
                    while (e >= 0) {
                        lengthOfFile = lengthOfFile + (header[e] * f);
                        e--;
                        f = f * 10;
                    }
                    System.out.println("Length of file :" + lengthOfFile);
                    File file = new File(fileName);
                    if (file.exists()) file.delete();
                    System.out.println("File name :" + file.getName());
                    System.out.println("File path :" + file.getPath());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
                    bytes = new byte[1024];
                    int i = 0;
                    int bytesRead;
                    while (true) {
                        bytesRead = inputStream.read(bytes);
                        if (bytesRead < 0) break;
                        i = i + bytesRead;
                        bos.write(bytes, 0, bytesRead);
                        bos.flush();
                        if (i == lengthOfFile) break;
                    }
                    outputStream.write(response, 0, headerSize);
                    outputStream.flush();
                    ck.close();
                    fileOutputStream.close();
                    System.out.println("File received");
                } else {
                    if (process.equalsIgnoreCase("dir")) {
                        byte response1[] = new byte[1];
                        byte folderNameBytes[] = new byte[1024];
                        inputStream = ck.getInputStream();
                        inputStream.read(response1);
                        outputStream = ck.getOutputStream();
                        File file = new File(".");
                        if (response1[0] == 3) {
                            outputStream.write(response, 0, headerSize);
                            outputStream.flush();
                            inputStream.read(folderNameBytes);
                            ByteArrayInputStream bais1 = new ByteArrayInputStream(folderNameBytes);
                            ObjectInputStream ois1 = new ObjectInputStream(bais1);
                            String folderName = (String) ois1.readObject();
                            System.out.println("Folder name :" + folderName);
                            file = new File(folderName);
                        }
                        File[] files = file.listFiles();
                        long lengthOfFiles = files.length;
                        int k = headerSize - 1;
                        baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(files);
                        bytes = baos.toByteArray();
                        long f1 = bytes.length;
                        while (k >= 0) {
                            header[k] = (byte) (f1 % 10);
                            f1 = f1 / 10;
                            k--;
                        }
                        outputStream.write(header, 0, headerSize);
                        outputStream.flush();
                        bufferSize = 1024;
                        int numberOfBytesToWrite;
                        int i = 0;
                        while (i < bytes.length) {
                            numberOfBytesToWrite = bufferSize;
                            if (i + bufferSize > bytes.length) {
                                numberOfBytesToWrite = bytes.length - i;
                            }
                            outputStream.write(bytes, i, numberOfBytesToWrite);
                            outputStream.flush();
                            i = i + bufferSize;
                        }
                        System.out.println("Files sent");
                    } else {
                        if (process.equalsIgnoreCase("md")) {
                            long lengthOfFileName = 0;
                            f = 1;
                            System.out.println("md server");
                            inputStream = ck.getInputStream();
                            inputStream.read(header);
                            e = headerSize - 1;
                            while (e >= 0) {
                                lengthOfFileName = lengthOfFileName + (header[e] * f);
                                f = f * 10;
                                e--;
                            }
                            System.out.println("Length of file :" + lengthOfFileName);
                            outputStream = ck.getOutputStream();
                            outputStream.write(response, 0, headerSize);
                            outputStream.flush();
                            bufferSize = 1024;
                            bytes = new byte[bufferSize];
                            byteRead = 0;
                            baos = new ByteArrayOutputStream();
                            byteCount = 0;
                            while (true) {
                                byteCount = inputStream.read(bytes);
                                System.out.println("byte count :" + byteCount);
                                if (byteCount < 0) break;
                                baos.write(bytes, 0, byteCount);
                                byteRead += byteCount;
                                if (byteRead == lengthOfFileName) break;
                            }
                            System.out.println("Serialized form of file name received");
                            bytes = baos.toByteArray();
                            outputStream.write(response, 0, headerSize);
                            outputStream.flush();
                            String folderName;
                            bais = new ByteArrayInputStream(bytes);
                            ois = new ObjectInputStream(bais);
                            folderName = (String) ois.readObject();
                            System.out.println("Reciving file :" + folderName);
                            new File(folderName.trim()).mkdir();
                        }
                    }
                }

            }

        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}