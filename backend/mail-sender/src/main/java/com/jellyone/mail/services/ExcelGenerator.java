package com.jellyone.mail.services;

import com.jellyone.mail.dto.TaskDTO;
import com.jellyone.mail.errors.ServerError;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Profile("mail")
public class ExcelGenerator {
    final static int maxWidth = 10000;
    public byte[] generateExcel(List<TaskDTO> tasks, List<TaskDTO> myTasks, List<TaskDTO> theirTasks) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheetGeneral = workbook.createSheet("General");
        sheetBuilder(tasks, sheetGeneral);

        Sheet sheetYouReimbursed = workbook.createSheet("You reimbursed");
        sheetBuilder(myTasks, sheetYouReimbursed);

        Sheet sheetYouOwe = workbook.createSheet("You owe");
        sheetBuilder(theirTasks, sheetYouOwe);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new ServerError("Error during writing to byte stream: " + e.getMessage());
        }

        return outputStream.toByteArray();
    }

    private void sheetBuilder(List<TaskDTO> tasks, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Title");
        headerRow.createCell(1).setCellValue("Price, RUB");
        headerRow.createCell(2).setCellValue("Assigned");
        headerRow.createCell(3).setCellValue("Assigned Email");
        headerRow.createCell(4).setCellValue("Description");
        double sum = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Row row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(tasks.get(i).getTitle());
            row.createCell(1).setCellValue(tasks.get(i).getPrice());
            row.createCell(2).setCellValue(tasks.get(i).getUser().getName());
            row.createCell(3).setCellValue(tasks.get(i).getUser().getEmail());
            row.createCell(4).setCellValue(tasks.get(i).getDescription());
            sum += tasks.get(i).getPrice();
        }
        Row row = sheet.createRow(tasks.size()+2);
        row.createCell(0).setCellValue("Summary");
        row.createCell(1).setCellValue(sum);
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
            if (maxWidth < sheet.getColumnWidth(i)) {
                sheet.setColumnWidth(i, maxWidth);
            }
        }
    }
}
