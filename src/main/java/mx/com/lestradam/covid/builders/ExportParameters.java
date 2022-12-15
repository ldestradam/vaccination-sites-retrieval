package mx.com.lestradam.covid.builders;

public class ExportParameters {
	
	// required parameters
	private String outputNodes;
	private String outputEdges;
	
	// optional parameters
	private String age;
	private String dose;
	
	
	public String getOutputNodes() {
		return outputNodes;
	}
	public String getOutputEdges() {
		return outputEdges;
	}
	public String getAge() {
		return age;
	}
	public String getDose() {
		return dose;
	}
	
	private ExportParameters (ExportParametersBuilder builder) {
		this.outputNodes = builder.outputNodes;
		this.outputEdges = builder.outputEdges;
		this.age = builder.age;
		this.dose = builder.dose;
	}
	
	//Builder Class
	public static class ExportParametersBuilder {
		// required parameters
		private String outputNodes;
		private String outputEdges;
		
		// optional parameters
		private String age;
		private String dose;
		
		public ExportParametersBuilder(String outputNodes, String outputEdges) {
			this.outputNodes = outputNodes;
			this.outputEdges = outputEdges;
		}
		
		public ExportParametersBuilder setAge(String age) {
			this.age = age;
			return this;
		}
		
		public ExportParametersBuilder setDose(String dose) {
			this.dose = dose;
			return this;
		}
		
		public ExportParameters build() {
			return new ExportParameters(this);
		}
	}

}
