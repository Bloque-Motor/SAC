package agent.agents;

import java.util.Map;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import utils.TelegramBot;

public class AgentInput extends AgentBase {

	private static final long serialVersionUID = 1L;
	public static final String NICKNAME = "Input";

	protected void setup(){
		super.setup();
		addBehaviour(new Input());
		this.type = AgentModel.INPUT;
		
		// Initializes context of the API
		ApiContextInitializer.init();
		// Creates a new API bot
		final TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		// Registers the bot
		try {
			telegramBotsApi.registerBot(new TelegramBot());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		registerAgentDF();
	}

	private class Input extends CyclicBehaviour {

		@Override
		public void action() {
			if(TelegramBot.pending.entrySet().iterator().hasNext()) {
				Map.Entry<Long,String> entry = TelegramBot.pending.entrySet().iterator().next();
				
				Long chatId = entry.getKey();
				String keyword = entry.getValue();
				
				System.out.println("Keyword is: " + keyword);
				
				ACLMessage hashtag = new ACLMessage(ACLMessage.REQUEST);
				hashtag.setSender(getAID());
				AID id = new AID("Search@192.168.1.106:1200/JADE", AID.ISGUID);
				hashtag.addReceiver(id);
				String toSend = chatId + "/" + keyword;
				hashtag.setContent(toSend);
				send(hashtag);
				
				TelegramBot.pending.remove(entry.getKey(), entry.getValue());
			}
		}
	}
}
