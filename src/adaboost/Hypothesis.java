package adaboost;

import java.util.ArrayList;



public class Hypothesis {

	
	public enum HypothesisType{NBC, DTC}
	
	private HypothesisType type;
	private DataSet trainingSet;
	private DataSet testSet;
	private double weight;
	private double error;
	private int DTCMaxDepth;
	private boolean good;
	private ArrayList<Integer> classifications;
	
	public Hypothesis(HypothesisType type, int DTCMaxDepht, DataSet testSet, DataSet trainingSet){
		
		this.testSet = testSet;
		this.trainingSet = trainingSet;
		this.type = type;
		this.error = 0.0;
		this.DTCMaxDepth = DTCMaxDepht;
		this.good = true;
		this.classifications = new ArrayList<Integer>();
	}
	
	public double doClassification(){
		double result = 0.0;
		
		if(this.type == HypothesisType.NBC){
			NBCBuilder NBC = new NBCBuilder(this.trainingSet);
			NBC.train();
			result = NBC.testSet(NBC.getTrainingSet());
			NBC.testSet(this.testSet);
		}
		else if(this.type == HypothesisType.DTC){
			DTCTree DTC = new DTCTree(trainingSet, DTCMaxDepth);
			System.out.println(DTC.classifyTestSet(trainingSet));
		}
		for(InstanceTriplet it: this.testSet.getInstances()){
			classifications.add(it.getClassification());
		}
		
		return result;
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

	public boolean isGood() {
		return good;
	}

	public void setGood(boolean good) {
		this.good = good;
	}

	public ArrayList<Integer> getClassifications() {
		return classifications;
	}

	public void setClassifications(ArrayList<Integer> classifications) {
		this.classifications = classifications;
	}
	
}
