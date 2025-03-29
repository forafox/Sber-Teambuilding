package com.jellyone.telegram.bot.controllers.keyboard;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.List;

public interface Controller {
    List<BotApiMethod<?>> routeByState(long chatId, String userInput);
}
