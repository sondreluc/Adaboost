package adaboost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import adaboost.Hypothesis.HypothesisType;

public class Adaboost {

	public DataSet dataset;
	public DataSet testSet;
	public DataSet trainingSet;
	public ArrayList<Hypothesis> hypothesis;
	private ArrayList<Integer> classifications;
	
	public Adaboost(String datasetFile, int NBCs, int DTCs, double trainingSetSize, int DTCMaxDept, boolean prepData, boolean samme){
		this.hypothesis = new ArrayList<Hypothesis>();
		this.testSet = new DataSet();
		this.trainingSet = new DataSet();
		this.classifications = new ArrayList<Integer>();
		try {
			this.readFromFile(datasetFile, prepData);
		} catch (IOException e) {

		}
		
		this.testSet = new DataSet();
		this.trainingSet = new DataSet();
		
		this.devideDataset(this.dataset, trainingSetSize);
		this.testSet.setAttrNumberOfValues(this.dataset.getAttrNumberOfValues());
		this.trainingSet.setAttrNumberOfValues(this.dataset.getAttrNumberOfValues());
		
		this.testSet.setClasses(this.dataset.getClasses());
		this.trainingSet.setClasses(this.dataset.getClasses());
		
		this.initWeights();
		
		for(int i = 0; i < DTCs; i++){
			while(true){
				Hypothesis h = new Hypothesis(HypothesisType.DTC, DTCMaxDept,this.testSet, this.trainingSet);
				h.doClassification();
				if((samme ? samme(h) : updateWeights(h))){
					hypothesis.add(h);
					break;
				}
			}
		}
		
		for(int i = 0; i < NBCs ; i++){
			while(true){
				Hypothesis h = new Hypothesis(HypothesisType.NBC, DTCMaxDept,this.testSet, this.trainingSet);
				h.doClassification();
				
				if((samme ? samme(h) : updateWeights(h))){
					hypothesis.add(h);
					break;
				}
			}
			
		}

		
		for(int i =0; i< this.getTestSet().getInstances().size(); i++){
			HashMap<Integer, Double> votes = new HashMap<Integer, Double>();
			for(Hypothesis h : this.getHypothesis()){
				if(h.isGood()){
					if(!votes.containsKey(h.getClassifications().get(i))){
						votes.put(h.getClassifications().get(i), h.getWeight());
					}
					else{
						double vote = votes.get(h.getClassifications().get(i));
						vote += h.getWeight();
						votes.put(h.getClassifications().get(i), vote);
					}
				}
			}
			int classification = 0;
			double highestVote = 0.0;
			for(Integer classInteger: votes.keySet()){
				if(votes.get(classInteger)> highestVote){
					classification = classInteger;
					highestVote = votes.get(classInteger);
				}
			}
			this.getClassifications().add(classification);
		}
		
	}
	
	public void initWeights(){
		for(InstanceTriplet it : this.trainingSet.getInstances()){
			it.setWeight((double)1.0/this.trainingSet.getInstances().size());
		}
	}
	

	public boolean updateWeights(Hypothesis h){
		double beta = 1.5; 
		double weightSum = 0.0;
		for(InstanceTriplet it : this.trainingSet.getInstances()){
			if (it.getInstance().get(it.getInstance().size()-1).intValue() != it.getClassification()){
				//System.out.println(it.getInstance().get(it.getInstance().size()-1).intValue()+"-"+it.getClassification());
				h.setError(h.getError()+it.getWeight());
				weightSum += it.getWeight();
			}
		}

		if(h.getError()>=(double)(this.dataset.getClasses().length-1)/(this.dataset.getClasses().length)){
			this.jiggleWeights(beta);
			System.out.println("bad: " + h.getError());
			h.setGood(false);
			return false;
		}
		
		
		if(0 < h.getError() && h.getError() < (double)(this.dataset.getClasses().length-1)/(this.dataset.getClasses().length)){
			for(InstanceTriplet it : this.trainingSet.getInstances()){
				if(it.getInstance().get(it.getInstance().size()-1).intValue() != it.getClassification()){
						it.setWeight(it.getWeight() * ((1.0-h.getError())/h.getError()) * (this.getDataset().getClasses().length-1));
				}
				weightSum += it.getWeight();
			}

			for(InstanceTriplet it : this.trainingSet.getInstances()){
				it.setWeight((it.getWeight()/weightSum));

				//System.out.println(it.getInstance().get(it.getInstance().size()-1).intValue()+"-"+it.getClassification()+" - Weight:"+it.getWeight());
			}
			
			h.setWeight(Math.log(((1.0-h.getError())/h.getError())*(this.dataset.getClasses().length-1)));
		}
		
		else if(h.getError() == 0){
			this.jiggleWeights(beta);
			h.setWeight((double)10.0+Math.log(this.dataset.getClasses().length-1));
		}
		
		h.setGood(true);
		return true;
	}

	
	private void jiggleWeights(double beta) {
		double weightSum = 0.0;
		for(InstanceTriplet it : this.trainingSet.getInstances()){
				it.setWeight(((double)it.getWeight()+(randomNumber(beta))));
				weightSum += it.getWeight();
		}

		for(InstanceTriplet it : this.trainingSet.getInstances()){
			it.setWeight(((double)it.getWeight()/weightSum));
		}
	}
	
	private boolean samme(Hypothesis h){
		double error = 0.0;
		for(InstanceTriplet it : this.trainingSet.getInstances()){
			if (it.getInstance().get(it.getInstance().size()-1).intValue() != it.getClassification()){
				//System.out.println(it.getInstance().get(it.getInstance().size()-1).intValue()+"-"+it.getClassification());
				error += it.getWeight();
			}
		}
		
		h.setError(error);
		
		double logError = (1.0-h.getError())/h.getError();
		double logClasses = this.dataset.getClasses().length-1;

		h.setWeight(Math.log(logError)+Math.log(logClasses));
				
		double weightsum = 0.0;
		for(InstanceTriplet it : this.trainingSet.getInstances()){
			if (it.getInstance().get(it.getInstance().size()-1).intValue() != it.getClassification()){
				//System.out.println(it.getInstance().get(it.getInstance().size()-1).intValue()+"-"+it.getClassification());
				it.setWeight(it.getWeight()*Math.pow(Math.E, h.getWeight()));
			}
			weightsum += it.getWeight();
		}
		for(InstanceTriplet it : this.trainingSet.getInstances()){
			it.setWeight(it.getWeight()/weightsum);
		}

		return true;
		
	}

	private double randomNumber(double beta){
		double min = -(1/Math.pow(this.trainingSet.getInstances().size(), beta));
		double max = (1/Math.pow(this.trainingSet.getInstances().size(), beta));
		return min + (Math.random() * ((max-min) + 1.0));
	}
	
	public ArrayList<Hypothesis> getHypothesis() {
		return hypothesis;
	}

	public void setHypothesis(ArrayList<Hypothesis> hypothesis) {
		this.hypothesis = hypothesis;
	}

	public void readFromFile(String file, boolean prepData) throws IOException{
		
		File f = new File(file);

        FileReader fReader = new FileReader(f);
        BufferedReader reader = new BufferedReader(fReader);
        ArrayList<InstanceTriplet> set = new ArrayList<InstanceTriplet>();
        while(true){
        	if(!reader.ready()) {
        		break;
        	}
        	ArrayList<Double> inst = new ArrayList<Double>();
        	String[] s = reader.readLine().split(",");
        	for(int i = 0; i < s.length ; i++){
        		double tall = Double.parseDouble(s[i]);
        		inst.add(tall);
        	}
        	InstanceTriplet instance = new InstanceTriplet(inst);
        	set.add(instance);
        }
        int counter = set.size();

        ArrayList<InstanceTriplet> random = new ArrayList<InstanceTriplet>();
        while(counter > 0){
        	random.add(set.remove(((int)(Math.random()*counter))));
        	
        	counter = set.size();
        }

        reader.close();
        this.dataset = new DataSet(random, prepData);
	}
	
	public void devideDataset(DataSet data, double ratio){
		this.getTestSet().getInstances().clear();
		this.getTestSet().getInstances().addAll(data.getInstances().subList(0, (int) (data.getInstances().size()*ratio)));
		
		this.getTrainingSet().getInstances().clear();
		this.getTrainingSet().getInstances().addAll(data.getInstances().subList((int) (data.getInstances().size()*ratio), data.getInstances().size()));
	}
	
	public DataSet getTestSet() {
		return testSet;
	}

	public void setTestSet(DataSet testSet) {
		this.testSet = testSet;
	}

	public DataSet getTrainingSet() {
		return trainingSet;
	}

	public void setTrainingSet(DataSet trainingSet) {
		this.trainingSet = trainingSet;
	}

	public DataSet getDataset() {
		return dataset;
	}
	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}
	
	public ArrayList<Integer> getClassifications() {
		return classifications;
	}

	public void setClassifications(ArrayList<Integer> classifications) {
		this.classifications = classifications;
	}
	
	public static void main(String[] args) throws IOException{

		Adaboost ada = new Adaboost("pen-digits.txt", 1, 0, 0.2, 5, true, false);
		int correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}
		System.out.println("Ada1 - NBC's: 1, DTC's: 0, prep:True, samme:false");
		System.out.println((double)correct/ada.getTestSet().getInstances().size());		
		System.out.println();
		
		ada = new Adaboost("pen-digits.txt", 10, 0, 0.2, 5, true, false);
		correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}
		System.out.println("Ada1 - NBC's: 10, DTC's: 0, prep:True, samme:false");
		System.out.println((double)correct/ada.getTestSet().getInstances().size());	
		System.out.println();
		
		ada = new Adaboost("pen-digits.txt", 20, 0, 0.2, 5, true, false);
		correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}
		System.out.println("Ada1 - NBC's: 20, DTC's: 0, prep:True, samme:false");
		System.out.println((double)correct/ada.getTestSet().getInstances().size());	
		System.out.println();
		
		ada = new Adaboost("pen-digits.txt", 0, 1, 0.2, 5, true, false);
		correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}
		System.out.println("Ada1 - NBC's: 0, DTC's: 1, prep:True, samme:false");
		System.out.println((double)correct/ada.getTestSet().getInstances().size());	
		System.out.println();
		
		ada = new Adaboost("pen-digits.txt", 0, 10, 0.2, 5, true, false);
		correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}
		System.out.println("Ada1 - NBC's: 0, DTC's: 10, prep:True, samme:false");
		System.out.println((double)correct/ada.getTestSet().getInstances().size());	
		System.out.println();
		
		ada = new Adaboost("pen-digits.txt", 0, 20, 0.2, 5, true, false);
		correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}
		System.out.println("Ada1 - NBC's: 0, DTC's: 20, prep:True, samme:false");
		System.out.println((double)correct/ada.getTestSet().getInstances().size());	
		System.out.println();

		
		ada = new Adaboost("pen-digits.txt", 10, 10, 0.2, 5, true, false);
		correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}
		System.out.println("Ada1 - NBC's: 10, DTC's: 10, prep:True, samme:false");
		System.out.println((double)correct/ada.getTestSet().getInstances().size());	
		System.out.println();
		
		ada = new Adaboost("pen-digits.txt", 20, 20, 0.2, 5, true, false);
		correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}
		System.out.println("Ada1 - NBC's: 20, DTC's: 20, prep:True, samme:false");
		System.out.println((double)correct/ada.getTestSet().getInstances().size());	
		System.out.println();
		
	}

	
	
}
