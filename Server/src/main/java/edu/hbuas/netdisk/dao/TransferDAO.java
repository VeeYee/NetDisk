package edu.hbuas.netdisk.dao;

import edu.hbuas.netdisk.model.Transfer;

import java.util.List;

public interface TransferDAO {
    boolean addRecode(Transfer transfer);
    boolean deleteRecode(long id);
    List<Transfer> getAllRecords(String username);
}
