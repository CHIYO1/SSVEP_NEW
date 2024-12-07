package com.ssvep.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.ObjectStreamException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExportService {
    public static <T> byte[] DataToCsv(List<T> dtos,ObjectMapper objectMapper){
        StringBuilder sb = new StringBuilder();
        List<Map<String,Object>> entityList=new ArrayList<>();
        for(T dto:dtos){
            Map<String,Object> map = objectMapper.convertValue(dto,Map.class);
            entityList.add(map);
        }
        if (!dtos.isEmpty()) {
            Map<String, Object> firstRow = entityList.get(0);
            if (firstRow != null) {
                sb.append(String.join(",", firstRow.keySet())).append('\n');

                for (Map<String, Object> map : entityList) {
                    if (map == null) {
                        break;
                    }
                    sb.append(String.join(",", map.values().stream()
                            .map(ExportService::safeToString)
                            .toArray(String[]::new))).append('\n');
                }
            }else{
                System.out.println("Data for csv is null");
                sb.append("null");
                return sb.toString().getBytes(StandardCharsets.UTF_8);
            }
        }else{
            System.err.println("Data for generating csv is empty");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

//    public static byte[] DataToJson(List<Map<String,Object>> list){
//        StringBuilder sb = new StringBuilder("[\n");
//        if (!list.isEmpty()) {
//            for (int index = 0; index < list.size(); index++) {
//                Map<String, Object> row = list.get(index);
//                sb.append("{\n");
//                boolean isfirstrow = true;
//                for (Map.Entry<String, Object> entry : row.entrySet()) {
//                    if(!isfirstrow){
//                        sb.append(",\n");
//                    }
//                    sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
//                    isfirstrow = false;
//                }
//                sb.append("\n}");
//                if (index < list.size() - 1) {
//                    sb.append(",");
//                }
//            }
//            sb.append("\n]");
//        }else{
//            System.err.println("Data for generating json is empty");
//        }
//        return sb.toString().getBytes(StandardCharsets.UTF_8);
//    }

    public static <T> byte[] DataToJson(List<T> dtos,ObjectMapper objectMapper){
        List<Map<String,Object>> entityList=new ArrayList<>();
        for(T dto:dtos){
            Map<String,Object> map = objectMapper.convertValue(dto,Map.class);
            if (map != null) {
                entityList.add(map);
            }else{
                break;
            }
        }
        try{
            String prettyjsonstring = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entityList);
            return prettyjsonstring.getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to convert data to JSON", e);
        }
    }

    public static String safeToString(Object obj){
        return obj==null?"null":obj.toString();
    }
//    public static String safeToString(Object value) {
//        if (value == null) {
//            return "";
//        }
//        if (value instanceof LocalDate) {
//            return ((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE);
//        } else if (value instanceof LocalDateTime) {
//            return ((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        } else if (value instanceof Date) {
//            return ((Date) value).toString();
//        } else {
//            return value.toString();
//        }
//    }
}
