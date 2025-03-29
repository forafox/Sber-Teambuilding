package com.jellyone.telegram.bot.controllers.inline;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.List;

public interface InlineController {
    List<BotApiMethod<?>> routeByState(long chatId, String userInput, int messageID);
}
