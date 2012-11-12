package adaboost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import adaboost.Hypothesis.HypothesisType;

public class Adaboost {

	public DataSet dataset;
	public ArrayList<Hypothesis> hypothesis;
	
	public Adaboost(String datasetFile, int NBCs, int DTCs, double trainingSetSize, int DTCMaxDept){
		this.hypothesis = new ArrayList<Hypothesis>();
		try {
			this.readFromFile(datasetFile);
		} catch (IOException e) {

		}
		this.initWeights();
		
		for(int i = 0; i < NBCs ; i++){
			
			Hypothesis h = new Hypothesis(this.getDataset(), HypothesisType.NBC, trainingSetSize, DTCMaxDept);
			h.doClassification();
			updateWeights(h);
			this.getHypothesis().add(h);
		}
//		for(int i = 0; i < DTCs; i++){
//			Hypothesis h = new Hypothesis(this.getDataset(), HypothesisType.DTC, trainingSetSize, DTCMaxDept);
//			//Do classification here?
//			this.getHypothesis().add(h);
//		}
		
	}
	
	public void initWeights(){
		for(InstanceTriplet it : this.dataset.getInstances()){
			it.setWeight((double)1.0/this.dataset.getInstances().size());
		}
	}
	

	public void updateWeights(Hypothesis h){
		double weightSum = 0.0;

		for(InstanceTriplet it : this.dataset.getInstances()){
			if (it.getInstance().get(it.getInstance().size()-1).intValue() != it.getClassification()){
				h.setError(h.getError()+ it.getWeight());

			}
			else{
				it.setWeight((double)h.getError()/(1.0-h.getError()));

			}
			weightSum += it.getWeight();

		}

		for(InstanceTriplet it : this.dataset.getInstances()){
			it.setWeight((double)it.getWeight()/weightSum);
		}
		
		h.setWeight(Math.log((double)(1-h.getError())/h.getError()));

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
        
        reader.close();
        this.dataset = new DataSet(set);
	}
	

	
	public DataSet getDataset() {
		return dataset;
	}
	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}
	public static void main(String[] args) throws IOException{

		Adaboost ada = new Adaboost("yeast.txt", 5, 0, 0.3, 0);

	}
	
}
