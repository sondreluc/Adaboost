package adaboost;



public class Hypothesis {

	
	public enum HypothesisType{NBC, DTC}
	
	private HypothesisType type;
	private DataSet dataSet;
	private DataSet trainingSet;
	private DataSet testSet;
	private double weight;
	private double error;
	private int DTCMaxDepth;
	private double ratio;
	
	public Hypothesis(DataSet data, HypothesisType type, double trainingRatio, int DTCMaxDepht){
		
		this.dataSet = data;
		this.testSet = new DataSet();
		this.trainingSet = new DataSet();
		this.type = type;
		this.error = 0.0;
		this.DTCMaxDepth = DTCMaxDepht;
		this.ratio = trainingRatio;
		
	}
	
	public double doClassification(){
		//double result;
		this.devideDataset(this.getDataset(), this.ratio);
		
		this.testSet.setAttrNumberOfValues(this.getDataset().getAttrNumberOfValues());
		this.trainingSet.setAttrNumberOfValues(this.getDataset().getAttrNumberOfValues());
		
		this.testSet.setClasses(this.getDataset().getClasses());
		this.trainingSet.setClasses(this.getDataset().getClasses());
		
		if(this.type == HypothesisType.NBC){
			NBCBuilder NBC = new NBCBuilder(trainingSet, testSet);
			NBC.train();
			System.out.println(NBC.test());
		}
		
		else if(this.type == HypothesisType.DTC){
			
		}
		return 0.0;
	}
	
	
	
	public void devideDataset(DataSet data, double ratio){
		this.getTrainingSet().getInstances().clear();
		this.getTrainingSet().getInstances().addAll(data.getInstances().subList(0, (int) (data.getInstances().size()*ratio)));
		
		this.getTestSet().getInstances().clear();
		this.getTestSet().getInstances().addAll(data.getInstances().subList((int) (data.getInstances().size()*ratio), data.getInstances().size()));
	}
	
	public DataSet getTrainingSet() {
		return trainingSet;
	}
	public void setTrainingSet(DataSet trainingSet) {
		this.trainingSet = trainingSet;
	}
	public DataSet getTestSet() {
		return testSet;
	}
	public void setTestSet(DataSet testSet) {
		this.testSet = testSet;
	}
	
	public DataSet getDataset() {
		return dataSet;
	}

	public void setDataset(DataSet dataset) {
		this.dataSet = dataset;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public void initilizeWeights(){
		for(InstanceTriplet it : this.dataSet.getInstances()){
			it.setWeight((double)1.0/this.dataSet.getInstances().size());
		}
	}

	public HypothesisType getType() {
		return type;
	}

	public void setType(HypothesisType type) {
		this.type = type;
	}

	public int getDTCMaxDepth() {
		return DTCMaxDepth;
	}

	public void setDTCMaxDepth(int dTCMaxDepth) {
		DTCMaxDepth = dTCMaxDepth;
	}
	
}
