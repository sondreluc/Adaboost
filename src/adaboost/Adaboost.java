package adaboost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import adaboost.Hypothesis.HypothesisType;

public class Adaboost {

	ArrayList<InstanceTriplet> dataset;
	ArrayList<Hypothesis> hypothesis;
	
	public Adaboost(String datasetFile, int NBCs, int DTCs, double trainingSetSize, int DTCMaxDept){
		this.dataset = new ArrayList<InstanceTriplet>();
		this.hypothesis = new ArrayList<Hypothesis>();
		try {
			this.readFromFile(datasetFile);
		} catch (IOException e) {

		}
		
		
		for(int i = 0; i < NBCs ; i++){
			
			Hypothesis h = new Hypothesis(this.getDataset(), HypothesisType.NBC, trainingSetSize, DTCMaxDept);
			//Do classification here?
			this.getHypothesis().add(h);
		}
		for(int i = 0; i < DTCs; i++){
			Hypothesis h = new Hypothesis(this.getDataset(), HypothesisType.DTC, trainingSetSize, DTCMaxDept);
			//Do classification here?
			this.getHypothesis().add(h);
		}
		
	}
	
	

	

	
	public ArrayList<Hypothesis> getHypothesis() {
		return hypothesis;
	}

	public void setHypothesis(ArrayList<Hypothesis> hypothesis) {
		this.hypothesis = hypothesis;
	}

	public void readFromFile(String file) throws IOException{
		
		File f = new File(file);

        FileReader fReader = new FileReader(f);
        BufferedReader reader = new BufferedReader(fReader);
        
        while(true){
        	if(!reader.ready()) {
        		break;
        	}
        	ArrayList<Integer> inst = new ArrayList<Integer>();
        	String[] s = reader.readLine().split(",");
        	for(int i = 0; i < s.length ; i++){
        		inst.add(Integer.parseInt(s[i]));
        	}
        	InstanceTriplet instance = new InstanceTriplet(inst);
        	this.dataset.add(instance);
        }
        
        reader.close();
	}
	

	
	public ArrayList<InstanceTriplet> getDataset() {
		return dataset;
	}
	public void setDataset(ArrayList<InstanceTriplet> dataset) {
		this.dataset = dataset;
	}
	public static void main(String[] args) throws IOException{
		//Adaboost ada = new Adaboost("yeast.txt", int NBCs, int DTCs, int trainingSetSize, int DTCMaxDept);
		
	}
	
}
