package adaboost;

import java.util.ArrayList;

public class DTCTree {
	
	private DataSet dataSet;
	private DTCNode rootNode;
	
	
	public DTCTree(DataSet dataSet, int maxDepht) {
		this.dataSet = dataSet;
		DTCNode root = new DTCNode(null, -1, -1, 0);
		
		ArrayList<Integer> initialAttributes = new ArrayList<Integer>();
		for(int i=0; i<dataSet.getAttrNumberOfValues().length; i++){
			initialAttributes.add(i);
		}
		
		root.setInstances(dataSet.getInstances());
		
		this.rootNode = buildTree(root, initialAttributes, 0, maxDepht);
	}
	
	/*
	 * Create a decision tree based on the data set given
	 */
	public DTCNode buildTree(DTCNode root, ArrayList<Integer> remainingAttributes, int level, int maxDepht){
		
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
			InstanceTriplet firstInstance = root.getInstances().get(0);
			root.setNodeClass(firstInstance.getInstance().get(firstInstance.getInstance().size()-1).intValue());
			return root;
		}
		
		
		//No more attributes to split on. Do plurality classification.
		else if(remainingAttributes.size() == 0) {
			
			root.setTerminal(true);
			root.setNodeClass(pluralityClass(root));
			return root;
			
		}
		
		else if(maxDepht == 0){
			root.setTerminal(true);
			root.setNodeClass(pluralityClass(root));
			return root;
		}
		
		for(int i=0; i<remainingAttributes.size(); i++) {
			double gain = calculateGain(root, splitNode(root, remainingAttributes.get(i), level));
			
			if(level == 0)
				System.out.println("Attr:" +i +", gain: " + gain);
			
			if(gain > bestGain) {
				bestAttribute = remainingAttributes.get(i);
				bestGain = gain;
			}
		}
		
		//if(level==0 || level==1)
			//System.out.println("lev:"+level + ", best: " + bestGain);
		
		if(bestAttribute != -1){
			ArrayList<Integer> remainingAttributesCopy = new ArrayList<Integer>();
			remainingAttributesCopy.addAll(remainingAttributes);
			remainingAttributesCopy.remove(remainingAttributesCopy.indexOf(bestAttribute));
			
			ArrayList<DTCNode> children = splitNode(root, remainingAttributes.get(remainingAttributes.indexOf(bestAttribute)), level+1);
			for(DTCNode child: children) {
				child.setParent(root);
				buildTree(child, remainingAttributesCopy, level+1, maxDepht-1);
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
				if(instance.getInstance().get(instance.getInstance().size()-1) == i) {
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
		double totalWeight = 0.0;
		
		for(DTCNode childNode: childNodes){
			totalWeight += childNode.getWeight();
		}
		
		for(DTCNode childNode: childNodes){
			childNode.setEntropy(calculateEntropy(childNode));
			
			totalEntropy += ((double) childNode.getWeight()/totalWeight) * childNode.getEntropy(); 
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
			newNode.setInstances(instances);
			newNode.setWeight(instances);
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
				if(instance.getInstance().get(instance.getInstance().size()-1) == this.dataSet.getClasses()[i])
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
	 * 
	 */
	public int classify(InstanceTriplet instance){
		return classifyHelper(instance, this.rootNode);
	}
	
	/*
	 * Method for classifying an instance with a decision tree
	 */
	public int classifyHelper(InstanceTriplet instance, DTCNode node) {
		
		if(node.isTerminal() || node.getChildren() == null) {
			return node.getNodeClass();
		}
		else {
			
			int testAttribute = node.getAttribute();
			double testResult = instance.getInstance().get(testAttribute);
			
			//Go to correct child node, based on the test attribute
			for(DTCNode child: node.getChildren()){
				if(child.getAttributeValue() == testResult){
					return classifyHelper(instance, child);
				}
			}
			
			//Error
			return -1;
		}
	}
	
	/*
	 * 
	 */
	public double classifyTestSet(DataSet set){
		int numOfInstances = set.getInstances().size();
		int errors = 0;
		
		for(InstanceTriplet instance: set.getInstances()){
			instance.setClassification(this.classify(instance));
			
			if(instance.getClassification() != instance.getInstance().get(instance.getInstance().size()-1)){
				errors +=1;
			}
		}
				
		return ((double) errors/numOfInstances);
		
		
		
	}
	
	
	/*
	 * Main method for testing
	 */
	public static void main(String[] args){
		/*
		ArrayList<InstanceTriplet> instances = new ArrayList<InstanceTriplet>();
		
		DataSet set = new DataSet();
		
		//Creating example from the exercise text
		set.setClasses(new int[]{0,1});
		set.setAttrNumberOfValues(new int[][]{{0,1,2},{0,1,2},{0,1}});
		
		ArrayList<Double> attributes1 = new ArrayList<Double>();
		attributes1.add(0.0);
		attributes1.add(0.0);
		attributes1.add(0.0);
		
		ArrayList<Double> attributes2 = new ArrayList<Double>();
		attributes2.add(0.0);
		attributes2.add(0.0);
		attributes2.add(0.0);
		
		ArrayList<Double> attributes3 = new ArrayList<Double>();
		attributes3.add(0.0);
		attributes3.add(1.0);
		attributes3.add(0.0);
		
		ArrayList<Double> attributes4 = new ArrayList<Double>();
		attributes4.add(0.0);
		attributes4.add(0.0);
		attributes4.add(1.0);
		
		ArrayList<Double> attributes5 = new ArrayList<Double>();
		attributes5.add(0.0);
		attributes5.add(2.0);
		attributes5.add(1.0);
		
		ArrayList<Double> attributes6 = new ArrayList<Double>();
		attributes6.add(1.0);
		attributes6.add(2.0);
		attributes6.add(0.0);
		
		ArrayList<Double> attributes7 = new ArrayList<Double>();
		attributes7.add(1.0);
		attributes7.add(2.0);
		attributes7.add(0.0);
		
		ArrayList<Double> attributes8 = new ArrayList<Double>();
		attributes8.add(1.0);
		attributes8.add(1.0);
		attributes8.add(0.0);
		
		ArrayList<Double> attributes9 = new ArrayList<Double>();
		attributes9.add(1.0);
		attributes9.add(0.0);
		attributes9.add(0.0);
		
		ArrayList<Double> attributes10 = new ArrayList<Double>();
		attributes10.add(1.0);
		attributes10.add(2.0);
		attributes10.add(1.0);
		
		ArrayList<Double> attributes11 = new ArrayList<Double>();
		attributes11.add(1.0);
		attributes11.add(1.0);
		attributes11.add(1.0);
		
		ArrayList<Double> attributes12 = new ArrayList<Double>();
		attributes12.add(2.0);
		attributes12.add(0.0);
		attributes12.add(0.0);
		
		ArrayList<Double> attributes13 = new ArrayList<Double>();
		attributes13.add(2.0);
		attributes13.add(1.0);
		attributes13.add(0.0);
		
		ArrayList<Double> attributes14 = new ArrayList<Double>();
		attributes14.add(2.0);
		attributes14.add(0.0);
		attributes14.add(0.0);
		
		ArrayList<Double> attributes15 = new ArrayList<Double>();
		attributes15.add(2.0);
		attributes15.add(1.0);
		attributes15.add(1.0);
		
		ArrayList<Double> attributes16 = new ArrayList<Double>();
		attributes16.add(2.0);
		attributes16.add(2.0);
		attributes16.add(1.0);
		
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
		
		instance1.setWeight(1.0/16.0);
		instance2.setWeight(1.0/16.0);
		instance3.setWeight(1.0/16.0);
		instance4.setWeight(1.0/16.0);
		instance5.setWeight(1.0/16.0);
		instance6.setWeight(1.0/16.0);
		instance7.setWeight(1.0/16.0);
		instance8.setWeight(1.0/16.0);
		instance9.setWeight(1.0/16.0);
		instance10.setWeight(1.0/16.0);
		instance11.setWeight(1.0/16.0);
		instance12.setWeight(1.0/16.0);
		instance13.setWeight(1.0/16.0);
		instance14.setWeight(1.0/16.0);
		instance15.setWeight(1.0/16.0);
		instance16.setWeight(1.0/16.0);
		
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
		
		DTCTree tree = new DTCTree(set,2);
				
		ArrayList<Integer> remaining = new ArrayList<Integer>();
		remaining.add(0);
		remaining.add(1);
		remaining.add(2);
				
		System.out.println("Instance is of class: " + tree.classify(instance8));
		*/
	}

}


