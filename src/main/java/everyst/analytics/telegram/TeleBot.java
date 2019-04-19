package everyst.analytics.telegram;

import java.sql.SQLException;
import java.util.ArrayList;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;

import everyst.analytics.listner.App;
import everyst.analytics.listner.dataManagement.Logger;
import everyst.analytics.listner.userInterface.Commands;

public class TeleBot extends TelegramLongPollingBot {

	private String username, botToken;
	private ArrayList<Long> trustedChatIDs;
	private Commands commands;
	private BotSession session;

	public static TeleBot init(String username, String token, ArrayList<Long> trustedChatIDs, Commands commands)
			throws TelegramApiRequestException {
		ApiContextInitializer.init();
		TelegramBotsApi botsApi = new TelegramBotsApi();
		TeleBot teleBot;

		BotSession bs = botsApi.registerBot(teleBot = new TeleBot(username, token, trustedChatIDs, commands));
		teleBot.setSession(bs);
		return teleBot;
	}

	private TeleBot(String username, String botToken, ArrayList<Long> trustedChatIDs, Commands commands) {
		super();
		this.username = username;
		this.botToken = botToken;
		this.trustedChatIDs = trustedChatIDs;
		this.commands = commands;
	}

	public void setSession(BotSession session) {
		this.session = session;
	}

	public void sendMessage(String message) throws TelegramApiException {
		for (int i = 0; i < trustedChatIDs.size(); i++) {
			SendMessage sendMessage = new SendMessage().setChatId(trustedChatIDs.get(i)).setText(message);
			execute(sendMessage);
		}
	}

	public void sendMessage(String message, long... ids) throws TelegramApiException {
		for (int i = 0; i < ids.length; i++) {
			SendMessage sendMessage = new SendMessage().setChatId(ids[i]).setText(message);
			execute(sendMessage);
		}
	}

	public void stop() {
		if (!App.DEBUG)
			try {
				sendMessage("I am shutting down! Just to let you know!");
			} catch (TelegramApiException e) {
				Logger.getInstance().handleError(e);
			}
		session.stop();
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			try {
				Message message = update.getMessage();

				// If the user is not trusted tell him he is not allowed to talk to me \(>_<)/
				if (!trustedChatIDs.contains(message.getChatId())) {
					sendMessage(Responses.UNKOWN_USER_PERMISSION_DENIED, message.getChatId());
					return;
				}

				switch (message.getText().toLowerCase()) {
				case Responses.COMMANDS_STILL_ALIVE:
					sendMessage(commands.getLast5LikeDates());
					break;
				case Responses.COMMANDS_STATUS:
					sendMessage(commands.getStatus());
					break;
				default:
					break;
				}
			} catch (TelegramApiException | SQLException e) {
				Logger.getInstance().handleError(e);
			}
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
