package com.ssvep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.ssvep.dao.TestRecordsDao;
import com.ssvep.dto.AnalysisReportDto;
import com.ssvep.dto.TestRecordDto;
import com.ssvep.dto.TreatmentRecommendationDto;
import com.ssvep.dto.UserDto;
import com.ssvep.model.TestRecords;
import com.ssvep.service.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportController extends HttpServlet {
    private final String Boundary = "----boundary" + UUID.randomUUID().toString();
    private UserService userService;
    private TestRecordService testRecordsService;
    private RecommendationService recommendationService;
    private AnalysisReportService analysisReportService;

    @Override
    public void init() throws ServletException{
        userService = new UserService();
        testRecordsService = new TestRecordService();
        recommendationService = new RecommendationService();
        analysisReportService = new AnalysisReportService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        try {
            String model = request.getParameter("model");
            String user_id = request.getParameter("user_id");
            String format = request.getParameter("format");

            if (user_id == null || !user_id.matches("\\d+")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user_id parameter.");
                return;
            }

            Long userid = Long.parseLong(user_id);
            ObjectMapper objectMapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));
            module.addSerializer(LocalDateTime.class,new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            objectMapper.registerModule(module);

//            if(isAdmin()){
//                if ("csv".equalsIgnoreCase(format)) {
//                    List<UserDto> userDtos = userService.getAllUsers();
//                    for (UserDto userDto : userDtos) {
//                        Long everyUserId = userDto.getUserId();
//                        List<TestRecordDto> recordDtos = testRecordsService.getRecordsByUser(userid);
//                        byte[] recordCsv = ExportService.DataToCsv(recordDtos, objectMapper);
//
//                        List<AnalysisReportDto> analysisReportDtos = new ArrayList<>();
//                        for (TestRecordDto recordDto : recordDtos) {
//                            analysisReportDtos.addAll(analysisReportService.getReportByTestRecord(recordDto.getRecordId()));
//                        }
//                        byte[] reportCsv = ExportService.DataToCsv(analysisReportDtos, objectMapper);
//
//                        List<TreatmentRecommendationDto> recommendationDtos = recommendationService.getrecommendationsByUser(userid);
//                        byte[] recommendationCsv = ExportService.DataToCsv(recommendationDtos, objectMapper);
//
//                        sendZipResponse(response, recordCsv, reportCsv, recommendationCsv, "csv");
//                    }
//                }else if ("json".equalsIgnoreCase(format)) {
//                    List<UserDto> userDtos = userService.getAllUsers();
//                    for (UserDto userDto : userDtos) {
//                        Long everyUserId = userDto.getUserId();
//                        List<TestRecordDto> recordDtos = testRecordsService.getRecordsByUser(everyUserId);
//                        byte[] recordJson = ExportService.DataToJson(recordDtos, objectMapper);
//
//                        List<AnalysisReportDto> analysisReportDtos = new ArrayList<>();
//                        for (TestRecordDto recordDto : recordDtos) {
//                            analysisReportDtos.addAll(analysisReportService.getReportByTestRecord(recordDto.getRecordId()));
//                        }
//                        byte[] reportJson = ExportService.DataToJson(analysisReportDtos, objectMapper);
//
//                        List<TreatmentRecommendationDto> recommendationDtos = recommendationService.getrecommendationsByUser(userid);
//                        byte[] recommendationJson = ExportService.DataToJson(recommendationDtos, objectMapper);
//
//                        sendZipResponse(response, recordJson, reportJson, recommendationJson, "json");
//                    }
//                }
//            } else {
                if ("csv".equalsIgnoreCase(format)) {
                    if ("user".equalsIgnoreCase(model)) {
                        List<TestRecordDto> recordDtos = testRecordsService.getRecordsByUser(userid);
                        byte[] recordCsv = ExportService.DataToCsv(recordDtos, objectMapper);

                        List<AnalysisReportDto> analysisReportDtos = new ArrayList<>();
                        for (TestRecordDto recordDto : recordDtos) {
                            analysisReportDtos.addAll(analysisReportService.getReportByTestRecord(recordDto.getRecordId()));
                        }
                        byte[] reportCsv = ExportService.DataToCsv(analysisReportDtos, objectMapper);

                        List<TreatmentRecommendationDto> recommendationDtos = recommendationService.getrecommendationsByUser(userid);
                        byte[] recommendationCsv = ExportService.DataToCsv(recommendationDtos, objectMapper);

                        sendZipResponse(response, recordCsv, reportCsv, recommendationCsv, "csv");
                    } else if ("record".equalsIgnoreCase(model)) {
                        List<TestRecordDto> recordDtos = testRecordsService.getRecordsByUser(userid);
                        byte[] recordCsv = ExportService.DataToCsv(recordDtos, objectMapper);
                                sendCsvResponse(response, "record.csv", recordCsv);
                    } else if ("report".equalsIgnoreCase(model)) {
                        List<TestRecordDto> recordDtos = testRecordsService.getRecordsByUser(userid);

                        List<AnalysisReportDto> analysisReportDtos = new ArrayList<>();
                        for (TestRecordDto recordDto : recordDtos) {
                            analysisReportDtos.addAll(analysisReportService.getReportByTestRecord(recordDto.getRecordId()));
                        }

                        byte[] reportCsv = ExportService.DataToCsv(analysisReportDtos, objectMapper);

                        sendCsvResponse(response, "report.csv", reportCsv);
                    } else if ("recommendation".equalsIgnoreCase(model)) {
                        List<TreatmentRecommendationDto> recommendationDtos = recommendationService.getrecommendationsByUser(userid);
                        byte[] recommendationCsv = ExportService.DataToCsv(recommendationDtos, objectMapper);
                        sendCsvResponse(response, "recommendation.csv", recommendationCsv);
                    }
                } else if ("json".equalsIgnoreCase(format)) {
                    if ("user".equalsIgnoreCase(model)) {
                        List<TestRecordDto> recordDtos = testRecordsService.getRecordsByUser(userid);
                        byte[] recordJson = ExportService.DataToJson(recordDtos, objectMapper);

                        List<AnalysisReportDto> analysisReportDtos = new ArrayList<>();
                        for (TestRecordDto recordDto : recordDtos) {
                            analysisReportDtos.addAll(analysisReportService.getReportByTestRecord(recordDto.getRecordId()));
                        }
                        byte[] reportJson = ExportService.DataToJson(analysisReportDtos, objectMapper);

                        List<TreatmentRecommendationDto> recommendationDtos = recommendationService.getrecommendationsByUser(userid);
                        byte[] recommendationJson = ExportService.DataToJson(recommendationDtos, objectMapper);

                        sendZipResponse(response, recordJson, reportJson, recommendationJson, "json");
                    } else if ("record".equalsIgnoreCase(model)) {
                        List<TestRecordDto> recordDtos = testRecordsService.getRecordsByUser(userid);
                        byte[] recordJson = ExportService.DataToJson(recordDtos, objectMapper);
                        sendJsonResponse(response, "record.json", recordJson);
                    } else if ("report".equalsIgnoreCase(model)) {
                        List<TestRecordDto> recordDtos = testRecordsService.getRecordsByUser(userid);
                        List<AnalysisReportDto> analysisReportDtos = new ArrayList<>();
                        for (TestRecordDto recordDto : recordDtos) {
                            analysisReportDtos.addAll(analysisReportService.getReportByTestRecord(recordDto.getRecordId()));
                        }

                        byte[] reportJson = ExportService.DataToJson(analysisReportDtos, objectMapper);
                        sendJsonResponse(response, "report.json", reportJson);
                    } else if ("recommendation".equalsIgnoreCase(model)) {
                        List<TreatmentRecommendationDto> recommendationDtos = recommendationService.getrecommendationsByUser(userid);
                        byte[] recommendationJson = ExportService.DataToJson(recommendationDtos, objectMapper);
                        sendJsonResponse(response, "recommendation.json", recommendationJson);
                    }
                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendCsvResponse(HttpServletResponse response,String fileName,byte[] csvData) {
        try (OutputStream out = response.getOutputStream()) {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out.write(csvData);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendJsonResponse(HttpServletResponse response,String fileName,byte[] jsonData) {
        try (OutputStream out = response.getOutputStream()) {
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out.write(jsonData);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendZipResponse(HttpServletResponse response, byte[] recordData, byte[] reportData, byte[] recommendationData,String format) throws IOException {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=user.zip");

        try (OutputStream out = response.getOutputStream();
             ZipOutputStream zos = new ZipOutputStream(out)) {

            addFileToZip(zos, "records." + format, recordData);
            addFileToZip(zos, "reports." + format, reportData);
            addFileToZip(zos, "recommendations." + format, recommendationData);

            zos.finish();
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void addFileToZip(ZipOutputStream zos, String fileName, byte[] data) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }
//    private void sendMultiPartResponse(HttpServletResponse response,byte[] recordData,byte[] reportData,byte[] recommendationData,String type) {
//        response.setContentType("multipart/mixed; boundary="+Boundary);
//        response.setHeader("Content-Disposition", "attachment; filename=user.zip");
//
//        try (OutputStream out = response.getOutputStream()) {
//            writeMultipartPart(out, "records." + type, recordData,type);
//            writeMultipartPart(out, "reports." + type, reportData,type);
//            writeMultipartPart(out, "recommendations." + type, recommendationData,type);
//
//            PrintWriter writer = new PrintWriter(out, true);
//            writer.append("--"+Boundary+"--\r\n");
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    private void writeMultipartPart(OutputStream out,String fileName,byte[] data,String type) throws IOException {
//        PrintWriter writer = new PrintWriter(out, true);
//        writer.append("--" + Boundary + "\r\n");
//        if("csv".equalsIgnoreCase(type))
//            writer.append("Content-Type: text/csv\r\n");
//        else if ("json".equalsIgnoreCase(type))
//            writer.append("Content-Type: application/json\r\n");
//        writer.append("Content-Disposition: attachment; filename=\"" + fileName + "\"\r\n");
//        writer.append("\r\n");
//        writer.flush();
//
//        out.write(data);
//        out.flush();
//
//        writer.append("\r\n");
//        writer.flush();
//    }

}
