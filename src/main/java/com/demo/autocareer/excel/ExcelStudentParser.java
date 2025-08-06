package com.demo.autocareer.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.InternshipAssignmentExcelDTO;
import com.demo.autocareer.model.enums.Gender;
import com.demo.autocareer.service.impl.StudentServiceImpl;

@Component
public class ExcelStudentParser {
    private static final Logger log = LoggerFactory.getLogger(ExcelStudentParser.class);
    public List<InternshipAssignmentExcelDTO> parse(MultipartFile file){
        List<InternshipAssignmentExcelDTO> result = new ArrayList<>();
        try(InputStream is = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is)){
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if(row.getRowNum() == 0) continue;

                InternshipAssignmentExcelDTO dto = new InternshipAssignmentExcelDTO();
                dto.setName(getSafeStringCell(row, 1, formatter));
                dto.setStudentCode(getSafeStringCell(row, 2, formatter));
                dto.setEmail(getSafeStringCell(row, 3, formatter));
                dto.setPhoneNumber(getSafeStringCell(row, 4, formatter));
                dto.setDob(getSafeDate(row.getCell(5)));
                String genderStr = getSafeStringCell(row, 6, formatter);
                try {
                    dto.setGender(Gender.valueOf(genderStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn("⚠️ Giá trị gender không hợp lệ tại dòng {}: '{}'", i + 1, genderStr);
                    dto.setGender(null);
                }
                dto.setFacultyName(getSafeStringCell(row, 7, formatter));
                String graduatedYearStr = getSafeStringCell(row, 8, formatter);
                try {
                    dto.setGraduatedYear(Integer.parseInt(graduatedYearStr));
                } catch (NumberFormatException e) {
                    log.warn("⚠️ Không thể parse graduatedYear tại dòng {}: '{}'", i + 1, graduatedYearStr);
                    dto.setGraduatedYear(null);
                }
                dto.setSkill(getSafeStringCell(row, 9, formatter));
                result.add(dto);
            }
        }catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

        return result;
    }

    private String getSafeStringCell(Row row, int index, DataFormatter formatter) {
        if (row == null || index < 0) {
            return "";
        }

        try {
            Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            return cell == null ? "" : formatter.formatCellValue(cell).trim();
        } catch (Exception e) {
            return "";
        }
    }

    private Date getSafeDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                boolean isDate = DateUtil.isCellDateFormatted(cell);
                if (isDate) {
                    return cell.getDateCellValue();
                } else {
                    Date fallback = DateUtil.getJavaDate(cell.getNumericCellValue());
                    return fallback;
                }
            }

            if (cell.getCellType() == CellType.STRING) {
                log.info("🔤 Cell is STRING: '{}'", cell.getStringCellValue());
            }
        } catch (Exception e) {
            log.error("❌ Exception trong getSafeDate: {}", e.getMessage(), e);
        }

        return null;
    }

    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
