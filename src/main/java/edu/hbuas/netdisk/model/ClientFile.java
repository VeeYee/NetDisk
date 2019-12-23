package edu.hbuas.netdisk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientFile implements Serializable {
    private String username;
    private String fileName;
    private long fileLength;
}
