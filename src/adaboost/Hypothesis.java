package adaboost;

import java.util.ArrayList;



public class Hypothesis {

	
	public enum HypothesisType{NBC, DTC}
	
	ArrayList<InstanceTriplet> dataset;
	double weight;
	double error;
	
	public Hypothesis(ArrayList<InstanceTriplet> data, HypothesisType type, double trainingRatio, double weight){
		this.dataset = new ArrayList<InstanceTriplet>();
		this.dataset.addAll(data);
		this.weight = weight;
		this.error = 0.0;
	}
	
	public void updateWeights(){
		double weightSum = 0;
		for(InstanceTriplet it : this.dataset){
			if (it.getInstance().get(it.getInstance().size()-1).intValue() != it.getClassification()){
				this.error += it.getWeight();
			}
			else{
				it.setWeight((double)this.error/(1.0-this.error));
			}
			weightSum += it.getWeight();
		}
		
		for(InstanceTriplet it : this.dataset){
			it.setWeight((double)it.getWeight()/weightSum);
		}
		
		this.weight = Math.log((double)(1-this.error)/this.error);
		
	}
	
	public ArrayList<InstanceTriplet> getDataset() {
		return dataset;
	}

	public void setDataset(ArrayList<InstanceTriplet> dataset) {
		this.dataset = dataset;
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

	public void initilizeWeights(){
		for(InstanceTriplet it : this.dataset){
			it.setWeight((double)1.0/this.dataset.size());
		}
	}
	
}
