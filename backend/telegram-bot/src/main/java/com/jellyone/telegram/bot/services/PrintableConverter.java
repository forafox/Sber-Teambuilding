package com.jellyone.telegram.bot.services;

import com.jellyone.telegram.bot.dto.Printable;
import com.jellyone.telegram.bot.Constants;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("telegram")
public class PrintableConverter {
    public String[] listIDFromPrintable(List<? extends Printable> lst) {
        String[] stringIDs = new String[lst.size()];
        for (int i = 0; i < lst.size(); i++) {
            stringIDs[i] = String.valueOf(lst.get(i).getId());
        }
        return stringIDs;
    }
    public String[] listTitleFromPrintable(List<? extends Printable> lst) {
        String[] stringIDs = new String[lst.size()];
        for (int i = 0; i < lst.size(); i++) {
            stringIDs[i] = String.valueOf(lst.get(i).getTitle());
        }
        return stringIDs;
    }

    public String listToString(List<? extends Printable> lst) {
        StringBuilder resultSB = new StringBuilder();
        for (Printable element : lst) {
            resultSB.append(String.format(Constants.TABLE_VAL, element.getTitle()));
        }
        return resultSB.toString();
    }
}
