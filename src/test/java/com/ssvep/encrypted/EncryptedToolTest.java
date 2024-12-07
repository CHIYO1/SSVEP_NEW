/**
 * 这个文件包含一个对EncryptedTool类进行测试的类，
 * 测试是否可以正常解密数据库中Users、TestRecords、AnalysisReports经过加密的数据。
 *
 * @author 张家荣
 * @version 1.0.0
 */
package com.ssvep.encrypted;

import com.ssvep.dao.TestRecordsDao;
import com.ssvep.dao.TreatmentRecommendationsDao;
import com.ssvep.dao.UserDao;
import com.ssvep.dao.AnalysisReportsDao;
import com.ssvep.model.AnalysisReports;
import com.ssvep.model.TestRecords;
import com.ssvep.model.TreatmentRecommendations;
import com.ssvep.model.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptedToolTest {
    private UserDao userDao;
    private TestRecordsDao testRecordsDao;
    private AnalysisReportsDao reportsDao;
    private TreatmentRecommendationsDao treatmentRecommendationsDao;

    @BeforeEach
    public void setUp() throws Exception {
        userDao = new UserDao();
        testRecordsDao = new TestRecordsDao();
        reportsDao = new AnalysisReportsDao();
        treatmentRecommendationsDao = new TreatmentRecommendationsDao();
    }

    @Test
    public void testUserDecrypt() throws Exception {
        Users user1 = new Users("videoTestuser3", "123345435", "User Three", null, Users.Role.USER);
        userDao.save(user1);

        Users retrievedUser = userDao.getUserById(user1.getUserId());

        System.out.println("用户注册所用密码为:" + user1.getPassword());
        System.out.println("从数据库中解密获得的密码为:" + retrievedUser.getPassword());

        assertEquals(retrievedUser.getPassword(),"123345435","The password isn't be decrypted correctly");
        assertArrayEquals(user1.getAeskey(),retrievedUser.getAeskey(),"The AesKey isn't decrypted correctly");
        assertArrayEquals(user1.getIv(),retrievedUser.getIv(),"The Iv isn't decrypted correctly");
    }

    @Test
    public void testTestRecordDecrypt() throws Exception {
        LocalDate date = LocalDate.now();
        TestRecords record = new TestRecords(27L, "videoTesttype", date, null, "teststring", 3L);
        Map<String, Object> map = new HashMap<>();
        map.put("key1", Integer.valueOf(1));
        record.setTestResults(map);
        testRecordsDao.save(record);

        System.out.println(map);
        TestRecords updatedRecord = testRecordsDao.getRecordById(record.getRecordId());

        System.out.println("原测试记录的测试结果为:" + map);
        System.out.println("从数据库解密获得的测试结果为:" + updatedRecord.getTestResults());

        assertEquals(map, updatedRecord.getTestResults(),"The updatedRecord got from database isn't the same as the right testrecord.");
    }

    @Test
    public void testAnalysisReportDecrypt() throws Exception {
        LocalDateTime time = LocalDateTime.now();

        Map<String, Object> map = new HashMap<>();
        map.put("key2", Integer.valueOf(2));

        AnalysisReports report = new AnalysisReports(28L,map,time);

        reportsDao.save(report);

        AnalysisReports updatedReport = reportsDao.getReportById(report.getReportId());
        assertEquals(map, updatedReport.getReportData(), "ReportData isn't decrypted correctly");
    }

    @Test
    public void testTreatmentRecommendationDecrypt() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("key3", Integer.valueOf(3));
        TreatmentRecommendations recommendations = new TreatmentRecommendations(3L,map);
        treatmentRecommendationsDao.save(recommendations);
        TreatmentRecommendations getTreatmentRecommondation = treatmentRecommendationsDao.getRecommendationById(recommendations.getRecommendationId());
        assertEquals(map,getTreatmentRecommondation.getAdvice(),"Advice isn't decrypted correctly");
    }
}
