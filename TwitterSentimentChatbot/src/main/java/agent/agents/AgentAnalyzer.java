package agent.agents;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.*;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utils.Sentiments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalTime;

public class AgentAnalyzer extends AgentBase{
	
	private static final long serialVersionUID = 1L;
	public static final String NICKNAME = "Analyzer";

	protected void setup(){
		super.setup();
		this.type = AgentModel.ANALYZER;
		addBehaviour(new Analyzer());
		registerAgentDF();
	}

	private class Analyzer extends CyclicBehaviour{

		public void reset() {
			super.reset();
		}

		@Override
		public void action() {
			ACLMessage message = receive();
			if(message!=null) {
				String [] arguments = message.getContent().split("///");
				long chatId = Long.parseLong(arguments[0]);
				JsonObject output = finalJSON(arguments[1]);
				
				LocalTime l = LocalTime.now();
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String result = (gson.toJson(output));
				String pretty = "";
				JsonArray array = output.getAsJsonArray("Tweets");
				System.out.println("Array" + array.toString());
				for (int i=0; i< array.size(); i++) {
					JsonObject obj = array.get(i).getAsJsonObject();
					String id = obj.get("Id").toString();
					String nickname = obj.get("Nickname").toString();
					String text = obj.get("Text").toString();
					String scr = obj.get("score").toString();
					String mgn = obj.get("magnitude").toString();
					pretty = pretty + "Id: " + id + "\n";
					pretty = pretty + "Nickname: " + nickname + "\n";
					pretty = pretty + "Text: " + text + "\n";
					pretty = pretty + "Score: " + scr + "\n";
					pretty = pretty + "Magnitude: " + mgn + "\n";
					pretty = pretty + "*************" + "\n";
				}
				//Files.write(Paths.get("Exports\\sentiments"+l.getHour()+l.getMinute()+l.getSecond()+".json"), gson.toJson(output).getBytes());
				ACLMessage analysis = new ACLMessage(ACLMessage.REQUEST);
				analysis.setSender(getAID());
				AID id = new AID("Output@192.168.1.106:1200/JADE", AID.ISGUID);
				analysis.addReceiver(id);
				analysis.setContent(chatId + "///" + pretty);
				send(analysis);
			}

		}
	}
	
	/**
	 * Llama al método getSentiment con el texto del tweet y añade los resultados al JSON
	 * @param json
	 * @return
	 */
	public JsonObject finalJSON(String json) {
		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
		JsonArray tweetsArray = jsonObject.getAsJsonArray("Tweets");
		JsonObject output = new JsonObject();
		JsonArray arrayOfTweets = new JsonArray();
		for(JsonElement tweets : tweetsArray) {
			Sentiments result = new Sentiments();
			JsonObject tweetsText = tweets.getAsJsonObject();
			String getText;
			try {
				getText = new String(tweetsText.get("Text").getAsString().getBytes(),"UTF-8");
				result = getSentiment(getText);
				tweetsText.addProperty("score", result.getScore());
				tweetsText.addProperty("magnitude", result.getMagnitude());
				arrayOfTweets.add(tweetsText);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		output.add("Tweets", arrayOfTweets);
		return output;
	}

	/**
	 * Realiza el análisis de sentimientos
	 * @param tweets
	 * @return
	 */
	private Sentiments getSentiment(String tweets){
		Sentiments output = new Sentiments(0, 0);
		try (LanguageServiceClient language = LanguageServiceClient.create()) {
	      Document doc = Document.newBuilder().setContent(tweets).setType(Type.PLAIN_TEXT).build();
	      Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
	      output.setScore(sentiment.getScore());
	      output.setMagnitude(sentiment.getMagnitude());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
}