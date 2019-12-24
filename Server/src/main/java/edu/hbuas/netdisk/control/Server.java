package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.dao.UserDAO;
import edu.hbuas.netdisk.dao.UserDAOImpl;
import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.User;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

//网盘服务器
public class Server {

    private UserDAO dao;
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
                dao = new UserDAOImpl();
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
                    //登录消息
                    case LOGIN:{
                        System.out.println("服务器收到用户登录的消息");
                        User LoginUser = dao.login(message.getFrom().getUsername(),message.getFrom().getPassword());
                        message.setFrom(LoginUser);
                        out.writeObject(message);
                        out.flush();
                        break;
                    }
                    //注册消息
                    case REGISTER:{
                        System.out.println("服务器收到注册消息");
                        boolean result = dao.registerUser(message.getFrom());
                        //插入失败写回空对象
                        if(!result){
                            message.setFrom(null);
                        }
                        out.writeObject(message);
                        out.flush();
                        break;
                    }
                    //上传文件的消息
                    case UPLOAD: {
                        System.out.println("服务器收到一条上传文件的消息");
                        //上传的文件写到D盘
                        File userDir = new File("D:/NetDiskFile/" + message.getFrom().getUsername());
                        if (!userDir.exists()) userDir.mkdir();
                        FileOutputStream out = new FileOutputStream(userDir + "/" + message.getFileName());
                        byte[] bs = new byte[1024];
                        int length = -1;
                        while ((length = in.read(bs)) != -1) {
                            out.write(bs,0,length);
                            out.flush();
                        }
                        out.close();
//                        in.close();
                        break;
                    }
                    //下载文件的消息
                    case DOWNLOAD: {
                        System.out.println("服务器收到一条下载文件的消息");
                        break;
                    }
                    //更新界面的消息
                    case UPDATE: {
                        System.out.println("服务器收到更新界面的消息");
                        //登录成功，初始化界面
                        File file = new File("D:/NetDiskFile/" + message.getFrom().getUsername());
                        if(!file.exists())  file.mkdir();
                        //遍历用户目录下的文件
                        File[] files = file.listFiles();
                        Set<File> fileList = new HashSet<File>();
                        for(File f:files){
                            fileList.add(f);
                        }
                        Message updateResult = new Message();
                        updateResult.setFileList(fileList);  //文件列表传给客户端
                        out.writeObject(updateResult);
                        out.flush();
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
