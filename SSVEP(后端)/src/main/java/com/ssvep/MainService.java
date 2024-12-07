/*
 * 这个文件是一个 Java 应用程序的入口点，用于启动一个嵌入式的 Tomcat 服务器，并注册多个控制器（Controller）以处理不同的 HTTP 请求。
 *
 * @author 石振山
 * @version 3.4.3
 */

//package com.ssvep;
//
//import org.apache.catalina.Context;
//import org.apache.catalina.startup.Tomcat;
//
//import com.ssvep.controller.TestRecordController;
//import com.ssvep.controller.UserController;
//import com.ssvep.controller.AnalysisReportController;
//import com.ssvep.controller.LogsController;
//import com.ssvep.controller.RecommendationController;
//import com.ssvep.controller.StimulusVideoController;
//
//import java.io.File;
//
//public class MainService {
//    public static void main(String[] args) throws Exception {
//        // 创建 Tomcat 实例
//        Tomcat tomcat = new Tomcat();
//        tomcat.setPort(8080);
//
//        // 创建应用上下文
//        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
//
//        // 添加各个 Controller 的实例
//        UserController userController = new UserController();
//        TestRecordController testRecordController = new TestRecordController();
//        StimulusVideoController stimulusVideoController = new StimulusVideoController();
//        RecommendationController recommendationController = new RecommendationController();
//        LogsController logsController = new LogsController();
//        AnalysisReportController analysisReportController = new AnalysisReportController();
//
//        // 注册每个 Controller
//        Tomcat.addServlet(ctx, "userController", userController);
//        ctx.addServletMappingDecoded("/users", "userController");
//
//        Tomcat.addServlet(ctx, "testRecordController", testRecordController);
//        ctx.addServletMappingDecoded("/testRecords", "testRecordController");
//
//        Tomcat.addServlet(ctx, "stimulusVideoController", stimulusVideoController);
//        ctx.addServletMappingDecoded("/stimulusVideos", "stimulusVideoController");
//
//        Tomcat.addServlet(ctx, "recommendationController", recommendationController);
//        ctx.addServletMappingDecoded("/recommendations", "recommendationController");
//
//        Tomcat.addServlet(ctx, "logsController", logsController);
//        ctx.addServletMappingDecoded("/logs", "logsController");
//
//        Tomcat.addServlet(ctx, "analysisReportController", analysisReportController);
//        ctx.addServletMappingDecoded("/analysisReports", "analysisReportController");
//
//        // 启动服务器
//        try {
//            System.out.println("启动 Tomcat...");
//            tomcat.start();
//            System.out.println("Tomcat 已启动！");
//        } catch (Exception e) {
//            System.out.println("Tomcat 启动失败：" + e.getMessage());
//            e.printStackTrace();
//        }
//        tomcat.getServer().await();
//    }
//}

package com.ssvep;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;

import com.ssvep.controller.TestRecordController;
import com.ssvep.controller.UserController;
import com.ssvep.controller.AnalysisReportController;
import com.ssvep.controller.LogsController;
import com.ssvep.controller.RecommendationController;
import com.ssvep.controller.StimulusVideoController;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import com.ssvep.filters.CORSFilter;

import java.io.File;


public class MainService {
    public static void main(String[] args) throws Exception {
        // 创建 Tomcat 实例
        Tomcat tomcat = new Tomcat();

        // 设置 HTTP 端口为 8080
        tomcat.setPort(8080); // HTTP 默认端口

        // 添加 HTTP 连接器
        Connector httpConnector = new Connector();
        httpConnector.setPort(8080);
        tomcat.getService().addConnector(httpConnector);

        // 添加 HTTPS 连接器（设置端口为 8443）
        tomcat.getService().addConnector(createHttpsConnector());

        // 创建应用上下文
        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());

        // 添加各个 Controller 的实例
        UserController userController = new UserController();
        TestRecordController testRecordController = new TestRecordController();
        StimulusVideoController stimulusVideoController = new StimulusVideoController();
        RecommendationController recommendationController = new RecommendationController();
        LogsController logsController = new LogsController();
        AnalysisReportController analysisReportController = new AnalysisReportController();

        // 注册每个 Controller
        Tomcat.addServlet(ctx, "userController", userController);
        ctx.addServletMappingDecoded("/users", "userController");

        Tomcat.addServlet(ctx, "testRecordController", testRecordController);
        ctx.addServletMappingDecoded("/testRecords", "testRecordController");

        Tomcat.addServlet(ctx, "stimulusVideoController", stimulusVideoController);
        ctx.addServletMappingDecoded("/stimulusVideos", "stimulusVideoController");

        Tomcat.addServlet(ctx, "recommendationController", recommendationController);
        ctx.addServletMappingDecoded("/recommendations", "recommendationController");

        Tomcat.addServlet(ctx, "logsController", logsController);
        ctx.addServletMappingDecoded("/logs", "logsController");

        Tomcat.addServlet(ctx, "analysisReportController", analysisReportController);
        ctx.addServletMappingDecoded("/analysisReports", "analysisReportController");

        // 创建 CORS 过滤器并注册
        FilterDef corsFilterDef = new FilterDef();
        corsFilterDef.setFilterName("CORSFilter");
        corsFilterDef.setFilterClass(CORSFilter.class.getName());

        // 将 Filter 定义添加到 Context 中
        ctx.addFilterDef(corsFilterDef);

        // 映射该过滤器到所有路径
        FilterMap filterMap = new FilterMap();
        filterMap.addURLPattern("/*");
        filterMap.setFilterName("CORSFilter");

        // 将 Filter 映射添加到 Context 中
        ctx.addFilterMap(filterMap);

        // 启动服务器
        try {
            System.out.println("启动 Tomcat...");
            tomcat.start();
            System.out.println("Tomcat 已启动！");
        } catch (Exception e) {
            System.out.println("Tomcat 启动失败：" + e.getMessage());
            e.printStackTrace();
        }
        tomcat.getServer().await();
    }



    // 创建 HTTPS 连接器
    private static Connector createHttpsConnector() {
        Connector connector = new Connector();
        connector.setPort(8443); // HTTPS 端口
        connector.setSecure(true);
        connector.setScheme("https");

        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        protocol.setSSLEnabled(true);

        // 配置 Keystore
        protocol.setKeystoreFile("D:\\大学相关\\专业\\java\\JAVA项目\\SSVEP\\SSVEP(后端)\\src\\main\\resources\\ssvep.jks"); // 替换为你的 keystore 路径
        protocol.setKeystorePass("111111"); // 替换为你的 keystore 密码
        protocol.setKeyAlias("eyes"); // 替换为你的证书别名

        protocol.setSslEnabledProtocols("TLSv1.2,TLSv1.3");
        protocol.setCiphers("TLS_AES_128_GCM_SHA256:TLS_AES_256_GCM_SHA384:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384");

        return connector;
    }
}