package adaboost;

import java.util.ArrayList;

public class DTCNode {
	
	private DTCNode parent;
	private ArrayList<DTCNode> children;
	private ArrayList<InstanceTriplet> instances;
	private double entropy;
	private int attribute;
	private int attributeValue;
	private boolean isTerminal;
	private int nodeClass;
	private int level; 
	private double weight;
	
	
	public DTCNode(DTCNode parent, int attribute, int attributeValue, int level) {
		this.parent = parent;
		this.instances = new ArrayList<InstanceTriplet>();
		this.attribute = attribute;
		this.attributeValue = attributeValue;
		this.isTerminal = false;
		this.nodeClass = -1;
		this.level = level;
	}

	public DTCNode getParent() {
		return parent;
	}

	public void setParent(DTCNode parent) {
		this.parent = parent;
	}

	public ArrayList<DTCNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<DTCNode> children) {
		this.children = children;
	}

	public ArrayList<InstanceTriplet> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<InstanceTriplet> instances) {
		this.instances = instances;
	}
	
	public void addInstance(InstanceTriplet instance) {
		if(this.instances != null) {
			this.instances.add(instance);
		}
		else {
			this.instances = new ArrayList<InstanceTriplet>();
			this.instances.add(instance);
		}
			
		
	}

	public double getEntropy() {
		return entropy;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

	public boolean isTerminal() {
		return isTerminal;
	}

	public void setTerminal(boolean isTerminal) {
		this.isTerminal = isTerminal;
	}

	public int getNodeClass() {
		return nodeClass;
	}

	public void setNodeClass(int nodeClass) {
		this.nodeClass = nodeClass;
	}
	
	public int getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(int attributeValue) {
		this.attributeValue = attributeValue;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(ArrayList<InstanceTriplet> instances) {
		double weight = 0.0;
		
		for(InstanceTriplet instance: instances){
			weight += instance.getWeight();
		}
		
		this.weight = weight;
	}
	
	
	
	
	
	
	
	
	

}
