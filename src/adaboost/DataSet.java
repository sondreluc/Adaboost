package adaboost;

import java.util.ArrayList;

public class DataSet {
	
	private ArrayList<InstanceTriplet> instances;
	private int[] attrNumberOfValues;
	
	public DataSet() {
		this.instances = new ArrayList<InstanceTriplet>();
		
	}

	public ArrayList<InstanceTriplet> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<InstanceTriplet> instances) {
		this.instances = instances;
	}

	public int[] getAttrNumberOfValues() {
		return attrNumberOfValues;
	}

	public void setAttrNumberOfValues(int[] attrNumberOfValues) {
		this.attrNumberOfValues = attrNumberOfValues;
	}
	
	

}
