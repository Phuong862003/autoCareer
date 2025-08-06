package com.demo.autocareer.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.service.storage.FileStorageService;

@Component
public class WriteStudentExcel {

    public String writeStudentExcel(List<StudentDTOResponse> students) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "students_" + timestamp + ".xlsx";
        String filePath = "uploads/listStudent/" + fileName;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("STT");
            header.createCell(1).setCellValue("Tên");
            header.createCell(2).setCellValue("Mã SV");
            header.createCell(3).setCellValue("Email");
            header.createCell(4).setCellValue("Số điện thoại");
            header.createCell(5).setCellValue("Ngày sinh");
            header.createCell(6).setCellValue("Giới tính");
            header.createCell(7).setCellValue("Ngành");
            header.createCell(8).setCellValue("Năm tốt nghiệp");
            header.createCell(9).setCellValue("Kỹ năng");

            for (int i = 0; i < students.size(); i++) {
                StudentDTOResponse s = students.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(s.getName());
                row.createCell(2).setCellValue(s.getStudentCode());
                row.createCell(3).setCellValue(s.getEmail());
                row.createCell(4).setCellValue(s.getPhoneNumber());
                row.createCell(5).setCellValue(s.getDob() != null ? s.getDob().toString() : "");
                row.createCell(6).setCellValue(s.getGender().toString());
                row.createCell(7).setCellValue(s.getOrganizationFaculty() != null ? s.getOrganizationFaculty().getFaculty().getFaculty_name() : "");
                row.createCell(8).setCellValue(s.getGraduatedYear());
                String skills = s.getStudentSkills() != null
                    ? s.getStudentSkills().stream()
                            .map(skill -> skill.getSkill().getSkillName())
                            .collect(Collectors.joining(", "))
                    : "";
                row.createCell(9).setCellValue(skills);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            return filePath;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi ghi file Excel sinh viên", e);
        }
    }
}
