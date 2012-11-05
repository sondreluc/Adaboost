package adaboost;


import java.util.ArrayList;

public class InstanceTriplet {

	ArrayList<Integer> instance;
	int classification;
	double weight;
	
	public InstanceTriplet(){
		this.instance = new ArrayList<Integer>();
		classification = 0;
		weight = 0.0;
	} 
	
	public InstanceTriplet(ArrayList<Integer> instance){
		this.instance = new ArrayList<Integer>();
		this.instance.addAll(instance);
		classification = 0; //Integer.parseInt((this.instance.remove(this.instance.size()-1).toString()));
		weight = 0.0;
	}

	public ArrayList<Integer> getInstance() {
		return instance;
	}

	public void setInstance(ArrayList<Integer> instance) {
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
