package adaboost;

import java.util.ArrayList;

public class NBCBuilder {

	private ArrayList<InstanceTriplet> trainingSet;
	private ArrayList<InstanceTriplet> testSet;
	
	
	
	public void devideDataset(ArrayList<InstanceTriplet> dataset, double ratio){
		this.getTrainingSet().clear();
		this.getTrainingSet().addAll(dataset.subList(0, (int) (dataset.size()*ratio)));
		
		this.getTestSet().clear();
		this.getTestSet().addAll(dataset.subList((int) (dataset.size()*ratio), dataset.size()));
	}
	
	public ArrayList<InstanceTriplet> getTrainingSet() {
		return trainingSet;
	}
	public void setTrainingSet(ArrayList<InstanceTriplet> trainingSet) {
		this.trainingSet = trainingSet;
	}
	public ArrayList<InstanceTriplet> getTestSet() {
		return testSet;
	}
	public void setTestSet(ArrayList<InstanceTriplet> testSet) {
		this.testSet = testSet;
	}
	
	
	
}
