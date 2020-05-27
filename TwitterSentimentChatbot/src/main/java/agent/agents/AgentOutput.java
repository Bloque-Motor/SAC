package agent.agents;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentOutput extends AgentBase {

	private static final long serialVersionUID = 1L;
	public static final String NICKNAME = "Output";
	private TelegramBot bot = new TelegramBot("1129265153:AAE2v9x6dIIWclgCX8M_YwwILey4ETlzczE");	
	
	protected void setup(){
		super.setup();
		this.type = AgentModel.OUTPUT;
		addBehaviour(new Output());
		registerAgentDF();
	}

	private class Output extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage result = receive();
			if(result!=null) {
				String [] arguments = result.getContent().split("///");
				Long chatId = Long.parseLong(arguments[0]);
				String analysis = arguments[1];
				System.out.print(analysis);
				SendMessage message = new SendMessage(chatId, analysis);
				SendResponse sendResponse = bot.execute(message);
			}		
		}
	}
}
