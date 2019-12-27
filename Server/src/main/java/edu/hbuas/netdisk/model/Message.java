package edu.hbuas.netdisk.model;

//封装一个网盘消息类型

import lombok.*;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message implements Serializable {

    private User from;
    private MessageType type;
    private String fileName;
    private long fileLength;
    private LinkedList<File> fileList;
    private LinkedList<Transfer> recordList;

    public Message(User from, MessageType type, String fileName, long fileLength) {
        this.from = from;
        this.type = type;
        this.fileName = fileName;
        this.fileLength = fileLength;
    }
}
