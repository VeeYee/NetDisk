package edu.hbuas.netdisk.model;

//封装一个网盘消息类型

import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message implements Serializable {

    private String from;
    private MessageType type;
    private String fileName;
    private long fileLength;
    private Set<ClientFile> fileList;

}
