//Program by Milk Charity

/*
 *	This is the main running class for the GRAM program
 *	It imports all of the needed tools and functions involved with GRAM
*/


public class Gram {
    static TopicList topics;

    public static void main(String[] args){
    	topics = new TopicList("lists");		//define the directory to pull the topics from
    	topics.makeMasterList();					//make the master list
    	topics.printMasterList();					//print the master list

    	System.out.println("");
    	topics.binary();
    	System.out.println("");

		String sentence = "I like to eat tacos with my blue wolf and cat";
    	int[] binSent = topics.getSentenceBin(sentence);
    	System.out.println("Sentence: " + topics.showBinArray(binSent));

    	System.out.println("");
		System.out.println("The subject is: " + topics.getSubject(sentence));


    }

}