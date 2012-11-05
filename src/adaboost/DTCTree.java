package adaboost;

import java.util.ArrayList;

public class DTCTree {
	
	private DataSet dataSet;
	private DTCNode rootNode;
	
	
	public DTCTree(DataSet dataSet) {
		this.dataSet = dataSet;
		this.rootNode = null;
		
	}
	
	/*
	 * Create a decision tree based on the data set given
	 */
	public DTCNode buildTree(DTCNode root, ArrayList<Integer> remainingAttributes, int level){
		
		int bestAttribute = -1;
		double bestGain = 0;
		
		root.setEntropy(calculateEntropy(root));
		
		//No more instances. Return plurality classification of parent node
		if(root.getInstances().size() == 0) {
			root.setTerminal(true);
			root.setNodeClass(pluralityClass(root.getParent()));
			return root;
			
		}
		
		//All the attributes are of the same class, return root with that class.
		else if(root.getEntropy() == 0) {
			root.setTerminal(true);
			root.setNodeClass(root.getInstances().get(0).classification);
			return root;
		}
		
		
		//No more attributes to split on. Do plurality classification.
		else if(remainingAttributes.size() == 0) {
			
			root.setTerminal(true);
			root.setNodeClass(pluralityClass(root));
			return root;
			
		}
		
		for(int i=0; i<remainingAttributes.size(); i++) {
			double gain = calculateGain(root, splitNode(root, remainingAttributes.get(i), level));
			
			if(gain > bestGain) {
				bestAttribute = remainingAttributes.get(i);
				bestGain = gain;
			}
		}
		
		if(bestAttribute != -1){
			ArrayList<Integer> remainingAttributesCopy = new ArrayList<Integer>();
			remainingAttributesCopy.addAll(remainingAttributes);
			remainingAttributesCopy.remove(remainingAttributesCopy.indexOf(bestAttribute));
			
			ArrayList<DTCNode> children = splitNode(root, remainingAttributes.get(remainingAttributes.indexOf(bestAttribute)), level+1);
			for(DTCNode child: children) {
				child.setParent(root);
				buildTree(child, remainingAttributesCopy, level+1);
			}
			
			root.setChildren(children);
			root.setAttribute(bestAttribute);
		}
		
		return root;
	}
	
	/*
	 * Helper method for calculating the entropy of a node
	 */
	public double calculateEntropy(DTCNode node) {
		double entropy = 0.0;
		int numberOfClasses = this.dataSet.getClasses().length;
		int totalInstances = node.getInstances().size();
		
		if(totalInstances == 0)
			return 0.0;
		
		for(int i=0; i<numberOfClasses; i++){
			
			int count = 0;
			
			for(InstanceTriplet instance: node.getInstances()){
				if(instance.getClassification() == i) {
					count++;
				}
			}
			
			double prob = (double) count/totalInstances;
			
			if(count > 0) {
				entropy += -prob * (Math.log(prob) / Math.log(2));
			}
		}
		
		return entropy;
	}
	
	/*
	 * Helper method for calculating the gain of a certain split
	 */
	public double calculateGain(DTCNode node, ArrayList<DTCNode> childNodes){
		double totalEntropy = 0.0;
		
		int totalInstances = node.getInstances().size();
		
		for(DTCNode childNode: childNodes){
			childNode.setEntropy(calculateEntropy(childNode));
			double childInstances = childNode.getInstances().size();
			totalEntropy += ((double) childInstances/totalInstances) * childNode.getEntropy(); 
		}
		
		return node.getEntropy() - totalEntropy;
		
	}
	
	/*
	 * Helper method for splitting a node on a given attribute
	 * Returns a list of all the new child nodes
	 */
	public ArrayList<DTCNode> splitNode(DTCNode node, Integer attribute, int level) {
		ArrayList<DTCNode> nodes = new ArrayList<DTCNode>();
		int[] valueSet = this.dataSet.getAttrNumberOfValues()[attribute];
		
		for(int i=0; i<valueSet.length; i++) {
			
			ArrayList<InstanceTriplet> instances = new ArrayList<InstanceTriplet>();
			
			for(InstanceTriplet instance: node.getInstances()){
				if(instance.getInstance().get(attribute) == valueSet[i]) {
					instances.add(instance);
				}
			}
			
			DTCNode newNode = new DTCNode(node, attribute, valueSet[i], level);
			newNode.setinstances(instances);
			nodes.add(newNode);
			
		}
		
		return nodes;
	}
	
	/*
	 * Helper method for deciding class with plurality classification
	 */
	public int pluralityClass(DTCNode node) {
		int[] counter = new int[this.dataSet.getClasses().length];
		
		for(int i=0; i<this.dataSet.getClasses().length; i++) {
			for(InstanceTriplet instance: node.getInstances()){
				if(instance.getClassification() == this.dataSet.getClasses()[i])
					counter[i]++;
			}
		}
		int max = 0;
		int mostCommonAttribute = 0;
		for(int i=0; i<counter.length; i++) {
			if(counter[i] > max){
				max = counter[i];
				mostCommonAttribute = i;
			}
		}
		
		return this.dataSet.getClasses()[mostCommonAttribute];
	}
	
	/*
	 * Method for classifying an instance with a decision tree
	 */
	public int classify(InstanceTriplet instance, DTCNode node) {
		
		if(node.isTerminal()) {
			return node.getNodeClass();
		}
		else {
			
			int testAttribute = node.getAttribute();
			int testResult = instance.getInstance().get(testAttribute);
			
			//Go to correct child node, based on the test attribute
			for(DTCNode child: node.getChildren()){
				if(child.getAttributeValue() == testResult){
					return classify(instance, child);
				}
			}
			
			//Error
			return -1;
		}
	}
	
	
	
	/*
	 * Main method for testing
	 */
	public static void main(String[] args){
		DTCNode node = new DTCNode(null, -1, -1, 0);
		
		ArrayList<InstanceTriplet> instances = new ArrayList<InstanceTriplet>();
		
		DataSet set = new DataSet();
		
		DTCTree tree = new DTCTree(set);
		tree.rootNode = node;
		
		
		//Creating example from the exercise text
		set.setClasses(new int[]{0,1});
		set.setAttrNumberOfValues(new int[][]{{0,1,2},{0,1,2},{0,1}});
		
		ArrayList<Integer> attributes1 = new ArrayList<Integer>();
		attributes1.add(0);
		attributes1.add(0);
		attributes1.add(0);
		
		ArrayList<Integer> attributes2 = new ArrayList<Integer>();
		attributes2.add(0);
		attributes2.add(0);
		attributes2.add(0);
		
		ArrayList<Integer> attributes3 = new ArrayList<Integer>();
		attributes3.add(0);
		attributes3.add(1);
		attributes3.add(0);
		
		ArrayList<Integer> attributes4 = new ArrayList<Integer>();
		attributes4.add(0);
		attributes4.add(0);
		attributes4.add(1);
		
		ArrayList<Integer> attributes5 = new ArrayList<Integer>();
		attributes5.add(0);
		attributes5.add(2);
		attributes5.add(1);
		
		ArrayList<Integer> attributes6 = new ArrayList<Integer>();
		attributes6.add(1);
		attributes6.add(2);
		attributes6.add(0);
		
		ArrayList<Integer> attributes7 = new ArrayList<Integer>();
		attributes7.add(1);
		attributes7.add(2);
		attributes7.add(0);
		
		ArrayList<Integer> attributes8 = new ArrayList<Integer>();
		attributes8.add(1);
		attributes8.add(1);
		attributes8.add(0);
		
		ArrayList<Integer> attributes9 = new ArrayList<Integer>();
		attributes9.add(1);
		attributes9.add(0);
		attributes9.add(0);
		
		ArrayList<Integer> attributes10 = new ArrayList<Integer>();
		attributes10.add(1);
		attributes10.add(2);
		attributes10.add(1);
		
		ArrayList<Integer> attributes11 = new ArrayList<Integer>();
		attributes11.add(1);
		attributes11.add(1);
		attributes11.add(1);
		
		ArrayList<Integer> attributes12 = new ArrayList<Integer>();
		attributes12.add(2);
		attributes12.add(0);
		attributes12.add(0);
		
		ArrayList<Integer> attributes13 = new ArrayList<Integer>();
		attributes13.add(2);
		attributes13.add(1);
		attributes13.add(0);
		
		ArrayList<Integer> attributes14 = new ArrayList<Integer>();
		attributes14.add(2);
		attributes14.add(0);
		attributes14.add(0);
		
		ArrayList<Integer> attributes15 = new ArrayList<Integer>();
		attributes15.add(2);
		attributes15.add(1);
		attributes15.add(1);
		
		ArrayList<Integer> attributes16 = new ArrayList<Integer>();
		attributes16.add(2);
		attributes16.add(2);
		attributes16.add(1);
		
		InstanceTriplet instance1 = new InstanceTriplet(attributes1);
		InstanceTriplet instance2 = new InstanceTriplet(attributes2);
		InstanceTriplet instance3 = new InstanceTriplet(attributes3);
		InstanceTriplet instance4 = new InstanceTriplet(attributes4);
		InstanceTriplet instance5 = new InstanceTriplet(attributes5);
		InstanceTriplet instance6 = new InstanceTriplet(attributes6);
		InstanceTriplet instance7 = new InstanceTriplet(attributes7);
		InstanceTriplet instance8 = new InstanceTriplet(attributes8);
		InstanceTriplet instance9 = new InstanceTriplet(attributes9);
		InstanceTriplet instance10 = new InstanceTriplet(attributes10);
		InstanceTriplet instance11 = new InstanceTriplet(attributes11);
		InstanceTriplet instance12 = new InstanceTriplet(attributes12);
		InstanceTriplet instance13 = new InstanceTriplet(attributes13);
		InstanceTriplet instance14 = new InstanceTriplet(attributes14);
		InstanceTriplet instance15 = new InstanceTriplet(attributes15);
		InstanceTriplet instance16 = new InstanceTriplet(attributes16);

		instance1.setClassification(1);
		instance2.setClassification(1);
		instance3.setClassification(1);
		instance4.setClassification(0);
		instance5.setClassification(0);
		instance6.setClassification(1);
		instance7.setClassification(1);
		instance8.setClassification(0);
		instance9.setClassification(1);
		instance10.setClassification(1);
		instance11.setClassification(0);
		instance12.setClassification(1);
		instance13.setClassification(1);
		instance14.setClassification(0);
		instance15.setClassification(0);
		instance16.setClassification(1);
		
		instances.add(instance1);
		instances.add(instance2);
		instances.add(instance3);
		instances.add(instance4);
		instances.add(instance5);
		instances.add(instance6);
		instances.add(instance7);
		instances.add(instance8);
		instances.add(instance9);
		instances.add(instance10);
		instances.add(instance11);
		instances.add(instance12);
		instances.add(instance13);
		instances.add(instance14);
		instances.add(instance15);
		instances.add(instance16);

		set.setInstances(instances);
		
		node.setinstances(instances);
		
		ArrayList<Integer> remaining = new ArrayList<Integer>();
		remaining.add(0);
		remaining.add(1);
		remaining.add(2);
		
		
		tree.buildTree(node, remaining, 0);
				
		System.out.println("Instance is of class: " + tree.classify(instance8, node));
		
		
		/*
		node.setEntropy(tree.calculateEntropy(node));
		
		double gain = tree.calculateGain(node, tree.splitNode(node, 0));
		
		System.out.println(gain);
		*/
		
		
	}

}


