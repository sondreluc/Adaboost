package adaboost;

import java.util.ArrayList;

public class DataSet {
	
	private ArrayList<InstanceTriplet> instances;
	private int[][] attrNumberOfValues;
	private int[] classes;
	
	public DataSet(ArrayList<InstanceTriplet> dataset){
		this.instances = new ArrayList<InstanceTriplet>();
		this.instances.addAll(dataset);
		this.preprocessData();
	}
	
	public int[] getClasses() {
		return classes;
	}

	public void setClasses(int[] classes) {
		this.classes = classes;
	}

	public DataSet() {
		this.instances = new ArrayList<InstanceTriplet>();
		
	}

	public ArrayList<InstanceTriplet> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<InstanceTriplet> instances) {
		this.instances = instances;
	}

	public int[][] getAttrNumberOfValues() {
		return attrNumberOfValues;
	}

	public void setAttrNumberOfValues(int[][] attrNumberOfValues) {
		this.attrNumberOfValues = attrNumberOfValues;
	}
	
	public void preprocessData(){
		this.attrNumberOfValues = new int[instances.get(0).instance.size()-1][];
		for(int i = 0; i < instances.get(0).instance.size()-1 ; i++){
			double low = 999999999;
			double heigh = -99999999;
			
			for(InstanceTriplet it : this.instances){
				if(it.getInstance().get(i)> heigh){
					heigh = it.getInstance().get(i);
				}
				if(it.getInstance().get(i) < low){
					low = it.getInstance().get(i);
				}
			}

			double dif = heigh-low;
			double onePart = dif/10;
			ArrayList<Double> attributeList = new ArrayList<Double>();
			for(InstanceTriplet it : this.instances){
				if(low <= it.getInstance().get(i) && it.getInstance().get(i)< low+onePart){
					it.getInstance().set(i, (double)0);
					if(!attributeList.contains(0.0)){
						attributeList.add(0.0);
					}
				}
				else if(low+onePart <= it.getInstance().get(i) && it.getInstance().get(i) < low+onePart*2){
					it.getInstance().set(i, (double)1);
					if(!attributeList.contains(1.0)){
						attributeList.add(1.0);
					}
				}
				else if(low+onePart*2 <= it.getInstance().get(i) && it.getInstance().get(i) < low+onePart*3){
					it.getInstance().set(i, (double)2);
					if(!attributeList.contains(2.0)){
						attributeList.add(2.0);
					}
				}
				else if(low+onePart*3 <= it.getInstance().get(i) && it.getInstance().get(i) < low+onePart*4){
					it.getInstance().set(i, (double)3);
					if(!attributeList.contains(3.0)){
						attributeList.add(3.0);
					}
				}
				else if(low+onePart*4 <= it.getInstance().get(i) && it.getInstance().get(i) < low+onePart*5){
					it.getInstance().set(i, 4.0);
					if(!attributeList.contains(4.0)){
						attributeList.add(4.0);
					}
				}
				else if(low+onePart*5 <= it.getInstance().get(i) && it.getInstance().get(i) < low+onePart*6){
					it.getInstance().set(i, (double)5);
					if(!attributeList.contains(5.0)){
						attributeList.add(5.0);
					}
				}
				else if(low+onePart*6 <= it.getInstance().get(i) && it.getInstance().get(i) < low+onePart*7){
					it.getInstance().set(i, (double)6);
					if(!attributeList.contains(6.0)){
						attributeList.add(6.0);
					}
				}
				else if(low+onePart*7 <= it.getInstance().get(i) && it.getInstance().get(i) < low+onePart*8){
					it.getInstance().set(i, (double)7);
					if(!attributeList.contains(7.0)){
						attributeList.add(7.0);
					}
				}
				else if(low+onePart*8 <= it.getInstance().get(i) && it.getInstance().get(i) < low+onePart*9){
					it.getInstance().set(i, (double)8);
					if(!attributeList.contains(8.0)){
						attributeList.add(8.0);
					}
				}
				else{
					it.getInstance().set(i, (double)9);	
					if(!attributeList.contains(9.0)){
						attributeList.add(9.0);
					}
				}
			}
			this.attrNumberOfValues[i] = new int[attributeList.size()];
			for(int j = 0; j<attributeList.size(); j++){
				this.attrNumberOfValues[i][j] = attributeList.get(j).intValue();
			}
			
		}
		
		ArrayList<Integer> classesArray = new ArrayList<>();
		for(InstanceTriplet it : this.instances){

			if(!classesArray.contains(it.getInstance().get(it.getInstance().size()-1).intValue())){
				classesArray.add(it.instance.get(it.instance.size()-1).intValue());
			}
		}
		
		this.classes = new int[classesArray.size()];
		for(int i = 0; i<classesArray.size(); i++){
			this.classes[i] = classesArray.get(i);
		}
		
	}

}
