/**
 * 这个文件包含AnalysisReports表的DAO类，继承抽象类AbstractDao。
 * 
 * @author 石振山
 * @version 2.1.2
 */
package com.ssvep.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.ssvep.encrypted.EncryptedTool;
import com.ssvep.model.AnalysisReports;
import com.ssvep.util.JsonConverter;


public class AnalysisReportsDao extends AbstractDao<AnalysisReports>{

    @Override
    protected String getTableName() {
        return "analysisreports";
    }

    @Override
    protected String getIdName() {
        return "report_id";
    }

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO analysisreports (record_id, report_data, created_at) VALUES (?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE analysisreports SET record_id=?,report_data=?,created_at=? WHERE report_id=?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, AnalysisReports report) throws SQLException {
        Map<String,byte[]> AesKeyandIv = getAesKeyAndIvByTestRecord(report.getTestRecordId());
        byte[] realKey = EncryptedTool.decryptAesKey(AesKeyandIv.get("aes_key"));
        byte[] iv = AesKeyandIv.get("iv");
        if (AesKeyandIv.get("aes_key") == null || realKey == null || iv == null){
            System.err.println("AES Key or realKey or IV are null!");
        }

        statement.setLong(1, report.getTestRecordId());
        statement.setBytes(2, EncryptedTool.encryptData(realKey,iv,JsonConverter.convertToJson(report.getReportData())));
        statement.setDate(3, Date.valueOf(report.getCreatedAt().toLocalDate()));
    }
    @Override
    protected void setUpdateParameters(PreparedStatement statement, AnalysisReports report) throws SQLException {
        Map<String,byte[]> AesKeyandIv = getAesKeyAndIvByTestRecord(report.getTestRecordId());
        byte[] realKey = EncryptedTool.decryptAesKey(AesKeyandIv.get("aes_key"));
        byte[] iv = AesKeyandIv.get("iv");
        if (AesKeyandIv.get("aes_key") == null || realKey == null || iv == null){
            System.err.println("AES Key or realKey or IV are null!");
        }

        statement.setLong(1, report.getTestRecordId());
        statement.setBytes(2, EncryptedTool.encryptData(realKey,iv,JsonConverter.convertToJson(report.getReportData())));
        statement.setDate(3, Date.valueOf(report.getCreatedAt().toLocalDate()));
        statement.setLong(4, report.getReportId());
    }

    @Override
    protected void setEntityId(AnalysisReports report, Long id) {
        report.setReportId(id);
    }

    @Override
    protected AnalysisReports mapRowToEntity(ResultSet resultSet) throws SQLException {
        AnalysisReports report=new AnalysisReports();

        Map<String,byte[]> AesKeyandIv = getAesKeyAndIvByTestRecord(resultSet.getLong("record_id"));
        byte[] realKey = EncryptedTool.decryptAesKey(AesKeyandIv.get("aes_key"));
        byte[] iv = AesKeyandIv.get("iv");
        if (AesKeyandIv.get("aes_key") == null || realKey == null || iv == null){
            System.err.println("AES Key or realKey or IV are null!");
        }

        report.setReportId(resultSet.getLong("report_id"));
        report.setTestRecordId(resultSet.getLong("record_id"));
        report.setReportData(JsonConverter.convertToMap(EncryptedTool.decryptData(realKey,iv,resultSet.getBytes("report_data"))));
        report.setCreatedAt(resultSet.getDate("created_at").toLocalDate().atStartOfDay());

        return report;
    }

    public AnalysisReports getReportById(Long id){
        Map<String,Object> criteria=new HashMap<>();

        criteria.put("report_id", id);
        List<AnalysisReports> results=query(criteria);

        if(results.isEmpty()){
            return null;
        }else{
            return results.get(0);
        }
    }

    public List<AnalysisReports> getReportByTestRecord(Long testRecordId){
        Map<String,Object> criteria=new HashMap<>();

        criteria.put("record_id", testRecordId);
        List<AnalysisReports> results=query(criteria);

        if(results.isEmpty()){
            return Collections.emptyList();
        }else{
            return results;
        }
    }

    public Map<String,byte[]> getAesKeyAndIvByTestRecord(Long record_id){
        if(record_id == null){
            System.err.println("record_id is null!");
            return null;
        }
        String sql = "SELECT user_id from testrecords where record_id = ?";
        Long userid;
        TestRecordsDao testRecordsDao = new TestRecordsDao();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,record_id);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    userid = resultSet.getLong("user_id");
                    return testRecordsDao.getAesKeyAndIvByUser(userid);
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
