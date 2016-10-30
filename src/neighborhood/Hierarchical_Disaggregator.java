 
//
// _HIERARCHICAL_DISAGGREGATOR_
//
//  --> to use pecan street data for the construction of a generalized model for disaggregating energy data 
//  
// _MAC FINNIE 2015 
//

import weka.classifiers.*;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation.*;

import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;

import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.Filter;

import weka.core.converters.ConverterUtils;
import weka.core.converters.ArffSaver;
import weka.core.*;
import java.io.*;
import java.util.*;

/*
  
 * Generates a network of Classifiers in order to disaggregate energy data instances

 */
public class Hierarchical_Disaggregator {

    // to_do: protected String LOG_FILE = "log.txt";

    protected String TRAINING_FILE;  // training filename 

    protected String PRUNING_LEVEL = ".5";          // pruning rate for j48 trees

    // to_do: boolean WEIGHT_PRUNING

    protected Dataset  master_training;    // (initial) training set 

    protected Dataset  master_test;

    protected Instances master_header;            // training file header (w/o class att)

    protected FastVector master_attributes; // to_do: all attributes excluding class

    protected Attribute master_class;             // the aggregate class attribute

    protected Node root;                          // Network root node  
    
    protected Vector<Node> nodes;                 // all nodes in the network

    protected Vector<Vector<Node>> layers;        // * organized by layer 

    protected Vector<Evaluation> evaluations;     // node evaluations

    protected double[] global_distribution;       // global distribution for an instance

    /** 
     *  
     *   ( follow WEKA code and process no data here)
     *
     **/
    public Hierarchical_Disaggregator(){
	
	// node container
	nodes = new Vector<Node>();              

	// layer organization
	layers = new Vector<Vector<Node>>();     

    }

    /**
     * Construct the main datasets from a given .arff file 
     * @param : title of .arff file for training 
     * 
     **/
    public void setData(String master_file) throws Exception{

	// Link the file to its location in Pecan/data/
	String master_filename = "/Users/gmfinnie33/Documents/Williams_College/summer_15/Pecan/data/" +  master_file;

	// Get input file in instance format 
	BufferedReader reader = new BufferedReader(new FileReader(master_filename));
	Instances master_pool = new Instances(reader);
	reader.close();

	// Set class index 
	master_pool.setClassIndex(master_pool.numAttributes()-1);
	
	/*
	  Shuffle the Input Data   
	 */
	// _make Random
	Random r = new Random();
	
	// _make Randomize Filter (WEKA)
	Randomize shuffler = new Randomize();
	
	// _set Shuffler random seed
	String[] shuffler_ops = new String[1];
	shuffler_ops[0] = ""+r.nextInt();
	shuffler.setOptions(shuffler_ops);
	shuffler.setInputFormat(master_pool);

	// _use on input data
	master_pool = Filter.useFilter(master_pool, shuffler);
	
	/*
	  Divide Input into TEST and TRAIN :
	 */
	// _set Filter Options
	String[] filter_ops = new String[2];
	filter_ops[0] = "-P";
	filter_ops[1] = "30.0";
	
	//_make RemovePercentage Filter (WEKA)
	RemovePercentage master_sampler = new RemovePercentage(); 
	master_sampler.setOptions(filter_ops);
	master_sampler.setInputFormat(master_pool);
	
	// _make Training Instances
	Instances training_instances = Filter.useFilter(master_pool, master_sampler);
	
	// _save file
	ArffSaver saver = new ArffSaver();
	saver.setInstances(training_instances);
	saver.setFile(new File("./data_log/master_training.arff"));
	saver.setDestination(new File("./data_log/master_training.arff"));
	saver.writeBatch();
		      
	// _make Testing Instances
	master_sampler.setInvertSelection(true);        // take remainder through inverted removal
	master_sampler.setInputFormat(master_pool);     // reset input format
	Instances test_instances = Filter.useFilter(master_pool, master_sampler);
	
	//_save file
	saver.setInstances(test_instances);
	saver.setFile(new File("./datalog/master_test.arff"));
	saver.setDestination(new File("./data_log/master_test.arff"));
	saver.writeBatch();

	// Print Review 
	System.out.println( " --------------------------[ DATA PROFILE ]------------------------- \n" );
	System.out.println( " ------------< training set > -------" + "\n" + training_instances.attributeStats(master_pool.classIndex()).toString() );
	System.out.println( " ------------< testing set  > -------" + "\n" + test_instances.attributeStats(master_pool.classIndex()).toString() );

	
	// make instances into datasets
	master_training = new Dataset( training_instances );
	master_test = new Dataset( test_instances );
       
	// Extract a header - a schematic for constructing future datasets 
	// get header via a WEKA datasource object 
	ConverterUtils.DataSource extractor = new ConverterUtils.DataSource(master_pool);
       	master_header = extractor.getStructure(master_pool.classIndex());
    }
    
    /** 
     *   Generate a trained network 
     **/
    public void seedNetwork() throws Exception{

	// Construct root node.
	root = new Node(master_training);

	// add to nodes
	nodes.add(root);

	// add first layer 
	layers.add(0,new Vector<Node>(1));
	layers.elementAt(0).add(root);

	// train the root node / generate network
	root.train();                          

	// give a review of the structure 
	System.out.println(this.toStructureString());
    }

    /**
     *  Test the Network on a User-Given file
     **/
    public void testNetwork() throws Exception{

	// Get test file - NOW HARDCODED AS SAMPLE OF MASTER FILE
	/*	Scanner s = new Scanner(System.in);
	System.out.println("Enter test set (.arff): ");
	String test_file = s.next(); 

	// Make dataset from file
	Instances test_instances = new Instances(new BufferedReader( new FileReader (test_file)));

	// ... (assume last index is class)
	test_instances.setClassIndex(master_training.classIndex());
	Dataset test_set = new Dataset( test_instances );
	*/
	// Create the Evaluation and Distribution for testing statistics 
	Evaluation test_eval = new Evaluation(master_training);
	global_distribution = new double[master_training.numClasses()];

	// Test each instance:
	Enumeration<Instance> test_feed = master_test.enumerateInstances();
	while(test_feed.hasMoreElements()){
	    
	    // Instance to be classified
	    Instance t_instance = test_feed.nextElement();

	    // Generate Global class probability distribution 
	    root.testInstance(t_instance);
	    
	    // Give result to test evaluation to update summary and determine prediction 
	    updateEvaluation(test_eval,global_distribution,t_instance);
	    
	    // Clear previous testing round
	    clearTest();
	    
	}

	// Give review 
	System.out.println(this.toSummaryString(test_eval));
    }
   
    /**
     *  Update the testing evaluation. 
     *  to_do: node-wise evaluation pooling, post testing
     *  @param : Evaluation t_ev - the evaluation to update
     *  @param : double[] distribution - a computed distribution 
     *  @param : Instance t_ex - the test example
     **/
    private void updateEvaluation(Evaluation t_ev , double[] distribution, Instance t_ex) throws Exception{

	double prediction = t_ev.evaluateModelOnceAndRecordPrediction(distribution, t_ex);
	
	/* to_do: add a lifetime evaluation with the ability to define a time unit / 
	 perhaps as a number of test sets tested / and examine factors across test sets. 
	 ( test_set should also train so that the network evolves )
	 - consequence of adding an evaluation which evaluates different versions of the disaggregator 
	  those trained on different data ( or different time periods )
	  nodes vs. #correct or more generally to include statistics about the */
    }

    /**
     * Clear a testing round by resetting global_distribution 
     **/
    public void clearTest(){

	// Clear Instance Distribution
	global_distribution = new double[master_training.numClasses()];
    }

    /**
     * Return a string summary of a current test evaluation
     **/
    public String toSummaryString(Evaluation eval) throws Exception{

	StringBuffer review_buffer = new StringBuffer();	

	review_buffer.append("--------------------[TEST_SUMMARY]-----------------------\n");
	review_buffer.append(eval.toSummaryString());
	review_buffer.append(eval.toClassDetailsString());
	review_buffer.append(eval.toMatrixString());

	// TO_DO: append desired summary statistics here
	return review_buffer.toString();
    }

    /**
     * Return a summary of the structure by getting string profile of Nodes. 
     **/
    public String toStructureString() throws Exception{
	
	StringBuffer structure_buffer = new StringBuffer();
	structure_buffer.append("---------------------[STRUCTURE_REVIEW]-----------------------\n");
	for(Node n: nodes){
	    structure_buffer.append(n.toProfileString());
	}

	return structure_buffer.toString();
	
    }

    // INTERNAL_CLASSES // // // // // // // // // // // // // // 
  
    /**
     *
     *  CLASSIFICATION NODE : Classifier in network 
     *
     **/ 
    private class Node {

	public int NODE_ID;                // unique identifier 

	public int LAYER;                  // resident layer

	public Boolean leaf;            // location 

	public Classifier classifier;      // classifier 

	public Dataset training_set;             // training data

	public int num_classes;                  // number of classes 

	public double[] local_distribution;      // class probabilities for test instance 

	public double[][] training_con_matrix;   // confusion matrix

	public Vector<Node> children;            // all children nodes

	public Vector<Dataset> data_branches;    // data sets of potential children

	public double[] class_weights;           // node weights for distribution 
	
	public Evaluation local_evaluation;             // local evaluation

	/**
	 * Node Constructor 
	 **/  
	private Node(Dataset data) throws Exception{
	    
	    // set id (count of nodes at time of construction)
	    NODE_ID = nodes.size();
 
	    // set layer id ( layer count at time of construction )
	    LAYER = layers.size();

	    // set up classifier type 
	    classifier = new J48();

	    // set training data 
	    training_set = data;     
	    training_set.setClassIndex(training_set.numAttributes()-1);
	    
	    // get total number of classes
	    num_classes = training_set.numClasses();
	    
	    // initialize children node vector
	    children = new Vector<Node>(num_classes);
	   	  
	    // location
	    leaf = (num_classes<3);

	    // set class weights of current data set
	    // = to % local dataset
	    class_weights = this.generateWeights(training_set);
	    
	    // Evaluation of Node's testing
	    local_evaluation = new Evaluation(training_set);
	}

	/**
	 * Determine if node is a leaf node - ( has no children / 2 classes )
	 * @return : the leaf boolean determined on construction 
	 **/
	public boolean isLeaf(){
	    
	    return this.leaf; 

	}

	public boolean isRoot(){
	    return (this.LAYER==0);
	}

	/** 
	 *   Train Node classifier. Produce Confusion Matrix. Train children.  
	 **/  
	public void train() throws Exception{
	 
	    if( !this.isRoot() ){
		// Train on a uniform distribution on internal nodes 
		// 
		String[] local_sampler_options = new String[4];
		local_sampler_options[0] = "-Z";
		local_sampler_options[1] = "10000";
		local_sampler_options[2] = "-B";
		local_sampler_options[3] = "1";
		
		// Set local sampler options 
		Resample local_sampler = new Resample();
		local_sampler.setOptions(local_sampler_options);
		local_sampler.setInputFormat(training_set);
		
		// pre 
		System.out.println("---------------------------[ SET BALANCING ]--------------------------------");
		System.out.println(" Set Balancing For : [ NODE " + NODE_ID +" ]"); 
		System.out.println(" Classes [ pre-filter ]\n "+ training_set.attributeStats(training_set.classIndex()).toString());
		// balance
		training_set = new Dataset(Filter.useFilter(training_set,local_sampler));
		// post
		System.out.println(" Classes [ post-filter ]\n "+ training_set.attributeStats(training_set.classIndex()).toString());

	    } // Use given dataset distribution at root 


	    // make classifier options
	    String[] cl_options = {"-C",PRUNING_LEVEL};              
	    
	    // set classifier options
	    classifier.setOptions(cl_options);              
	    classifier.buildClassifier(this.training_set);

	    // get an Evaluation
	    Evaluation training_evaluation = new Evaluation(this.training_set); 

	    // find # folds possible up to 10
	    int folds = 10;
	    if( training_set.numInstances() < 10 ){
		
		folds = training_set.numInstances();
		
		//  stop at nodes with 1 instance 
		if( folds < 2 ){
		    return ;
		}
		
	    }
	    
	    // Cross Validate	    
	    training_evaluation.crossValidateModel( this.classifier, this.training_set, folds, training_set.getRandomNumberGenerator(1));
	    
	    // Generate datasets of potential children
	    // ... This is slightly confused. Need clearer statement of when to branch. 
	    if( !this.isLeaf() ){
	
		data_branches = this.training_set.generateBranches(training_evaluation); 
		
		// Check if data for children exists 
		if( data_branches.size() > 0 ){
		    
		    // Data branches exist:
		    // ... create & train child nodes 
		    bloom(data_branches);

		}else{

		    // no branches. at leaf via #branches=0
		    leaf = true;

		}

	    }else{

		// ... we are at a leaf via #classes=2 (already determined)
		//	System.out.println("*** [Leaf Reached]  <@> [" + this.training_set.relationName() + "]\n");

	    }
	}
	
	/** 
	 *
	 *  Generate child nodes from branch datasets
	 *  @param branches : datasets with which to create new nodes
	 **/
	public void bloom(Vector<Dataset> branches) throws Exception{

	    // layer for new nodes 
	    Vector<Node> children_layer;

	    // Check if that layer (of existing child nodes) exists
	    if(layers.size() < (LAYER+1)  ){ 

		// <DOES NOT EXIST>  
		// Determine size of layers
		int layer_size = num_classes;

		// Make new layer
		children_layer = new Vector<Node>(num_classes);

		// Add to layer structure
		layers.add(children_layer);

	    }else{

		// <DOES EXIST>
		// Get existing layer
		children_layer = layers.elementAt(LAYER);
	    }

	    // Visit each branch    
	    for( Dataset branch_set : branches ){ 
		if(branch_set.numClasses() > 1 ){	
		
		    Node node = new Node(branch_set);      // make new node with this data
		    
		    nodes.add( node );                      // add to master list 
	
		    children.add( node );                   // add to local list 
		
		    children_layer.add(node);               // add to layer list 

		    node.train();                           // train the node
		}
	    }
	}

	/** 
	 * <1.> Generate local probabilities --> <2.> Update global probabilities  
	 * <3.> Test on child specializing in predicted class, if available
	 */
	public void testInstance(Instance in_testing) throws Exception{
	    
	    //<CALIBRATE INSTANCE>
	    // ... copy and designate dataset 
	    Instance test = new Instance(in_testing);
	    test.setDataset(this.training_set);

	    // ... change class value to match in local class attribute
	    // find index
	    int class_index = (int) test.classValue();
	    // find string value
	    String instance_class = master_training.classAttribute().value(class_index);	    
	    // find match
	    double newClassValue = (double) this.training_set.classAttribute().indexOfValue(instance_class);
	    // set
	    test.setValue(this.training_set.classIndex(), newClassValue);

	    // <LOCAL PREDICTION>
	    // get confidence array
	    double[] local_distribution = this.classifier.distributionForInstance(test);


	    // <VARIABLES FOR GLOBAL UPDATE>
	    int lead_dex=0;     // current prediction
	    int glo_dex;	// index in global distributrion     
	    String class_val;   // class val as string 

	    // Update each class confidence to reflect weighting
	    for(int p=0 ; p<this.num_classes ; p++){

		//<CALIBRATE CLASS> 
		// title of class p
		class_val = this.training_set.classAttribute().value(p);
		
		// index in master list 
		glo_dex = master_training.classAttribute().indexOfValue(class_val);
				
		// update confidence for class p 
		global_distribution[glo_dex] += local_distribution[p]*class_weights[p];
		
		// issues when confidence grew larger than 1
		if( global_distribution[glo_dex] > 1 ){
		    global_distribution[glo_dex] = 1;
		}

		// update search for max probability [this node's prediction]
		if(local_distribution[p] > local_distribution[lead_dex]){
		
		    // new leading class index
		    lead_dex = p;

		}
	    } 
	    //<ROUTE TEST INSTANCE TO SPECIALIST>
	    // only continue if an internal node
	    if(!this.isLeaf()){

		// test and update global distribution at child specialist for current guess
		children.elementAt(lead_dex).testInstance(test);

	    }
	}

	/**
	 * Generate weight array for class prediction.
	 * @param  dataset determining weight 
	 * @return weights array for node
	 **/
	public double[] generateWeights(Dataset set){

	    // Array to return 
	    double[] c_weights = new double[this.num_classes];

	    // Array of class values for each instance in set
	    double[] c_values  = set.attributeToDoubleArray(set.classIndex());

	    // Total instances
	    int total = set.numInstances();

	    // index of a class in the class weight array
	    int c_dex;

	    // update each weight incrementally. += 1/total 
	    for( double value : c_values ){
		c_dex = (int) value;
		c_weights[c_dex] += (double) 1/total;
	    }

	    // Return the array of weight = (class_count)/(total_count)
	    return c_weights;

	}
	
	/**
	 * Return a string representation of the Node's testing [not implemented]
	 **/
	public String toProfileString(){

	    StringBuffer profile = new StringBuffer();

	    profile.append("\n----[ NODE " +NODE_ID+ " ]------< profile >---------------------------");
	    profile.append("\nID           : " + NODE_ID );
	    profile.append("\nLayer        : " + LAYER );
	    profile.append("\nLeaf         : " + leaf );
	    profile.append("\nDataset      : " + training_set.relationName());
	    profile.append("\n__Statistics : " + "\n__" + training_set.attributeStats(training_set.classIndex()).toString() );
	    profile.append("\n# Children   : " + children.size() );
	    profile.append("\n------------------< classifier >-------------------------");
	    profile.append("\nClassifier   : " + classifier.toString() );
	    profile.append("\nConfusion_Mx : " );
	    profile.append("\n__Train      : " + Arrays.deepToString(training_con_matrix));
	    //	    profile.append("\n__Test       : " + Arrays.deepToString(local_evaluation.confusionMatrix() ));     
	    profile.append("\n----------------------------------------------------------");
	    return profile.toString();
	}
    }

    /** 
     *  
     *  A VERSION OF INSTANCES WITH UNIQUE FILTERING METHODS / FIELDS
     *
     */
    private class Dataset extends Instances{

	// Count of subsets directly generated from this set 
	protected int subsets_generated;
	
	// Thresholds:
	// above which a class will be added to confusion group 
	int CONFUSION_THRESHOLD;

	// above which a confusion group will be considered a valid branch set 
	int BRANCHING_THRESHOLD;

	/**
	 * Dataset Constructor
	 */ 
	private Dataset( Instances instances){ 
	    
	    super( instances );     

	    subsets_generated = 0;      // set subset count

	    CONFUSION_THRESHOLD = 0;    // TO BE EXPERIMENTED WITH

	    BRANCHING_THRESHOLD = 0;    // TO BE EXPERIMENTED WITH

	}

	/** 
	 * Generate a dataset for all confusion pairs (BRANCHING_THRESHOLD)
	 * Each group is formed via analysis of a confusion matrix (CONFUSION_THRESHOLD)
	 *
	 * @param self_eval : an evaluation of a classifier on this data
	 * @return Vector<Dataset> : branch for each predicted class 
	 **/
	public Vector<Dataset> generateBranches(Evaluation self_eval) throws Exception{
	    
	    // Datasets to return
	    Vector<Dataset> data_branches = new Vector<Dataset>(this.numClasses());

	    // WEKA source code edited to give confusion matrix with 
	    //     cells containing corresponding instances
	    Vector<Vector<Instances>> data_matrix = self_eval.dataMatrix();
      
	    //<BRANCH ON EVERY PREDICTED CLASS P ... >
	    for(int p = 0 ; p < this.numClasses() ; p ++ ){
		
		// all cells predicted as p
		Vector<Instances> predicted_p = data_matrix.elementAt(p);

		// all cells we determine to be for branch p
		Vector<Instances> branch_cells = new Vector<Instances>(2);

		// current maximums 
		int indexOfMax = -1;
		int countOfMax = 0;

		//< ... AND MAXIMUM PREDICTED P, ACTUALLY CLASS A (A!=P)>
		for( int a = 0; a<this.numClasses(); a++){

		    // get data
		    Instances actual_a = predicted_p.elementAt(a);

		    // keep if current max & not true positive
		    if(actual_a.numInstances() >= countOfMax){
			if( a != p ){
			    // update false positive leader
			    indexOfMax = a;
			    countOfMax = actual_a.numInstances();
			}
		    }
		}

		//<CONSTRUCT BRANCH >
		// ... if satisfactory pair was found
		if(indexOfMax > -1){

		    // update subset count 
		    subsets_generated++;

		    // create class list 
		    int[] branch_classes = new int[]{p,indexOfMax};

		    // add instances to list 
		    branch_cells.add(predicted_p.elementAt(p));
		    branch_cells.add(predicted_p.elementAt(indexOfMax));

		    // merge cells into one dataset, add to data branches
		    data_branches.add(p,new Dataset(mergeCells(branch_classes,branch_cells)));

	       

		}else{

		    // No confusion group was found
		    System.out.println("ERROR NO CLASS PAIR FOR PREDICTED CLASS <:> " + p );
		}
	    }

	    //<RETURN BRANCH>
	    return data_branches;
	}
    

	/**
	 *
	 * Merge cells into a single Instances
	 * @param classes : class indices
	 * @param cells : all Instances cells to merge
	 * @return Instances : the aggregate
	 **/
	private Instances mergeCells(int[] classes, Vector<Instances> cells){

	    // <BRANCH SPECIFICS> 
	    String branch_ID = "Class_Sub_Set_# " + subsets_generated;
	    int capacity = this.numInstances();

	    // <ADD ATTRIBUTES>
	    // all but class from master header
	    FastVector branch_atts = new FastVector(master_header.numAttributes()); 
	    Attribute att_to_add;
	    for(int a =0; a < master_header.numAttributes()-1; a++){
		att_to_add = master_header.attribute(a);
		branch_atts.addElement(att_to_add);
	    }
	    // class 
	    FastVector branch_classValues = new FastVector(classes.length);
	    for(int c = 0; c < classes.length ; c++){
		branch_classValues.addElement(this.classAttribute().value(classes[c]));
	    }
	    Attribute branch_ClassAttribute = new Attribute(branch_ID,branch_classValues);
	    branch_atts.addElement(branch_ClassAttribute);

	    // <ADD INSTANCES>
	    Instances branch_instances = new Instances(branch_ID,branch_atts,capacity);
	    branch_instances.setClassIndex(branch_instances.numAttributes()-1);
	    //	    System.out.println("[Adding Instances to : " + branch_ID);
	    // Merge cells 
	    for( Instances c : cells ){

		// add instance-wise to the aggregate branch_instances
		Enumeration<Instance> branch_feed = c.enumerateInstances();
		while(branch_feed.hasMoreElements()){

		    // copy and calibrate instance to the aggregate set
		    Instance branch_instance = new Instance(branch_feed.nextElement());
		    branch_instance.setDataset(branch_instances);

		    // calibrate class value
		    int class_index = (int) branch_instance.classValue();
		    String instance_class = this.classAttribute().value(class_index); 
		    
		    //    System.out.println(" class : " + instance_class);
		    
		    double newClassValue = (double) branch_instances.classAttribute().indexOfValue(instance_class);
		    branch_instance.setValue(branch_instances.classIndex(), newClassValue);

		    // add to aggregate
		    branch_instances.add(branch_instance);
		    
		}
	    }

	    // return the final product		
	    return branch_instances;
	}
	
	public void calibrateInstance(Instance notFit){
	    //to_do: for fitting instance to dataset/node, proper location?
	}
    }
    
    /**
     *  Main
     */
    public static void main(String[] args) throws Exception{	

	// Get File 
	Scanner scanner = new Scanner( System.in );
	System.out.print( "please enter training file (.arff) :  ");
	String train_file = scanner.next();     

	// Log Header
	System.out.println( "\n\n <><><><><><><><><><><><<<_O_P_E_R_A_T_I_O_N L_O_G_>>><><><><><><><><><><><><><> \n");  

	// Build Disaggregator 
	Hierarchical_Disaggregator pecan;	      
	pecan = new Hierarchical_Disaggregator();

	// Run Disaggregator
	pecan.setData(train_file);         // set data
	pecan.seedNetwork();               // train network 
	pecan.testNetwork();               // test network

    }
}
