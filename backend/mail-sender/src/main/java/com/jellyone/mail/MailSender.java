package com.jellyone.mail;

import com.jellyone.mail.dto.TaskDTO;
import com.jellyone.mail.services.ExcelGenerator;
import com.jellyone.mail.services.MailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("mail")
public class MailSender {
    private final MailService mailSender;
    private final ExcelGenerator excelGenerator;
    private final String excelFileName = "report.xls";

    public Context createContext(
            long eventNumber,
            Date curDate,
            int totalAmount,
            int amountYouSpent,
            int amountOwedToYou,
            int amountYouOwe,
            boolean isEventClosed,
            Date eventEndDate,
            List<TaskDTO> tasks
    ) {
        Context context = new Context();
        context.setVariable("eventNumber", eventNumber);
        context.setVariable("curDate", curDate);
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("amountSpent", amountYouSpent);
        context.setVariable("amountOwedToYou", amountOwedToYou);
        context.setVariable("amountYouOwe", amountYouOwe);
        context.setVariable("isEventClosed", isEventClosed);
        context.setVariable("eventEndDate", eventEndDate);
        context.setVariable("tasks", tasks);
        return context;
    }

    // этот метод потом подправим, мне не нрав, что здесь так много аргументов (как я понимаю их число
    // должно увеличиться)
    public File createExcel(List<TaskDTO> allTasks, List<TaskDTO> myTasks, List<TaskDTO> otherTasks) {
        byte[] excelBytes = excelGenerator.generateExcel(allTasks, myTasks, otherTasks);
        if (excelBytes.length != 0) {
            File file = new File(excelFileName);
            try {
                FileUtils.writeByteArrayToFile(file, excelBytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return file;
        }
        return null;
    }


    // Чтобы отправить письмо, нужно создать контекст.
    // Создаем контекст с помощью апишки, метод для создания контекста выше
    // письмо с файлом
    public void sendAttachMailReport(String email, String name, File file, Context context) {
        mailSender.sendAttachMail(email, name, file, context);
    }

    // письмо без файла (но у всех один вид)
    public void sendMailReport(String email, String eventTitle, Context context) {
        mailSender.sendMail(email, eventTitle, context);
    }
}
