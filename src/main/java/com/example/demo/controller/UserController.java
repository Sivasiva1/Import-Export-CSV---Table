package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/import/csv")
    public String importCSV(@RequestParam("file") MultipartFile file) {
        try {
            userService.importFromCSV(file);
            return "CSV data imported successfully!";
        } catch (Exception e) {
            return "Failed to import CSV data: " + e.getMessage();
        }
    }

    @PostMapping("/import/xlsx")
    public String importXLSX(@RequestParam("file") MultipartFile file) {
        try {
            userService.importFromXLSX(file);
            return "XLSX data imported successfully!";
        } catch (Exception e) {
            return "Failed to import XLSX data: " + e.getMessage();
        }
    }

    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.csv\"");
        try {
            userService.exportToCSV(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/export/xlsx")
    public void exportXLSX(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"users.xlsx\"");
        try {
            userService.exportToXLSX(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
