package everyst.analytics.telegram;

import java.util.ArrayList;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TeleBot extends TelegramLongPollingBot{

	private String username, botToken;
	private ArrayList<Long> trustedChatIDs;
	
	public TeleBot(String username, String botToken, ArrayList<Long> trustedChatIDs) {
		super();
		this.username = username;
		this.botToken = botToken;
		this.trustedChatIDs = trustedChatIDs;
	}
	
	public void sendMessage(String message) throws TelegramApiException {
		for (int i = 0; i < trustedChatIDs.size(); i++) {
			SendMessage sendMessage = new SendMessage().setChatId(trustedChatIDs.get(i)).setText(message);
			execute(sendMessage);			
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			// TODO Implement responses
		}
	}

	@Override
	public String getBotUsername() {
		return username;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

}
