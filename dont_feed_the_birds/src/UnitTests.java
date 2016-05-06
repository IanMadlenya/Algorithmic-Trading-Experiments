package com.company;


import java.io.*;
import java.util.HashMap;

/**
 * Test class, used for ensuring that:
 *  - LingPipe makes accurate determinations, given a file with known
 * sentiment.
 *  - Logic for the book-keeping function is functions as expected, and there are not mathematical errors
 *
 */
public class UnitTests {

    /**
     * Method used to run all tests as a suite, ensuring that all logic is functional.
     */
    public static void runTestSuite() {
        // Run unit tests to ensure all logic is functional
        UnitTests testInstance = new UnitTests();
        System.out.println("The investment logic worked accurately with NEGATIVE sentiment: " +
                testInstance.investmentsPerformedAccuratelyWithNegativeSentiment());
        System.out.printf("The investment logic worked accurately with POSITIVE sentiment: " +
                testInstance.investmentsPerformedAccuratelyWithPositiveSentiment());
        System.out.println();
        System.out.println("\n\nLingPipe's trained classifier is accurately able to determine " +
                "POSITIVE sentiment: " + testInstance.posSentimentCorrectlyIdentified());
        System.out.println("\n\nLingPipe's trained classifier is accurately able to determine " +
                "NEGATIVE sentiment: " + testInstance.negSentimentCorrectlyIdentified());
    }

    /**
     * Method used for testing whether POSITIVE sentiment is correctly identified by LingPipe.
     * @return - boolean, "True" if LingPipe is functioning as expected.
     */
   public boolean posSentimentCorrectlyIdentified() {

        // Create example file with overtly positive sentiment
        File posSentiment = new File("testData/posSentiment.txt");
        String exampleFilePath = posSentiment.getAbsolutePath();

        // Create instance of LingPipe classifier, train it, then ensure it works on example
        boolean exampleSentiment = false;
        try {
            PolarityBasic pb = new PolarityBasic(new String[]{
                    "POLARITY_DIR"});
            pb.train();
            pb.evaluate();
            exampleSentiment = pb.invest(exampleFilePath);

        } catch (Throwable t) {
            System.out.println("Thrown: " + t);
            t.printStackTrace(System.out);
        }

       boolean testPassed = false;
       if (String.valueOf(exampleSentiment).equalsIgnoreCase("true")) {
           testPassed = true;
       }

       return testPassed;

    }

    /**
     * Method used for testing whether NEGATIVE sentiment is correctly identified by LingPipe.
     * @return - boolean, "True" if LingPipe is functioning as expected.
     */
    public boolean negSentimentCorrectlyIdentified() {
        boolean testPassed = false;

        // Create example file with overtly negative sentiment
        File posSentiment = new File("testData/negSentiment.txt");
        String exampleFilePath = posSentiment.getAbsolutePath();


        // Create instance of LingPipe classifier, train it, then ensure it works on example
        boolean exampleSentiment = true;
        try {
            PolarityBasic pb = new PolarityBasic(new String[]{
                    "POLARITY_DIR"});
            pb.run();
            exampleSentiment = pb.invest(exampleFilePath);

        } catch (Throwable t) {
            System.out.println("Thrown: " + t);
            t.printStackTrace(System.out);
        }
        if (String.valueOf(exampleSentiment).equalsIgnoreCase("false")) {
            testPassed = true;
        }

        return testPassed;
    }

    /**
     * Method used for testing whether the investment logic and the subsequent calcuations are performed
     * correctly when given the go-ahead to invest on any given day.
     * @return - boolean, "True" if the logic is functioning as expected.
     */
    public boolean investmentsPerformedAccuratelyWithPositiveSentiment() {

        // Create example values
        HashMap<String, String> exampleData =  new HashMap<String, String>();
        exampleData.put("Open", "100");
        exampleData.put("Close", "101");

        double totalAssets = 10000.00;
        boolean sentiment = true;


        // Test investment function
        double programResult = DataOps.investmentResult(exampleData,totalAssets, sentiment);


        // Perform expected operation here, then compare
        String updatedStockPriceString = exampleData.get("Close");
        double updatedStockPrice = Double.parseDouble(updatedStockPriceString);
        double testResult = totalAssets * updatedStockPrice;

        boolean testPassed = false;
        if (String.valueOf(programResult).equalsIgnoreCase(String.valueOf(testResult))) {
            testPassed = true;
        }
        return testPassed;

    }

    /**
     * Method used for testing whether the investment logic and the subsequent calcuations are performed
     * correctly when algorithmically told NOT to invest on any given day.
     * @return - boolean, "True" if the logic is functioning as expected.
     */
    public boolean investmentsPerformedAccuratelyWithNegativeSentiment() {

        // Create example values
        HashMap<String, String> exampleData =  new HashMap<String, String>();
        exampleData.put("Open", "100");
        exampleData.put("Close", "101");

        double totalAssets = 10000.00;
        boolean sentiment = false;

        // Test investment function
        double programResult = DataOps.investmentResult(exampleData,totalAssets, sentiment);

        // Perform expected operation here, for comparison
        String updatedStockPriceString = exampleData.get("Close");
        double updatedStockPrice = Double.parseDouble(updatedStockPriceString);
        double testResult = totalAssets;

        boolean testPassed = false;
        if (String.valueOf(programResult).equalsIgnoreCase(String.valueOf(testResult))) {
            testPassed = true;
        }
        return testPassed;
    }

}



