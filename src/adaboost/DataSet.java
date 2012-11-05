package adaboost;

import java.util.ArrayList;

public class DataSet {
	
	private ArrayList<InstanceTriplet> instances;
	private int[][] attrNumberOfValues;
	private int[] classes;
	
	public int[] getClasses() {
		return classes;
	}

	public void setClasses(int[] classes) {
		this.classes = classes;
	}

	public DataSet() {
		this.instances = new ArrayList<InstanceTriplet>();
		
	}

	public ArrayList<InstanceTriplet> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<InstanceTriplet> instances) {
		this.instances = instances;
	}

	public int[][] getAttrNumberOfValues() {
		return attrNumberOfValues;
	}

	public void setAttrNumberOfValues(int[][] attrNumberOfValues) {
		this.attrNumberOfValues = attrNumberOfValues;
	}
	
	

}
