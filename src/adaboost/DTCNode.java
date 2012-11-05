package adaboost;

import java.util.ArrayList;

public class DTCNode {
	
	private DTCNode parent;
	private ArrayList<DTCNode> children;
	private ArrayList<InstanceTriplet> instances;
	private double entropy;
	private int attribute;
	private boolean isTerminal;
	private int nodeClass;
	
	public DTCNode(DTCNode parent, ArrayList<InstanceTriplet> instances, int attribute) {
		this.parent = parent;
		this.instances = instances;
		this.attribute = attribute;
		this.isTerminal = false;
		this.nodeClass = -1;
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

	public void setinstances(ArrayList<InstanceTriplet> instances) {
		this.instances = instances;
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
	
	
	
	

}
