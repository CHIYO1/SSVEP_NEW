package com.ssvep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssvep.dto.AnalysisReportDto;
import com.ssvep.dto.TestRecordDto;
import com.ssvep.dto.TreatmentRecommendationDto;
import com.ssvep.model.TreatmentRecommendations;
import com.ssvep.service.AnalysisReportService;
import com.ssvep.service.RecommendationService;
import com.ssvep.service.TestRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class ExportControllerTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ExportController controller;
    private TestRecordService testRecordService;
    private AnalysisReportService analysisReportService;
    private RecommendationService recommendationService;
    private ByteArrayOutputStream outputStream;
    private ObjectMapper objectMapper;
    private PrintWriter writer;

    @BeforeEach
    void setUp(){
        try {
            testRecordService = new TestRecordService();
            analysisReportService = new AnalysisReportService();
            recommendationService = new RecommendationService();
            controller = new ExportController();
            controller.init();
            outputStream = new ByteArrayOutputStream();
//        writer = new PrintWriter(outputStream);
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            request = Mockito.mock(HttpServletRequest.class);
            response = Mockito.mock(HttpServletResponse.class);
//        when(response.getWriter()).thenReturn(writer);
            when(response.getOutputStream()).thenAnswer(invocation -> {
                return new ServletOutputStream() {
                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                    }

                    @Override
                    public void write(int b) throws IOException {
                        outputStream.write(b);
                    }
                };
            });

        }catch(IOException e){
            e.printStackTrace();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void csvExportUserTest() {
        Long userId = 1L;
        when(request.getParameter("user_id")).thenReturn("4");
        when(request.getParameter("model")).thenReturn("user");
        when(request.getParameter("format")).thenReturn("csv");

        controller.doGet(request, response);
        verify(response).setContentType("application/zip");
        verify(response).setHeader("Content-Disposition", "attachment; filename=user.zip");

        byte[] result = outputStream.toByteArray();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(result))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                System.out.println("Found file in ZIP: " + fileName);

                if (fileName.endsWith(".csv")) {
                    ByteArrayOutputStream csvOutput = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        csvOutput.write(buffer, 0, length);
                    }
                    String csvContent = csvOutput.toString(StandardCharsets.UTF_8);
                    System.out.println("Contents of " + fileName + ":\n" + csvContent);

                } else {
                    System.out.println(fileName + " is not a CSV file.");
                }

                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to read the zip content: " + e.getMessage());
        }
    }

    @Test
    public void csvExportRecordTest() throws IOException {
        Long userId = 1L;
        when(request.getParameter("user_id")).thenReturn("4");
        when(request.getParameter("model")).thenReturn("record");
        when(request.getParameter("format")).thenReturn("csv");

        controller.doGet(request, response);
        verify(response).setContentType("text/csv");
        verify(response).setHeader("Content-Disposition", "attachment; filename=record.csv");

        String result = outputStream.toString();
        System.out.println(result);
    }

    @Test
    public void csvExportReportTest() throws IOException {
        Long userId = 1L;
        when(request.getParameter("user_id")).thenReturn("4");
        when(request.getParameter("model")).thenReturn("report");
        when(request.getParameter("format")).thenReturn("csv");

        controller.doGet(request, response);
        verify(response).setContentType("text/csv");
        verify(response).setHeader("Content-Disposition", "attachment; filename=report.csv");

        String result = outputStream.toString();
        System.out.println(result);
    }

    @Test
    public void csvExportRecommendationTest() throws IOException {
        Long userId = 1L;
        when(request.getParameter("user_id")).thenReturn("4");
        when(request.getParameter("model")).thenReturn("recommendation");
        when(request.getParameter("format")).thenReturn("csv");

        controller.doGet(request, response);
        verify(response).setContentType("text/csv");
        verify(response).setHeader("Content-Disposition", "attachment; filename=recommendation.csv");

        String result = outputStream.toString();
        System.out.println(result);
    }

    @Test
    public void jsonExportUserTest() throws IOException {
        Long userId = 1L;
        when(request.getParameter("user_id")).thenReturn("4");
        when(request.getParameter("model")).thenReturn("user");
        when(request.getParameter("format")).thenReturn("json");

        controller.doGet(request, response);
        verify(response).setContentType("application/zip");
        verify(response).setHeader("Content-Disposition", "attachment; filename=user.zip");

        byte[] result = outputStream.toByteArray();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(result))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                System.out.println("Found file in ZIP: " + fileName);

                if (fileName.endsWith(".json")) {
                    ByteArrayOutputStream csvOutput = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        csvOutput.write(buffer, 0, length);
                    }
                    String csvContent = csvOutput.toString(StandardCharsets.UTF_8);
                    System.out.println("Contents of " + fileName + ":\n" + csvContent);

                } else {
                    System.out.println(fileName + " is not a Json file.");
                }

                zis.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to read the zip content: " + e.getMessage());
        }
    }

    @Test
    public void jsonExportRecordTest() throws IOException {
        Long userId = 1L;
        when(request.getParameter("user_id")).thenReturn("4");
        when(request.getParameter("model")).thenReturn("record");
        when(request.getParameter("format")).thenReturn("json");

        controller.doGet(request, response);
        verify(response).setContentType("application/json");
        verify(response).setHeader("Content-Disposition", "attachment; filename=record.json");

        String result = outputStream.toString();
        System.out.println(result);
    }

    @Test
    public void jsonExportReportTest() throws IOException {
        Long userId = 1L;
        when(request.getParameter("user_id")).thenReturn("4");
        when(request.getParameter("model")).thenReturn("report");
        when(request.getParameter("format")).thenReturn("json");

        controller.doGet(request, response);
        verify(response).setContentType("application/json");
        verify(response).setHeader("Content-Disposition", "attachment; filename=report.json");

        String result = outputStream.toString();
        System.out.println(result);
    }
    @Test
    public void jsonExportRecommendationTest() throws IOException {
        Long userId = 1L;
        when(request.getParameter("user_id")).thenReturn("4");
        when(request.getParameter("model")).thenReturn("recommendation");
        when(request.getParameter("format")).thenReturn("json");

        controller.doGet(request, response);
        verify(response).setContentType("application/json");
        verify(response).setHeader("Content-Disposition", "attachment; filename=recommendation.json");

        String result = outputStream.toString();
        System.out.println(result);
    }

}
