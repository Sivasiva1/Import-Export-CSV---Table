package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Import from CSV
    public void importFromCSV(MultipartFile file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            List<User> users = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                User user = new User();
                user.setUserName(record.get("UserName"));
                user.setEmailId(record.get("EmailId"));
                users.add(user);
            }
            userRepository.saveAll(users);
        }
    }

    // Import from XLSX
    public void importFromXLSX(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<User> users = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header row

                User user = new User();
                user.setUserName(row.getCell(0).getStringCellValue());
                user.setEmailId(row.getCell(1).getStringCellValue());
                users.add(user);
            }
            userRepository.saveAll(users);
        }
    }

    // Export to CSV
    public void exportToCSV(OutputStream os) throws Exception {
        List<User> users = userRepository.findAll();

        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(os), CSVFormat.DEFAULT.withHeader("UserName", "EmailId"))) {
            for (User user : users) {
                csvPrinter.printRecord(user.getUserName(), user.getEmailId());
            }
        }
    }

    // Export to XLSX
    public void exportToXLSX(OutputStream os) throws Exception {
        List<User> users = userRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("UserName");
            headerRow.createCell(1).setCellValue("EmailId");

            // Data
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getUserName());
                row.createCell(1).setCellValue(user.getEmailId());
            }

            workbook.write(os);
        }
    }
}
