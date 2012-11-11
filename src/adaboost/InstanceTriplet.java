package adaboost;


import java.util.ArrayList;

public class InstanceTriplet {

	ArrayList<Double> instance;
	int classification;
	double weight;
	
	public InstanceTriplet(){
		this.instance = new ArrayList<Double>();
		classification = 0;
		weight = 0.0;
	} 
	
	public InstanceTriplet(ArrayList<Double> instance){
		this.instance = new ArrayList<Double>();
		this.instance.addAll(instance);
		classification = 0; //double.parseInt((this.instance.remove(this.instance.size()-1).toString()));
		weight = 0.0;
	}

	public ArrayList<Double> getInstance() {
		return instance;
	}

	public void setInstance(ArrayList<Double> instance) {
		this.instance = instance;
	}

	public int getClassification() {
		return classification;
	}

	public void setClassification(int classification) {
		this.classification = classification;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	

	
}
