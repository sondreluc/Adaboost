package adaboost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import adaboost.Hypothesis.HypothesisType;

public class Adaboost {

	public DataSet dataset;
	public DataSet testSet;
	public DataSet trainingSet;
	public ArrayList<Hypothesis> hypothesis;
	private ArrayList<Integer> classifications;
	
	public Adaboost(String datasetFile, int NBCs, int DTCs, double trainingSetSize, int DTCMaxDept, boolean prepData, boolean samme, boolean compare){


		this.testSet = new DataSet();
		this.trainingSet = new DataSet();

		try {
			this.readFromFile(datasetFile, prepData);
		} catch (IOException e) {

		}
		if(DTCMaxDept == -1){
			DTCMaxDept = this.dataset.getAttrNumberOfValues().length;
		}
		this.testSet = new DataSet();
		this.trainingSet = new DataSet();
		
		this.devideDataset(this.dataset, trainingSetSize);
		this.testSet.setAttrNumberOfValues(this.dataset.getAttrNumberOfValues());
		this.trainingSet.setAttrNumberOfValues(this.dataset.getAttrNumberOfValues());
		
		this.testSet.setClasses(this.dataset.getClasses());
		this.trainingSet.setClasses(this.dataset.getClasses());
		this.run(NBCs, DTCs, trainingSetSize, DTCMaxDept, prepData, samme);
		
		int error = 0;
		for (int i = 0; i< this.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = this.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() != this.getClassifications().get(i)){
				error++;
			}
		}

		
		System.out.println("Results of Adaboos run with: "+NBCs+" NBC's, "+DTCs+" DTC's(Maxdepth: "+(DTCMaxDept==this.getDataset().getAttrNumberOfValues().length ?"All" : DTCMaxDept)+") , Preprocessing of data: "+prepData+", SAMME:"+samme);
		System.out.println("Precentage of error in the boosted classification of the test set: "+ (((double)error/this.getTestSet().getInstances().size())*100.0) +"%");		
		System.out.println();
		
		if(compare){
			this.run(NBCs, DTCs, trainingSetSize, DTCMaxDept, prepData, !samme);
			error = 0;
			for (int i = 0; i< this.getTestSet().getInstances().size(); i++){
				InstanceTriplet it = this.getTestSet().getInstances().get(i);
				if(it.getInstance().get(it.getInstance().size()-1).intValue() != this.getClassifications().get(i)){
					error++;
				}
			}

			
			System.out.println("Results of Adaboos run with: "+NBCs+" NBC's, "+DTCs+" DTC's(Maxdepth: "+(DTCMaxDept==this.getDataset().getAttrNumberOfValues().length ?"All" : DTCMaxDept)+") , Preprocessing of data: "+prepData+", SAMME:"+!samme);
			System.out.println("Precentage of error in the boosted classification of the test set: "+ (((double)error/this.getTestSet().getInstances().size())*100.0) +"%");		
			System.out.println();
		}
	}
	
	public void initWeights(){
		for(InstanceTriplet it : this.trainingSet.getInstances()){
			it.setWeight((double)1.0/this.trainingSet.getInstances().size());
		}
	}
	
	public void run(int NBCs, int DTCs, double trainingSetSize, int DTCMaxDept, boolean prepData, boolean samme){
		this.classifications = new ArrayList<Integer>();
		this.hypothesis = new ArrayList<Hypothesis>();
		double start = System.currentTimeMillis();
		this.initWeights();
		
		ArrayList<Double> dtcTrainingResults = new ArrayList<Double>();
		ArrayList<Double> nbcTrainingResults = new ArrayList<Double>();
		
		double dtcMean = 0.0;
		int badDTCs = 0;
		
		for(int i = 0; i < DTCs; i++){
			while(true){
				Hypothesis h = new Hypothesis(HypothesisType.DTC, DTCMaxDept,this.testSet, this.trainingSet);
				double result = h.doClassification();
				if((samme ? samme(h) : updateWeights(h))){
					dtcMean += result;
					dtcTrainingResults.add(result);
					hypothesis.add(h);
					break;
				}
				badDTCs++;
			}
		}
		
		double nbcMean = 0.0;
		int badNBCs = 0;
		for(int i = 0; i < NBCs ; i++){
			while(true){
				Hypothesis h = new Hypothesis(HypothesisType.NBC, DTCMaxDept,this.testSet, this.trainingSet);
				double result = h.doClassification();
				if((samme ? samme(h) : updateWeights(h))){
					nbcMean += result;
					nbcTrainingResults.add(result);
					hypothesis.add(h);
					break;
				}
				badNBCs++;
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
		System.out.println("Run time in milliseconds "+(System.currentTimeMillis()-start));
		
		if(badDTCs>0){
			System.out.println("There were "+badDTCs+" bad DTC's");
		}
		if(badNBCs>0){
			System.out.println("There were "+badNBCs+" bad NBC's");
		}

		dtcMean = dtcMean/dtcTrainingResults.size();
		nbcMean = nbcMean/nbcTrainingResults.size();
		
		double dtcSD = 0.0;
		for(Double d : dtcTrainingResults){
			dtcSD += Math.pow((d-dtcMean),2);
		}
		dtcSD = dtcSD/dtcTrainingResults.size();
		System.out.println();
		System.out.println("DTC average error: "+ dtcMean);
		System.out.println("DTC standard deviation: "+ Math.pow(dtcSD, 0.5));
		
		
		double nbcSD = 0.0;
		for(Double d : nbcTrainingResults){
			nbcSD += Math.pow((d-nbcMean),2);
		}
		nbcSD = nbcSD/nbcTrainingResults.size();
		System.out.println();

		System.out.println("NBC average error: "+ nbcMean/nbcTrainingResults.size());
		System.out.println("NBC standard deviation: "+ Math.pow(nbcSD, 0.5));
		System.out.println();
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
	
	public static void consoleInterface(Scanner scanner){
		String file = "";
		int nbcs = 0;
		int dtcs = 0;
		int depth = 1;
		double ratio = 0.5;
		boolean prepProcess = false;
		boolean samme = false;
		
		System.out.println("Welcome to this Adaboost implementation by Sondre and Øystein");
		System.out.println("This uses a user defined number of NBC's and DTC's for boosted classification");
		System.out.println("Please input the dataset to use(String value, only name).");
		while (scanner.hasNext()) {
			String in = scanner.next()+".txt";
			File f = new File(in);
			if(f.exists()){
				file = in;
				break;
			}
			System.out.println("File not found. Please try again.");
		}
		System.out.println("Please input the number of NBC's to use:");
		while (scanner.hasNextInt()) {
			int number = scanner.nextInt();
			if(0<=number){
				nbcs = number;
				break;
			}
			System.out.println("Number of NBC's can not be negative. Please try again.");
		}
		System.out.println("Please input the number of DTC's to use:");
		while (scanner.hasNextInt()) {
			int number = scanner.nextInt();
			if(0<=number){
				dtcs = number;
				break;
			}
			System.out.println("Number of NBC's can not be negative. Please try again.");
		}
		if(0<dtcs){
			System.out.println("Please input the max depth for the DTC's to use:(int value or A for maximum depth)");
		}
		while (scanner.hasNext() && dtcs>0) {
			String s = scanner.next();
			if(s.equalsIgnoreCase("A")){
				depth = -1;
				break;
			}
			try{
				int tall = Integer.parseInt(s);
				
				if(0<tall){
					depth =tall;
					break;
				}
				System.out.println("Invalid input for depth. Please try again.");
			}
			catch(NumberFormatException e){
				System.out.println("Invalid input for depth. Please try again.");
			}
		}
		System.out.println("Please input the procentage of the dataset to test on:(minimum 0.1, maximum 0.9)");
		while (scanner.hasNext()) {
			double number = Double.parseDouble(scanner.next());
			if(0.1 <= number && number <= 0.9){
				ratio = number;
				break;
			}
			System.out.println("The procentage must be between 0.1 and 0.9! Please try again.");
		}
		System.out.println("Do you want to preprocess your dataset? Type Yes/No");
		while (scanner.hasNext()) {
			
			String prepp = scanner.next();
			if(prepp.equalsIgnoreCase("YES") ||prepp.equalsIgnoreCase("Y")){
				prepProcess = true;
				break;
			}
			else if(prepp.equalsIgnoreCase("NO")|| prepp.equalsIgnoreCase("n")){
				prepProcess = false;
				break;
			}
			System.out.println("The value you entered is invalid. Please try again.");
		}
		System.out.println("Do you want to use the SAMME-algorithm for weight updating(default is UPDATEWEIGHTS)? Type Yes/No");
		while (scanner.hasNext()) {
			String sam = scanner.next();
			if(sam.equalsIgnoreCase("YES")|| sam.equalsIgnoreCase("Y")){
				samme = true;
				break;
			}
			else if(sam.equalsIgnoreCase("NO")|| sam.equalsIgnoreCase("N")){
				samme = false;
				break;
			}
			System.out.println("The value you entered is invalid. Please try again.");
		}
		
		System.out.println("Starting Adaboost!");
		Adaboost ada = new Adaboost(file, nbcs, dtcs, ratio, depth, prepProcess, samme, false);
		
		int correct = 0;
		for (int i = 0; i< ada.getTestSet().getInstances().size(); i++){
			InstanceTriplet it = ada.getTestSet().getInstances().get(i);
			if(it.getInstance().get(it.getInstance().size()-1).intValue() == ada.getClassifications().get(i)){
				correct++;
			}
		}

		
		System.out.println("Results of Adaboos run with: "+nbcs+" NBC's, "+dtcs+" DTC's(Maxdepth: "+(depth>0 ? depth : "All")+") , Preprocessing of data: "+prepProcess+", SAMME:"+samme);
		System.out.println("% Correct classified: "+ (((double)correct/ada.getTestSet().getInstances().size())*100.0) +"%");		
		System.out.println();
		
		System.out.println();
		
	}
	
	public static void main(String[] args) throws IOException{
		//consoleInterface(new Scanner(System.in));
		
		String file = "nursery.txt";
		int nbcs = 20;
		int dtcs = 20;
		int depth = 2;
		double ratio = 0.2;
		boolean prepProcess =false;
		boolean samme = false;
		
		System.out.println("Starting Adaboost!");
		Adaboost ada = new Adaboost(file, nbcs, dtcs, ratio, depth, prepProcess, samme, true);

	}
	
	
}
