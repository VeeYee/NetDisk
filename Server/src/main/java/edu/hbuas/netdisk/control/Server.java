package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.dao.FileDAO;
import edu.hbuas.netdisk.model.ClientFile;
import edu.hbuas.netdisk.model.Message;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//网盘服务器
public class Server {

    private ServerSocket serverSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    {
        try {
            //初始化连接
            serverSocket = new ServerSocket(8123);
            System.out.println("服务器启动成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(){
        //接受客户端的连接
        try {
            while (true) {
                Socket client = serverSocket.accept();
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());
                System.out.println(client.getInetAddress().getHostAddress()+"连接进来了");
                ReceivedMessage rm = new ReceivedMessage(in,out);
                rm.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AllArgsConstructor
    //结束客户端消息的线程
    private class ReceivedMessage extends Thread {
        private ObjectInputStream in;
        private ObjectOutputStream out;

        @Override
        public void run() {
            //不断接收客户端发来的消息
            try {
                Message message = (Message) in.readObject();
                switch (message.getType()) {
                    case UPLOAD: {
                        System.out.println("服务器收到一条上传文件的消息");
                        //上传的文件插入到数据库
                        FileDAO fileDAO = new FileDAO();
                        fileDAO.upload(new ClientFile(message.getFrom(), message.getFileName(), message.getFileLength()));
                        //上传的文件写到D盘
                        File userDir = new File("D:/NetDiskFile/" + message.getFrom());
                        if (!userDir.exists()) userDir.mkdir();
                        FileOutputStream out = new FileOutputStream(userDir + "/" + message.getFileName());
                        byte[] bs = new byte[1024];
                        int length = -1;
                        while ((length = in.read(bs)) != -1) {
                            out.write(bs);
                            out.flush();
                        }
                        out.close();
                        in.close();
                        break;
                    }
                    case DOWNLOAD: {
                        System.out.println("服务器收到一条下载文件的消息");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
