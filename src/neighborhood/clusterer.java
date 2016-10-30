// program utilizing WEKA packages to cluster a data set 
// a dataset with cluster appended is returned
 
public class pecan_Clusterer{

    // input file 
    protected String filename;

    // constructor 
    public pecan_Clusterer(){

    }
    // get filename and begin clustering process
    public static void main( String[] args ){

	Scanner input = new Scanner(System.in);

	System.out.println("◊ Is your dataset in WEKA's .arff format (y/n) ?");
	String response = input.next();

	if( response == "y" ){

	    System.out.println(" > Please provide the .arff filename: ");
	    filename = input.next();

	}else if( response == "n" ){

	    System.out.println(" > Please provide the .csv filename: ");
	    filename = input.next();

	}else{

	    System.out.println( " > INPUT ERROR " );
	    System.exit(1);

	}

	
    }

}

/*
  extra code from when h_d implemented clustering:
  /*

      PREPARE DATA
      1. convert from .csv to .arff
      2. add cluster as class to instances
      3. construct Dataset objects from instances
    */
    public void setData( String csv_Train, String csv_Test) throws Exception{

	// CONVERT TO .ARFF:

       	convertToArff( csv_Train, TRAINING_FILE );

	convertToArff( csv_Test, TESTING_FILE );
	
	System.out.println( "[Data Converted]  <to>  [.arff]" );

	// MAKE INSTANCES:

	Instances training = new Instances( new BufferedReader( new FileReader(TRAINING_FILE)));

	Instances testing = new Instances( new BufferedReader( new FileReader(TESTING_FILE)));

	// cluster training & testing instances
	training = addClusterAsClass(training);   // add cluster using model	
	testing = addClusterAsClass(testing);     // *	

	// Form the master class list 
	Enumeration<String> cluster_feed = training.classAttribute().enumerateValues();

	while(cluster_feed.hasMoreElements()){
	    
	    master_classlist.add(cluster_feed.nextElement());

	}

	// make training set
	training_pool = new Dataset(training, master_classlist);
	training_pool.setRelationName("full_training_data");

	// make test set 
	test_pool = new Dataset(testing, master_classlist);
	training_pool.setRelationName("full_testing_data");

	System.out.println("[Clusters Added]");
    }

    /* 

       PERFORM .CSV --> .ARFF CONVERSION 

    */
    private void convertToArff( String csv_File, String arff_File ) throws Exception {
	
	// load CSV 
	CSVLoader loader = new CSVLoader();

	loader.setSource( new File(csv_File) );

	// format as instances
	Instances csv_Data = loader.getDataSet();
	
	// save ARFF 
	ArffSaver saver = new ArffSaver();

	saver.setInstances(csv_Data);

	// saved in 
	saver.setFile( new File( arff_File  ) ); 

	saver.setDestination( new File( arff_File ));

	saver.writeBatch();

    }

    /* 

       ADD CLUSTER TO A DATASET 

    */
    public Instances addClusterAsClass ( Instances i ) throws Exception{
	
	// get the unclustered instances 
	Instances unclustered = i;

	// set clusterer as filter option  
	String[] filter_options = new String[2];
	filter_options[0] = "-W";
	filter_options[1] = "weka.clusterers.SimpleKMeans -N 4";

	// cluster via an AddCluster filter 
	AddCluster filter = new AddCluster();

	// set options
	filter.setOptions(filter_options);

	// give input file
	filter.setInputFormat(unclustered);

	// generate clustered instances
	Instances clustered = Filter.useFilter(unclustered, filter);

	// set class index (cluster#)
	clustered.setClassIndex(clustered.numAttributes()-1);

	return clustered;
    }
    
 */
