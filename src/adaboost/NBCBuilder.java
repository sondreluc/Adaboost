package adaboost;

import java.util.ArrayList;
import java.util.HashMap;

public class NBCBuilder {

	private DataSet trainingSet;
	private HashMap<Integer, Double> aPrioriClassProb;
	private HashMap<AttributeValueClassTriplet, Double> conditionalAttrProb;
 	
	public NBCBuilder(DataSet data){
		this.trainingSet = data;
	}
	
	public void train(){
		
		this.aPrioriClassProb = new HashMap<Integer, Double>();
		this.conditionalAttrProb = new HashMap<AttributeValueClassTriplet, Double>();

		for (int i = 0; i < this.trainingSet.getClasses().length; i++){
			int classification = this.trainingSet.getClasses()[i];
			int counter = 0;
			double nevner = 0.0;
			ArrayList<InstanceTriplet> temp = new ArrayList<InstanceTriplet>();
			
			for(InstanceTriplet it : this.trainingSet.getInstances()){
				if(it.instance.get(it.instance.size()-1) == classification){
					counter++;
					temp.add(it);
					nevner += it.weight;

				}
			}
			
			this.aPrioriClassProb.put(classification, (double)counter/this.trainingSet.getInstances().size());
			
			for(int j = 0; j < this.trainingSet.getAttrNumberOfValues().length; j++){
				int attr = j;
				for(int k = 0; k < this.trainingSet.getAttrNumberOfValues()[attr].length ; k++){
					int val = this.trainingSet.getAttrNumberOfValues()[attr][k];
					
					AttributeValueClassTriplet attrValClass = new AttributeValueClassTriplet(attr, (double)val, classification);

					double teller = 0.0;
					for(InstanceTriplet it : temp){
						if(it.instance.get(j) == attrValClass.value){
							teller += it.weight;
							
						}
					}
					this.conditionalAttrProb.put(attrValClass, (double)teller/nevner);
				}
			}
		}
	}
	
	public double testSet(DataSet set){
		int correct = 0;
		for(InstanceTriplet it : set.getInstances()){
			double max = 0.0;
			for(int i = 0; i < set.getClasses().length; i++){
				double hmap = 1.0;
				for(int attr = 0; attr < set.getAttrNumberOfValues().length; attr++){
					hmap = hmap*(double)conditionalAttrProb.get(new AttributeValueClassTriplet(attr, it.instance.get(attr), set.getClasses()[i]));
				}
				
				hmap = hmap*this.aPrioriClassProb.get(set.getClasses()[i]);
				if(max < hmap){
					it.setClassification(set.getClasses()[i]);
					max = hmap;
				}
			}
			if(it.getClassification() == it.instance.get(it.instance.size()-1).intValue()){
				correct++;
			}
		}
		
		return (double)correct/(double)set.getInstances().size();
	}
	
	public HashMap<Integer, Double> getaPrioriClassProb() {
		return aPrioriClassProb;
	}

	public void setaPrioriClassProb(HashMap<Integer, Double> aPrioriClassProb) {
		this.aPrioriClassProb = aPrioriClassProb;
	}

	public HashMap<AttributeValueClassTriplet, Double> getaPrioriAttrProb() {
		return conditionalAttrProb;
	}

	public void setaPrioriAttrProb(HashMap<AttributeValueClassTriplet, Double> aPrioriAttrProb) {
		this.conditionalAttrProb = aPrioriAttrProb;
	}
	
	public DataSet getTrainingSet() {
		return trainingSet;
	}
	public void setTrainingSet(DataSet dataSet) {
		this.trainingSet = dataSet;
	}

	
	
	public class AttributeValueClassTriplet{
		
		public int attribute;
		public double value;
		public int classification;
		
		public AttributeValueClassTriplet(int a, double v, int c){
			this.attribute = a;
			this.value = v;
			this.classification = c;
		}
		
		@Override
		public int hashCode(){
			return (this.attribute * (int)value) ^ classification;
		}
		
		@Override
		public boolean equals(Object obj){
			if(obj instanceof AttributeValueClassTriplet) {
				AttributeValueClassTriplet AttrValClass = (AttributeValueClassTriplet) obj;
				return (this.attribute == AttrValClass.attribute && this.value == AttrValClass.value && this.classification == AttrValClass.classification);
			}
			else
				return false;
		}

	}
}
