package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.dao.TransferDAO;
import edu.hbuas.netdisk.dao.TransferDAOImpl;
import edu.hbuas.netdisk.dao.UserDAO;
import edu.hbuas.netdisk.dao.UserDAOImpl;
import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.Transfer;
import edu.hbuas.netdisk.model.User;
import lombok.AllArgsConstructor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//网盘服务器
public class Server {

    private UserDAO dao;
    private TransferDAO tfdao;
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
                tfdao = new TransferDAOImpl();
                System.out.println(client.getInetAddress().getHostAddress()+"连接进来了");
                ReceivedMessage rm = new ReceivedMessage(in,out);
                rm.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AllArgsConstructor
    //接收客户端消息的线程
    private class ReceivedMessage extends Thread {
        private ObjectInputStream in;
        private ObjectOutputStream out;

        @Override
        public void run() {
            //不断接收客户端发来的消息
            try {
                Message message = (Message) in.readObject();
                //用户文件目录
                File userDir = new File("D:/NetDiskFile/" + message.getFrom().getUsername());
                if(!userDir.exists())  userDir.mkdir();
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
                        System.out.println("服务器收到用户注册消息");
                        boolean result = dao.registerUser(message.getFrom());
                        //插入失败写回空对象
                        if(!result) message.setFrom(null);
                        out.writeObject(message);
                        out.flush();
                        break;
                    }
                    //更新界面的消息
                    case UPDATE: {
                        System.out.println("服务器收到更新界面的消息");
                        //遍历用户目录下的文件
                        File[] files = userDir.listFiles();
                        LinkedList<File> fileList = new LinkedList<>();
                        for(File f:files){
                            fileList.add(f);
                        }
                        Message updateResult = new Message();
                        updateResult.setFileList(fileList);  //文件列表传给客户端
                        out.writeObject(updateResult);
                        out.flush();
                        break;
                    }
                    //上传文件的消息
                    case UPLOAD: {
                        System.out.println("服务器收到一条上传文件的消息");
                        //通过通道流读取文件信息，再通过字节流从内存写向磁盘
                        FileOutputStream fileOut = new FileOutputStream(userDir + "/" + message.getFileName().split("   ")[1]);
                        byte[] bs = new byte[1024];
                        int length = -1;
                        while ((length = in.read(bs)) != -1) {
                            fileOut.write(bs,0,length);
                            fileOut.flush();
                        }
                        fileOut.close();
                        in.close();
                        //向传输记录表插入一条数据
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); //获取当前时间
                        Transfer record = new Transfer(0,message.getFrom().getUsername(),message.getFileName().split("   ")[1],
                                message.getFileLength(),message.getFileName().split("   ")[0],time,"上传");
                        tfdao.addRecode(record);
                        break;
                    }
                    //下载文件的消息
                    case DOWNLOAD: {
                        System.out.println("服务器收到一条下载文件的消息");
                        //字节流从磁盘读取文件写向内存，再通过通道流写向客户端
                        File file = new File(userDir +"/" +message.getFileName().split("   ")[1]);  //目录+文件名
                        FileInputStream fileIn = new FileInputStream(file);
                        byte[] bs = new byte[1024];
                        int length = -1;
                        while((length = fileIn.read(bs)) != -1) {
                            out.write(bs,0,length);
                            out.flush();
                        }
                        fileIn.close();
                        out.close();  //关闭流！！
                        //向传输记录表中插入一条数据
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());  //获取当前时间
                        Transfer record = new Transfer(0,message.getFrom().getUsername(),message.getFileName().split("   ")[1],
                                file.length(),message.getFileName().split("   ")[0],time,"下载");
                        tfdao.addRecode(record);
                        break;
                    }
                    //删除文件的消息
                    case DELETE:{
                        System.out.println("服务器收到一条删除文件的消息");
                        File file = new File(userDir+"/"+message.getFileName());
                        file.delete();
                        break;
                    }
                    //重命名文件的消息
                    case RENAME:{
                        System.out.println("服务器收到一条重命名文件的消息");
                        String beforeName = message.getFileName().split("   ")[0];
                        String afterName = message.getFileName().split("   ")[1];
                        File beforeFile = new File(userDir + "/" + beforeName);
                        File afterFile = new File(userDir+ "/" +afterName);
                        beforeFile.renameTo(afterFile);
                        break;
                    }
                    //获取传输记录的方法
                    case TRANSFER:{
                        System.out.println("服务器收到一条获取传输记录的消息");
                        List<Transfer> list = tfdao.getAllRecords(message.getFrom().getUsername());
                        LinkedList<Transfer> recordList = new LinkedList<>();
                        for (Transfer t:list){
                            recordList.add(t);
                        }
                        message.setRecordList(recordList);
                        out.writeObject(message);
                        out.flush();
                        break;
                    }
                    //删除文件传输记录的消息
                    case DELETERECORD:{
                        System.out.println("服务器收到一条删除传输记录的消息");
                        if(tfdao.deleteRecode(message.getFileLength())){
                            message.setFileName("true");
                        }else{
                            message.setFileName("false");
                        }
                        out.writeObject(message);
                        out.flush();
                        break;
                    }
                    default:{
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
