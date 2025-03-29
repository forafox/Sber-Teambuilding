package com.jellyone.telegram.bot.services;

import com.jellyone.telegram.bot.Constants;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.jellyone.telegram.bot.Constants.CANCEL;

@Service
@Profile("telegram")
public class KeyboardService {
    private ReplyKeyboardMarkup menu;
    private ReplyKeyboardMarkup cancel;

    public ReplyKeyboardMarkup menuKeyboard() {
        if (menu != null) {
            return menu;
        }
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.MENU_BTN_MY_EVENTS));
        keyboard.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.MENU_BTN_MY_TASKS));
        row.add(new KeyboardButton(Constants.MENU_BTN_EVENT_TASKS));
        keyboard.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.MENU_BTN_CREATE_TASK));
        keyboard.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(Constants.MENU_BTN_CREATE_FAST_TASK));
        keyboard.add(row);

        menu = new ReplyKeyboardMarkup(keyboard);
        menu.setOneTimeKeyboard(true);
        menu.setResizeKeyboard(true);
        return menu;
    }

    public ReplyKeyboardMarkup cancelKeyboard() {
        if (cancel != null) {
            return cancel;
        }
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Отмена"));
        keyboard.add(row);

        cancel = new ReplyKeyboardMarkup(keyboard);
        cancel.setOneTimeKeyboard(true);
        cancel.setResizeKeyboard(true);
        return cancel;
    }

    public InlineKeyboardMarkup inlineListKeyboard(String[] inlineElementsName, String[] inlineData, int width) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();

        for (int i = 0; i < inlineElementsName.length; i += width) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            for (int j = i; j < i + width && j < inlineElementsName.length; j++) {
                InlineKeyboardButton btn = new InlineKeyboardButton(String.valueOf(inlineElementsName[j]));
                btn.setCallbackData(String.valueOf(inlineData[j]));
                row.add(btn);
            }
            keyboard.add(row);
        }

        InlineKeyboardRow row = new InlineKeyboardRow();
        InlineKeyboardButton btn = new InlineKeyboardButton(CANCEL);
        btn.setCallbackData(CANCEL);
        row.add(btn);

        keyboard.add(row);
        return new InlineKeyboardMarkup(keyboard);
    }
}
