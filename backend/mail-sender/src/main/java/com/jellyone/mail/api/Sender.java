package com.jellyone.mail.api;

import com.jellyone.mail.dto.TaskDTO;
import org.thymeleaf.context.Context;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public interface Sender {
    Context createContext(
            String eventUrl,
            String eventTitle,
            LocalDateTime curDate,
            double totalAmount,
            double amountUserSpent,
            double amountOwedToUser,
            boolean isEventClosed,
            LocalDateTime eventEndDate,
            List<TaskDTO> tasks
    );

    File createExcel(List<TaskDTO> allTasks, List<TaskDTO> myTasks, List<TaskDTO> otherTasks);


    // Чтобы отправить письмо, нужно создать контекст.
    // Создаем контекст с помощью апишки, метод для создания контекста выше
    // письмо с файлом
    void sendAttachMailReport(String email, String name, File file, Context context);

    // письмо без файла (но у всех один вид)
    void sendMailReport(String email, String eventTitle, Context context);
}
