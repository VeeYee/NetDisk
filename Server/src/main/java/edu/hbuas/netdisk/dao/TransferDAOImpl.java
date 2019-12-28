package edu.hbuas.netdisk.dao;

import edu.hbuas.netdisk.model.Transfer;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class TransferDAOImpl implements TransferDAO {
    /**
     * 向传输记录表中添加一条记录
     * @param t
     * @return
     */
    @Override
    public boolean addRecode(Transfer t) {
        boolean flag = false;
        QueryRunner runner = new QueryRunner(ConnectionPool.getDataSource());
        try {
            int count = runner.update("insert into transfer values(?,?,?,?,?,?,?)",
                    0,t.getUsername(),t.getFilename(),t.getFileLength(),t.getFilepath(),t.getTime(),t.getOperating());
            flag = count>0?true:false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除一条记录
     * @param id
     * @return
     */
    @Override
    public boolean deleteRecode(long id) {
        boolean flag = false;
        QueryRunner runner = new QueryRunner(ConnectionPool.getDataSource());
        try {
            int count = runner.update("delete from transfer where id=?",id);
            flag = count>0?true:false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    //查询所有记录
    @Override
    public List<Transfer> getAllRecords(String username) {
        QueryRunner runner = new QueryRunner(ConnectionPool.getDataSource());
        List<Transfer> list = null;
        try {
            list = runner.query("select * from transfer where username=?", new BeanListHandler<Transfer>(Transfer.class),username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
