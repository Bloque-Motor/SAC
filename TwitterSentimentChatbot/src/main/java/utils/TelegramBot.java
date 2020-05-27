package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;


public class TelegramBot extends TelegramLongPollingBot{

	public static HashMap<Long,String> pending;
	public static boolean analyzing;
	public static final String command1 = "/help";
	public static final String command2 = "/analyze";

	public TelegramBot() {
		pending = new HashMap<Long, String>();
		analyzing = false;
	}

	/**
	 * When our chatbot receives a message:
	 */
	@Override
	public void onUpdateReceived(Update update) {
		// Get users message:
		String messageTextReceived = update.getMessage().getText();
		// Get id from user chat:
		final long chatId = update.getMessage().getChatId();
		// Creates a new message:
		SendMessage message = new SendMessage().setChatId(chatId).setText("¡Welcome to TwitterSentimentChatbot!");
		Message msg = update.getMessage();

		switch(messageTextReceived) {
		case command1:
			message = new SendMessage().setChatId(chatId).setText("With TwitterSentimentChatbot "
					+ "you can get the sentiment analysis for any keyword in Twitter! " 
					+ "You just have to choose command /alazyze and follow the next steps.");
			break;
		case command2:
			message = new SendMessage().setChatId(chatId).setText("Please enter the keyword or hashtag "
					+ "to analyze.");
			analyzing = true;
			break;			
		default:
			if (analyzing) {
				pending.put(chatId, messageTextReceived);
				message = new SendMessage().setChatId(chatId).setText("Analyzing...");
				analyzing = false;
				break;
			} else {
				System.out.print(analyzing);
				message = new SendMessage().setChatId(chatId).setText("Invalid command, try one of these...");
				SendMessage answer = new SendMessage();
				answer.enableMarkdown(true);
				answer.setReplyMarkup(getSettingsKeyboard(command1, command2));
				answer.setReplyToMessageId(msg.getMessageId());
				answer.setChatId(msg.getChatId());
				answer.setText(command1);
				message = sendChooseOptionMessage(chatId, msg.getMessageId(), getSettingsKeyboard(command1, command2));
				break;	
			}
		}
		try {
			// Send message:
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método para seleccionar opciones dentro de un teclado
	 */
	private static ReplyKeyboardMarkup getSettingsKeyboard(String firstOption, String secondOption) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(false);
		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow keyboardFirstRow = new KeyboardRow();
		keyboardFirstRow.add(firstOption);
		keyboardFirstRow.add(secondOption);
		keyboard.add(keyboardFirstRow);
		replyKeyboardMarkup.setKeyboard(keyboard);
		return replyKeyboardMarkup;
	}

	/**
	 * Responder al usuario (con el teclado generado en getSettingsKeyboard)
	 */
	private static SendMessage sendChooseOptionMessage(Long chatId, Integer messageId, ReplyKeyboard replyKeyboard) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(chatId.toString());
		sendMessage.setReplyToMessageId(messageId);
		sendMessage.setReplyMarkup(replyKeyboard);
		sendMessage.setText("Try one of the options in the menu :)");
		return sendMessage;
	}

	@Override
	public String getBotUsername() {
		return "TwitterSentimentChatbot";
	}

	@Override
	public String getBotToken() {
		return "1129265153:AAE2v9x6dIIWclgCX8M_YwwILey4ETlzczE";
	}

	public void sendMessageToUser(long id, String text) {
		try {
			SendMessage message = new SendMessage().setChatId(id).setText(text);
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
