package adaboost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Adaboost {

	ArrayList<InstanceTriplet> dataset;
	
	public Adaboost(String dataSetFile, int NBCs, int DTCs, int trainingSetSize){
		this.dataset = new ArrayList<InstanceTriplet>();
		try {
			this.readFromFile(dataSetFile);
		} catch (IOException e) {

		}
	}
	

	

	
	public void readFromFile(String file) throws IOException{
		
		File f = new File(file);

        FileReader fReader = new FileReader(f);
        BufferedReader reader = new BufferedReader(fReader);
        
        while(true){
        	if(!reader.ready()) {
        		break;
        	}
        	ArrayList<Double> inst = new ArrayList<Double>();
        	String[] s = reader.readLine().split(",");
        	for(int i = 0; i < s.length ; i++){
        		inst.add(Double.parseDouble(s[i]));
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
		//Adaboost ada = new Adaboost("yeast.txt");
		
	}
	
}
