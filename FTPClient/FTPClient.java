import java.io.*;
import java.net.*;

class psp {
    public static void main(String gg[]) {
        Socket socket = null;
        String server = gg[0];
        int port = Integer.parseInt(gg[1]);
        Console console = System.console();
        String command;
        String process = "";
        System.out.println("FTP client version 1.0");
        while (true) {
            command = console.readLine("ftc>");
            System.out.println("Command :" + command);
            try {
                socket = new Socket(server, port);
            } catch (Exception e) {
                System.out.println("Connection exception :" + e);
            }
            if (command.equalsIgnoreCase("bye")) {
                try {
                    socket.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
            } else {
                try {
                    OutputStream os = socket.getOutputStream();
                    String[] strings = command.split(" ");
                    process = "";
                    if (strings.length == 1) {
                        process = command;
                    } else {
                        process = command.substring(0, command.indexOf(" "));
                    }
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    ObjectOutputStream o = new ObjectOutputStream(b);
                    o.writeObject(process);
                    byte processBytes[] = b.toByteArray();
                    int processBytesLength = processBytes.length;
                    int headerSize = 20;
                    byte response[] = new byte[20];
                    byte header[];
                    header = new byte[headerSize];
                    int k = headerSize - 1;
                    long f = processBytesLength;
                    while (k >= 0) {
                        header[k] = (byte) (f % 10);
                        f = f / 10;
                        k--;
                    }
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(header, 0, headerSize);
                    outputStream.flush();
                    InputStream inputStream = socket.getInputStream();
                    inputStream.read(response);
                    outputStream.write(processBytes, 0, processBytesLength);
                    outputStream.flush();
                    inputStream.read(response);
                } catch (Exception exception) {
                    System.out.println("Else exception: " + exception);
                }
                if (process.equalsIgnoreCase("upload")) {
                    try {
                        String filePath = command.substring(command.indexOf(" "), command.length()).trim();
                        File file;
                        File file1 = new File("FolderName.txt");
                        String path = "";
                        if (file1.exists()) {
                            RandomAccessFile raf = new RandomAccessFile(file1, "rw");
                            path = raf.readLine().trim();
                        }
                        file = new File(filePath);
                        if (file.exists() == false) {
                            System.out.println("File not found :" + filePath);
                            return;
                        }
                        System.out.println("Uploading....................");
                        String fileName = path + "\\" + file.getName();
                        System.out.println("File name and path :"+fileName);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(fileName);
                        byte[] fileNameByteArray;
                        fileNameByteArray = baos.toByteArray();
                        int lengthOfFileName = fileNameByteArray.length;
                        int headerSize = 20;
                        byte header[];
                        header = new byte[headerSize];
                        int k = headerSize - 1;
                        long f = lengthOfFileName;
                        while (k >= 0) {
                            header[k] = (byte) (f % 10);
                            f = f / 10;
                            k--;
                        }
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
                        System.out.println("Uploaded");
                        socket.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else {
                    if (process.equalsIgnoreCase("download")) {
                        try {
                            System.out.println("Downloading..............");
                            String filePath = command.substring(command.indexOf(" "), command.length()).trim();
                            File file = new File(filePath);
                            File folderNameFile = new File("FolderName.txt");
                            String fileName = file.getName();
                            String folderName;
                            if (folderNameFile.exists()) {
                                RandomAccessFile raf = new RandomAccessFile(folderNameFile, "rw");
                                folderName = raf.readLine().trim();
                                fileName = folderName + "/" + fileName;
                                raf.close();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                            oos.writeObject(fileName);
                            byte[] fileNameByteArray;
                            fileNameByteArray = baos.toByteArray();
                            int lengthOfFileName = fileNameByteArray.length;
                            int headerSize = 20;
                            byte header[];
                            header = new byte[headerSize];
                            int k = headerSize - 1;
                            long f = lengthOfFileName;
                            while (k >= 0) {
                                header[k] = (byte) (f % 10);
                                f = f / 10;
                                k--;
                            }
                            byte response[] = new byte[20];
                            OutputStream outputStream;
                            outputStream = socket.getOutputStream();
                            outputStream.write(header, 0, headerSize);
                            outputStream.flush();
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
                            inputStream.read(header);
                            long lengthOfFile;
                            lengthOfFile = 0;
                            int e = headerSize - 1;
                            f = 1;
                            while (e >= 0) {
                                lengthOfFile = lengthOfFile + (header[e] * f);
                                e--;
                                f = f * 10;
                            }
                            file = new File(filePath);
                            if (file.exists()) file.delete();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
                            byte[] bytes = new byte[1024];
                            i = 0;
                            int bytesRead;
                            while (true) {
                                bytesRead = inputStream.read(bytes);
                                if (bytesRead < 0) break;
                                i = i + bytesRead;
                                bos.write(bytes, 0, bytesRead);
                                bos.flush();
                                if (i == lengthOfFile) break;
                            }
                            System.out.println("downloaded");
                            fileOutputStream.close();
                            socket.close();
                        } catch (Exception exception) {
                            System.out.println("Exception download if :" + exception);
                        }
                    } else {
                        if (process.equalsIgnoreCase("dir")) {
                            try {
                                File file1 = new File("FolderName.txt");
                                byte[] response = new byte[1];
                                response[0] = 3;
                                byte[] response1 = new byte[20];
                                OutputStream outputStream;
                                outputStream = socket.getOutputStream();
                                InputStream inputStream = socket.getInputStream();
                                byte[] bytes;
                                if (file1.exists()) {
                                    RandomAccessFile raf = new RandomAccessFile(file1, "rw");
                                    String folderName = raf.readLine();
                                    raf.close();
                                    outputStream.write(response, 0, response.length);
                                    outputStream.flush();
                                    inputStream.read(response1);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                                    oos.writeObject(folderName);
                                    bytes = baos.toByteArray();
                                    outputStream.write(bytes, 0, bytes.length);
                                    outputStream.flush();
                                } else {
                                    response[0] = 4;
                                    outputStream.write(response, 0, response.length);
                                    outputStream.flush();
                                }
                                byte header[] = new byte[20];
                                int headerSize = 20;
                                inputStream.read(header);
                                long lengthOfFiles = 0;
                                int e = headerSize - 1;
                                int f = 1;
                                while (e >= 0) {
                                    lengthOfFiles = lengthOfFiles + (header[e] * f);
                                    f = f * 10;
                                    e--;
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] b = new byte[1024];
                                int byteCount;
                                while (true) {
                                    byteCount = inputStream.read(b);
                                    if (byteCount < 0) break;
                                    baos.write(b, 0, byteCount);
                                    if (byteCount == lengthOfFiles) break;
                                }
                                b = baos.toByteArray();
                                ByteArrayInputStream bais = new ByteArrayInputStream(b);
                                ObjectInputStream ois = new ObjectInputStream(bais);
                                File files[] = (File[]) ois.readObject();
                                for (File file : files) {
                                    if (!file.getName().contains(".")) {
                                        System.out.println("Directory :" + file.getName());
                                    } else {
                                        System.out.println("File :" + file.getName());
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Exception while showing directories :" + e);
                            }
                        } else {
                            if (process.equalsIgnoreCase("md")) {
                                try {
                                    String folderName = command.substring(command.indexOf(" "), command.length());
                                    System.out.println("Folder name :" + folderName);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                                    oos.writeObject(folderName.trim());
                                    oos.flush();
                                    byte folderNameBytes[] = baos.toByteArray();
                                    long folderNameLength = folderNameBytes.length;
                                    int headerSize = 20;
                                    byte header[] = new byte[headerSize];
                                    int k = headerSize - 1;
                                    long f = folderNameLength;
                                    while (k >= 0) {
                                        header[k] = (byte) (f % 10);
                                        f = f / 10;
                                        k--;
                                    }
                                    byte response[] = new byte[20];
                                    OutputStream outputStream = socket.getOutputStream();
                                    outputStream.write(header, 0, headerSize);
                                    outputStream.flush();
                                    InputStream is = socket.getInputStream();
                                    is.read(response);
                                    int i;
                                    int bufferSize = 1024;
                                    int numberOfBytesToWrite = bufferSize;
                                    i = 0;
                                    while (i < folderNameBytes.length) {
                                        if (i + bufferSize > folderNameBytes.length) {
                                            numberOfBytesToWrite = folderNameBytes.length - i;
                                        }
                                        outputStream.write(folderNameBytes, i, numberOfBytesToWrite);
                                        outputStream.flush();
                                        i = i + bufferSize;
                                    }
                                    is.read(response);
                                } catch (Exception e) {
                                    System.out.println("Exception at md command :" + e.getMessage());
                                }
                            } else {
                                if (process.equalsIgnoreCase("cd")) {
                                    try {
                                        String folderName = command.substring(command.indexOf(" "), command.length());
                                        File file = new File("FolderName.txt");
                                        if (file.exists()) {
                                            file.delete();
                                        }
                                        RandomAccessFile raf = new RandomAccessFile(file, "rw");
                                        raf.writeBytes(folderName.trim());
                                        raf.close();
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                }//if of cd
                                else {
                                    if (process.trim().equalsIgnoreCase("cd..")) {
                                        try {
                                            System.out.println("In cd..");
                                            File file = new File("FolderName.txt");
                                            System.out.println(file.exists());
                                            if (file.exists()) {
                                                file.delete();
                                                System.out.println("File deleted");
                                            }
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }
                                    }//if of cd..
                                }
                            }
                        }
                    }
                }
//else end
            }
        }

    }//main end
}//class end