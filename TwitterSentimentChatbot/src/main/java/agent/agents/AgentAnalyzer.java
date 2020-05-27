package agent.agents;

import agent.launcher.AgentBase;
import agent.launcher.AgentModel;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utils.Sentiments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

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
			ArrayList<Sentiments> tuples = new ArrayList<>();
			HashMap<Sentiments.SentimentName, Integer> modeMap = new HashMap<>();
			double totalMagnitude = 0.0;
			ACLMessage message = receive();
			if(message!=null) {
				String [] arguments = message.getContent().split("///");
				long chatId = Long.parseLong(arguments[0]);
				JsonObject output = finalJSON(arguments[1]);

				String pretty = "";
				JsonArray array = output.getAsJsonArray("Tweets");
				for (int i=0; i< array.size(); i++) {
					JsonObject obj = array.get(i).getAsJsonObject();
					String scr = obj.get("score").toString();
					String mgn = obj.get("magnitude").toString();
					float score = Float.parseFloat(scr);
					float magnitude = Float.parseFloat(mgn);
					totalMagnitude += magnitude;
					Sentiments tuple = new Sentiments(score, magnitude);
					tuples.add(tuple);
				}
				double cumulative = 0.0;
				for (Sentiments tuple: tuples){
					cumulative += tuple.getScore() * tuple.getMagnitude();
				}
				double average = 0.0;
				if (totalMagnitude > 0) average = cumulative/totalMagnitude;
				cumulative = 0.0;
				for (Sentiments tuple: tuples){
					cumulative = pow(tuple.getScore() - average, 2);
					Sentiments.SentimentName sentimentName = Sentiments.classify(tuple.getScore());
					Integer instances = modeMap.get(sentimentName);
					if (instances == null){
						instances = 0;
					}
					instances += 1;
					modeMap.put(sentimentName, instances);
				}
				double stddev  = sqrt(cumulative / tuples.size());

				Integer record = 0;
				Sentiments.SentimentName mode = Sentiments.SentimentName.NEUTRAL;
				for (Map.Entry<Sentiments.SentimentName, Integer> entry : modeMap.entrySet())
				{
					if(entry.getValue() >= record){
						mode = entry.getKey();
						record = entry.getValue();
					}
				}
				pretty = "Number of tweets analyzed: " + tuples.size();
				pretty += "\nThe average sentiment for your search is: " + Sentiments.classify(average).toString();
				pretty += "\nThe confidence for this result is: " + (Math.round((1 - stddev) * 10000.00) / 100.00) + "%";
				pretty += "\nThe sentiment with the most appearances is: " + mode.toString() + " with " + record + " appearances.";

				ACLMessage analysis = new ACLMessage(ACLMessage.REQUEST);
				analysis.setSender(getAID());
				AID id = new AID("Output@192.168.1.55:1200/JADE", AID.ISGUID);
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