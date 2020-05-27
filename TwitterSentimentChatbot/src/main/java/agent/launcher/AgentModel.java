package agent.launcher;

public enum AgentModel {

	INPUT("Input"),
	SEARCH("Search"),
	ANALYZER("Analyzer"),
	OUTPUT("Output"),
	UNKNOWN("Unknown");
	
	private final String value;
	
	AgentModel(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public static AgentModel getEnum(String value) {
		switch(value) {
			case "Input":
				return INPUT;
			case "Search":
				return SEARCH;
			case "Analyzer":
				return ANALYZER;
			case "Output":
				return OUTPUT;
			default:
				return UNKNOWN;
		}
	}
}
