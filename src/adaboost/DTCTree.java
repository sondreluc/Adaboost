package adaboost;

import java.util.ArrayList;

public class DTCTree {
	
	private DataSet dataSet;
	
	
	public DTCTree(DataSet dataSet) {
		this.dataSet = dataSet;
		
	}
	
	/*
	 * Create a decision tree based on the data set given
	 */
	public DTCNode buildTree(DTCNode root, ArrayList<Integer> remainingAttributes, int level){
		System.out.println("level: " + level);
		int bestAttribute = -1;
		double bestGain = 0;
		
		root.setEntropy(calculateEntropy(root));
		
		if(root.getEntropy() == 0) {
			root.setTerminal(true);
			root.setNodeClass(root.getInstances().get(0).classification);
			return root;
		}
		
		for(int i=0; i<remainingAttributes.size(); i++) {
			double gain = calculateGain(root, splitNode(root, remainingAttributes.get(i)));
			
			if(gain > bestGain) {
				bestAttribute = i;
				bestGain = gain;
			}
		}
		
		if(bestAttribute != -1){
			ArrayList<Integer> remainingAttributesCopy = new ArrayList<Integer>();
			remainingAttributesCopy.addAll(remainingAttributes);
			remainingAttributesCopy.remove(bestAttribute);
			
			
			ArrayList<DTCNode> children = splitNode(root, remainingAttributes.get(bestAttribute));
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
	 * Helper method for splitting a node on a given attribute
	 * Returns a list of all the new child nodes
	 */
	public ArrayList<DTCNode> splitNode(DTCNode node, int attribute) {
		ArrayList<DTCNode> nodes = new ArrayList<DTCNode>();
		int[] valueSet = this.dataSet.getAttrNumberOfValues()[attribute];
		
		for(int i=0; i<valueSet.length; i++) {
			
			ArrayList<InstanceTriplet> instances = new ArrayList<InstanceTriplet>();
			
			for(InstanceTriplet instance: node.getInstances()){
				if(instance.getInstance().get(attribute) == valueSet[i]) {
					instances.add(instance);
				}
			}
			
			if(instances.size()!=0){
				DTCNode newNode = new DTCNode(node, attribute);
				newNode.setinstances(instances);
				nodes.add(newNode);
			}
		}
		
		return nodes;
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
	 * Main method for testing
	 */
	public static void main(String[] args){
		DTCNode node = new DTCNode(null, 0);
		
		ArrayList<InstanceTriplet> instances = new ArrayList<InstanceTriplet>();
		
		DataSet set = new DataSet();
		
		DTCTree tree = new DTCTree(set);
		
		set.setClasses(new int[]{0,1});
		set.setAttrNumberOfValues(new int[][]{{1,2},{1,2},{1,2}});
		
		ArrayList<Integer> attributes1 = new ArrayList<Integer>();
		attributes1.add(1);
		attributes1.add(1);
		attributes1.add(1);
		
		ArrayList<Integer> attributes2 = new ArrayList<Integer>();
		attributes2.add(1);
		attributes2.add(1);
		attributes2.add(2);
		
		ArrayList<Integer> attributes3 = new ArrayList<Integer>();
		attributes3.add(1);
		attributes3.add(2);
		attributes3.add(2);
		
		ArrayList<Integer> attributes4 = new ArrayList<Integer>();
		attributes4.add(2);
		attributes4.add(1);
		attributes4.add(1);
		
		ArrayList<Integer> attributes5 = new ArrayList<Integer>();
		attributes5.add(1);
		attributes5.add(2);
		attributes5.add(1);
		
		InstanceTriplet instance1 = new InstanceTriplet(attributes1);
		InstanceTriplet instance2 = new InstanceTriplet(attributes2);
		InstanceTriplet instance3 = new InstanceTriplet(attributes3);
		InstanceTriplet instance4 = new InstanceTriplet(attributes4);
		InstanceTriplet instance5 = new InstanceTriplet(attributes5);
		
		instance1.setClassification(0);
		instance2.setClassification(0);
		instance3.setClassification(0);
		instance4.setClassification(1);
		instance5.setClassification(1);
		
		instances.add(instance1);
		instances.add(instance2);
		instances.add(instance3);
		instances.add(instance4);
		instances.add(instance5);
		
		set.setInstances(instances);
		
		node.setinstances(instances);
		
		ArrayList<Integer> remaining = new ArrayList<Integer>();
		remaining.add(0);
		remaining.add(1);
		remaining.add(2);
		
		
		tree.buildTree(node, remaining, 0);
		
		System.out.println("Best attr: " + node.getAttribute());
		System.out.println("Entropy: " + node.getEntropy());
		
		
		/*
		node.setEntropy(tree.calculateEntropy(node));
		
		double gain = tree.calculateGain(node, tree.splitNode(node, 0));
		
		System.out.println(gain);
		*/
		
		
	}

}


