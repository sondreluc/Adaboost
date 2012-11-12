package adaboost;



public class Hypothesis {

	
	public enum HypothesisType{NBC, DTC}
	
	private HypothesisType type;
	private DataSet trainingSet;
	private DataSet testSet;
	private double weight;
	private double error;
	private int DTCMaxDepth;
	private double ratio;
	
	public Hypothesis(HypothesisType type, int DTCMaxDepht, DataSet testSet, DataSet trainingSet){
		
		this.testSet = testSet;
		this.trainingSet = trainingSet;
		this.type = type;
		this.error = 0.0;
		this.DTCMaxDepth = DTCMaxDepht;
		
	}
	
	public double doClassification(){
		//double result;
		
		if(this.type == HypothesisType.NBC){
			NBCBuilder NBC = new NBCBuilder(trainingSet, testSet);
			NBC.train();
			System.out.println(NBC.test());
		}
		
		else if(this.type == HypothesisType.DTC){
			DTCTree DTC = new DTCTree(trainingSet, DTCMaxDepth);
			System.out.println(DTC.classifyTestSet(trainingSet));
		}
		return 0.0;
	}
	
	public DataSet getTrainingSet() {
		return trainingSet;
	}
	public void setTrainingSet(DataSet trainingSet) {
		this.trainingSet = trainingSet;
	}
	public DataSet getTestSet() {
		return testSet;
	}
	public void setTestSet(DataSet testSet) {
		this.testSet = testSet;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public HypothesisType getType() {
		return type;
	}

	public void setType(HypothesisType type) {
		this.type = type;
	}

	public int getDTCMaxDepth() {
		return DTCMaxDepth;
	}

	public void setDTCMaxDepth(int dTCMaxDepth) {
		DTCMaxDepth = dTCMaxDepth;
	}
	
}
