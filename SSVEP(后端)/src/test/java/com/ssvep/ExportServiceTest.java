package com.ssvep;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.ssvep.dto.TestRecordDto;
import com.ssvep.dto.UserDto;
import com.ssvep.encrypted.EncryptedTool;
import com.ssvep.service.ExportService;
import com.ssvep.service.TestRecordService;
import com.ssvep.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExportServiceTest
{
    private TestRecordService testRecordService;
    private UserService userService;

    @BeforeEach
    public void setup(){
        testRecordService = new TestRecordService();
        userService = new UserService();
    }

    @Test
    public void csvTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));
        objectMapper.registerModule(module);
//        List<UserDto> userDtos = userService.getAllUsers();
//        List<Map<String,Object>> userList = new ArrayList<>();
//        for (UserDto userDto : userDtos){
//            Map<String,Object> user = objectMapper.convertValue(userDto, Map.class);
//            userList.add(user);
//        }
//
//        byte[] result = ExportService.DataToCsv(userList);
//        System.out.println(result);

        List<TestRecordDto> recordDtos = testRecordService.getAllRecords();
        byte[] result = ExportService.DataToCsv(recordDtos,objectMapper);
        System.out.println(new String(result));
    }

    @Test
    public void jsonTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));
        objectMapper.registerModule(module);
//        List<UserDto> userDtos = userService.getAllUsers();
//        List<Map<String,Object>> userList = new ArrayList<>();
//        for (UserDto userDto : userDtos){
//            Map<String,Object> user = objectMapper.convertValue(userDto, Map.class);
//            userList.add(user);
//        }
//
//        byte[] result = ExportService.DataToCsv(userList);
//        System.out.println(result);

        List<TestRecordDto> recordDtos = testRecordService.getAllRecords();

        byte[] result = ExportService.DataToJson(recordDtos,objectMapper);
        System.out.println(new String(result));
        System.out.println(result);
    }


}
