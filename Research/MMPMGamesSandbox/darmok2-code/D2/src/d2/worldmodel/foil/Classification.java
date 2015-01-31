/* Copyright 2010 Santiago Ontanon and Ashwin Ram */

package d2.worldmodel.foil;

/**
 * Methods to produce classification rules using FOIL.
 * 
 * Assumes that input data set is organized such that classes are at the end of each record.
 * 
 * Note: number of classes value is stored in the numClasses field.
 * @author Frans Coenen, modified for D2 use by Katie Long
 */
public class Classification extends AssocRuleMining {

	/**
	 * 2-D array to hold the test data set. Classification involves producing a set of Classification 
	 * Rules (CRs) from a training set and then testing the effectiveness of the CRs on a test set.
	 */
	protected short[][] testDataArray = null;

	/**
	 * 3-D array to hold 10th sets of input data. Used in conjunction with "10 Cross Validation" where the 
	 * input data is divided into 10 subsets and CRs are produced using each subset in turn and validated 
	 * against the remaining 9 sets. The overall average accuracy is then the total accuracy divided by 10.
	 */
	protected short[][][] tenthDataSets = new short[10][][];

	/**
	 * 1-D data array to hold classifiers.
	 */
	protected short[] classifiers = null; 

	/**
	 * 2-D attribute array, first row contains '0.0' or '1.0' (0.0=available, 1.0=unavailable), second row 
	 * contains gain.
	 */
	protected double[][] attributes = null;

	/**
	 * Temporary 2-D attribute array.
	 */
	protected double[][] attributes2 = null;


	/**
	 * The maximum number of rules to be considered per class when classifying records during testing.
	 * Initially set to 5.
	 */
	protected final int K_VALUE = 5;

	/**
	 * The minimum best gain acceptable for the process of the generation of a single rule to continue.
	 */
	protected final double MIN_BEST_GAIN=0.7;


	/**
	 * Number of rows in input data set.  Note this is not the same as the number of rows in the 
	 * classification training set. Used for temporary storage of total number of rows when using 
	 * Ten Cross Validation (TCV) approach only. The numRows field inherited from the super class
	 * records is used throughout the CR generation process. Set to number of rows using setNumRowsInInputSet 
	 * method called by application class. */
	protected int numRowsInInputSet;

	/**
	 * Number of rows in test set, not the same as the number of rows in the classification training set.
	 */
	protected int numRowsInTestSet;

	/**
	 * Number of rows in training set, not the same as the number of rows in the classification test set.
	 */
	protected int numRowsInTrainingSet;

	/**
	 * Instance of the class RuleList
	 */
	protected RuleList currentRlist = null;


	/**
	 * Constructor for Classification class which also processes command line arguments.
	 * @param args the command line arguments (array of String instances).
	 * */
	public Classification() {
		super();

		// Create RuleList object for later usage
		currentRlist = new RuleList();
	}


	/**
	 * Starts classification rule generation process.
	 * @return The classification accuracy (%).
	 */
	public double startClassification() {

		/* STUB */

		return(0.0);   	// Default
	}


	/**
	 * Process attributes array to identify the best gain
	 * @return the index of the best attribute, if no available attributes or will return 0.
	 */
	protected int getBestGain() {
		int indexOfBestAttribute = 0; 			
		double bestGainSofar = 0.0;	

		// Loop through attributes2 array
		for (int index=1;index<attributes2.length;index++) {
			// If attribute not part of existing rule check gain
			if ((int) attributes2[index][0] == 0) {
				if (attributes2[index][1]>bestGainSofar) {
					indexOfBestAttribute = index; 		
					bestGainSofar = attributes2[index][1];
				}
			}
		}
		return(indexOfBestAttribute);
	}


	/**
	 * Tests the generated classification rules using test sets and return percentage accuracy.
	 * @param the percentage accuracy.
	 */
	protected double testClassification() {	
		int correctClassCounter = 0;
		int wrongClassCounter   = 0;	
		int unclassifiedCounter = 0;

		// Check if test data exists, if not return' 0'
		if (testDataArray==null) {
			System.out.println("ERROR: No test data");
			return(0);
		}

		// Check if any classification rules have been generated, if not
		// return'0'
		if (currentRlist.startRulelist==null) {
			System.out.println("No classification rules generated!");
			return(0);
		}

		// Loop through test set
		int index=0;
		for(;index<testDataArray.length;index++) {
			// Note: classifyRecord methods are contained in the
			// AssocRuleMining class. 
			short classResult = currentRlist.classifyRecordBestKaverage(K_VALUE,classifiers, testDataArray[index]);
			if (classResult==0) unclassifiedCounter++;
			else {
				short classActual = getLastElement(testDataArray[index]);
				if (classResult == classActual) correctClassCounter++;
				else wrongClassCounter++;
			}
		}

		// Calculate classification accuracy
		double accuracy = (correctClassCounter*100.0/index);
		return(accuracy);				
	}


	/**
	 * Start Ten Cross Validation (TCV) process using a 9/10 training/test set split.
	 * @return overall accuracy (%).
	 */
	public double commenceTCV() {
		double[] parameters = new double[10];

		// Loop through tenths data sets
		for (int index=0;index<10;index++) {
//			System.out.println("[--- " + index + " ---]");
			// Create training and test sets
			createTrainingAndTestDataSets(index);
			// Mine data, produce T-tree and generate CRs
			parameters[index] = startClassification();
		}

		// Determine overall accuracy
		double totalAccu = 0;
		for (int index=0;index<parameters.length;index++) {
			totalAccu = totalAccu + parameters[index];
		}
		return(totalAccu/10.0);
	}


	/**
	 * Start Ten Cross Validation (TCV) process with output of individual accuracies.
     */
	public void commenceTCVwithOutput() {
		double[][] parameters = new double[10][2];

		// Loop through tenths data sets
		for (int index=0;index<10;index++) {
			System.out.println("[--- " + index + " ---]");
			// Create training and test sets
			createTrainingAndTestDataSets(index);
			// Mine data, produce T-tree and generate CRs
			parameters[index][0] = startClassification();
			parameters[index][1] = currentRlist.getNumCRs();
		}

		// Determine totals
		double totalAccu        = 0;
		double totalNumCRs      = 0;
		for (int index=0;index<parameters.length;index++) {
			System.out.println("(" + (index+1) + ") Accuracy = " + twoDecPlaces(parameters[index][0]) + ", Num CRs = " + ((int) parameters[index][1]));	
			totalAccu        = totalAccu+parameters[index][0];
			totalNumCRs      = totalNumCRs+parameters[index][1];
		}
		System.out.println("---------------------------------------");
		System.out.println("Average Accuracy = " + twoDecPlaces(totalAccu/10) + ", Average Num CRs = " + twoDecPlaces(totalNumCRs/10));
	}


	/**
	 * Commences process of getting input data. <P> reads and checks file, and if of correct 
	 * format then also sets number of rows in input set field and creates array of classes.
	 */
	public void inputDataSet() {
		if (checkOrdering()) {
			// Count number of columns
			countNumCols();
		}
		else {
			System.out.println("Ordering is incorrect!\n");
			System.exit(1);
		}
	}


	/**
	 * Populates training and test data sets using a 50:50 training/test set split. Note: 
	 * (1) training data set is stored in the dataArray structure in which the input data was 
	 * originally stored, (2) method called from application class not from Classification class 
	 * constructor because the input data set may (given a particular application) first require 
	 * ordering and possibly also pruning and recasting.
	 */
	public void createTrainingAndTestDataSets() {
		// Determine size of training and test sets.
		final double PERCENTAGE_SIZE_OF_TEST_SET = 50.0;
		numRowsInTestSet     = (int) (numRows*PERCENTAGE_SIZE_OF_TEST_SET/100.0);
		numRowsInTrainingSet = numRows-numRowsInTestSet;
		numRows              = numRowsInTrainingSet;

		// Output
//		System.out.println("Num. of recs. in training set = " + numRowsInTrainingSet + 
//				"\nNum. of recs. " + "in test set     = " + numRowsInTestSet);

		// Dimension and populate training set. 
		short[][] trainingSet = new short[numRowsInTrainingSet][];
		int index1=0;
		for (;index1<numRowsInTrainingSet;index1++) 
			trainingSet[index1] = dataArray[index1];

		// Dimension and populate test set
		testDataArray = new short[numRowsInTestSet][];
		for (int index2=0;index1<dataArray.length;index1++,index2++) 
			testDataArray[index2] = dataArray[index1];

		// Assign training set label to input data set label.
		dataArray = trainingSet;   
	}

	
	/**
	 * Populates training and test data sets using a 9:1 training/test set split.  Note: 
	 * (1) using TCV (Ten Cross Validation), (2) training data set is stored in the dataArray 
	 * structure in which the input data was originally stored, in which the initial input data 
	 * is stored, (3) this method is not called from the constructor as the input data set may 
	 * (given a particular application) first require ordering and possibly also pruning.
	 * @param testSetIndex the index of the tenths data sets to be used as the test set.
	 */
	private void createTrainingAndTestDataSets(int testSetIndex) {
		// Dimension and populate test set.
		numRowsInTestSet = tenthDataSets[testSetIndex].length;
		testDataArray    = tenthDataSets[testSetIndex];

		// Dimension of and populate training set.
		numRowsInTrainingSet = numRowsInInputSet-numRowsInTestSet;
		numRows              = numRowsInTrainingSet;
		short[][] trainingSet = new short[numRows][];
		int trainingSetIndex=0;	
		// Before test set
		for(int index=0;index<testSetIndex;index++) {
			for (int tenthsIndex=0;tenthsIndex<tenthDataSets[index].length;
			tenthsIndex++,trainingSetIndex++) {
				trainingSet[trainingSetIndex] = 
					tenthDataSets[index][tenthsIndex];
			}
		}    	
		// After test set
		for(int index=testSetIndex+1;index<tenthDataSets.length;index++) {
			for (int tenthsIndex=0;tenthsIndex<tenthDataSets[index].length;
			tenthsIndex++,trainingSetIndex++) {
				trainingSet[trainingSetIndex] = 
					tenthDataSets[index][tenthsIndex];
			}
		}

		// Output
//		System.out.println("Num. of recs. in training set = " + 
//				numRows + "\nNum. of recs. " +
//				"in test set     = " + numRowsInTestSet);

		// Assign training set label to input data set label.
		dataArray = trainingSet;   
	}


	/**
	 * Populates ten tenths data sets for use when doing Ten Cross Validation (TCV) --- test and 
	 * training data sets. Note: this method is not called from the constructor as the input data 
	 * set may (given a particular application) first require ordering (and possibly also pruning!).
	 */
	public void createTenthsDataSets() {

		// If number of rows is less than 10 cannot create appropriate data 
		// sets
		if (numRows<10) {
			System.out.println("ERROR: only " + numRows + ", therefore cannot create tenths data sets!");
			System.exit(1);
		}

		// Determine size of first nine tenths data sets.
		int tenthSize = numRows/10;

		// Dimension first nine tenths data sets.
		int index=0;
		for( ;index<tenthDataSets.length-1;index++)
			tenthDataSets[index] = new short[tenthSize][];
		// Dimension last tenths data set
		tenthDataSets[index] = new short[numRows-(tenthSize*9)][];	

		// Populate tenth data sets
		int inputDataIndex=0;
		for(index=0;index<tenthDataSets.length;index++) {
			for(int tenthIndex=0;tenthIndex<tenthDataSets[index].length; tenthIndex++,inputDataIndex++) {
				tenthDataSets[index][tenthIndex] = dataArray[inputDataIndex];
			}
		}
	}

	/**
	 * Processes rule linked list to remove duplicates. It is possible, using PRM or CPAR, to generate 
	 * two or more rules with identical antecedents and consequents. This is because records are not 
	 * removed from the N and P lists once a rule that satisfies them has been found, but instead these
	 * records simply have their weighting reduced. This reduced weighting forms part of the algorithm to 
	 * calculate gains, however it is still possible for the same attributes to be selected to form the 
	 * antecedent of a rule because these attributes (despite the reduce weighting) still produce the best
	 * gain. Eventually the weighting for the effected records is reduced so far that the attributes do not 
	 * produce a best gain and are therefore not selected. Where this occurs the rules with the lower accuracy 
	 * are removed from the rule list.
	 */
	protected void processRules() {
		// Remove duplicates from rule list
		removeDuplicateRules();
	}


	/**
	 * Processes linked list of rules and removes any duplicates.
	 */
	private void removeDuplicateRules() {
		RuleList.RuleNode linkRuleNode = currentRlist.startRulelist;

		// Loop through linked list looking for duplicates
		while (linkRuleNode != null) {
			removeDuplicate(linkRuleNode, linkRuleNode.antecedent, linkRuleNode.consequent);
			linkRuleNode = linkRuleNode.next;	
		}
	}

	
	/**
	 * Processes linked list of rules and removes any duplicate of the given rule.
     */
	private void removeDuplicate(RuleList.RuleNode linkRef, short[] antecedent, short[] consequent) {
		RuleList.RuleNode markerNode = linkRef;
		RuleList.RuleNode linkRuleNode = linkRef.next;

		// Loop through linked list looking for duplicate rule
		while (linkRuleNode != null) {
			if (isEqual(antecedent,linkRuleNode.antecedent) && isEqual(consequent,linkRuleNode.consequent)) {
				markerNode.next = linkRuleNode.next;
			}
			else markerNode = linkRuleNode;
			linkRuleNode = linkRuleNode.next;
		}
	}

 	
	/**
	 * Gets the current instance of the RuleList class.
	 * @return the current RuleList object.
	 */
	public RuleList getCurrentRuleListObject() {
		return(currentRlist);
	}	


	/**
	 * Copies the given 2-D array of doubles, which is assumed to have a second dimension of 2, 
	 * and returns the copy.
	 * @param oldArray the given 2-D double array.
	 * @return copy of the given array.
	 */
	protected double[][] copyDouble2Darray(double[][] oldArray) {
		// Initialize new array
		double[][] newArray = new double[oldArray.length][2];

		// Loop through old array
		for(int index=0;index<oldArray.length;index++) {
			newArray[index][0] = oldArray[index][0];
			newArray[index][1] = oldArray[index][1];
		}
		return(newArray);
	}	


	/**
	 * Makes and returns a copy of a given 2-D array of short integers.
	 * @return a copy of the input array.
	 */
	protected short[][] copy2DshortArray(short[][] inputArray) {
		// Check for empty input array
		if (inputArray==null) return(null);

		// Dimension new array
		short[][] newArray = new short [inputArray.length][];

		// Loop through old array
		for (int index1=0;index1<inputArray.length;index1++) {
			if (inputArray[index1]==null) newArray[index1]=null;
			else {
				newArray[index1] = new short[inputArray[index1].length];
				for (int index2=0;index2<inputArray[index1].length;
				index2++) {
					newArray[index1][index2]=inputArray[index1][index2];
				}
			}
		}
		return(newArray);
	}
}