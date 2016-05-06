package com.company;

import com.aliasi.util.Files;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;

import com.aliasi.lm.NGramProcessLM;

import java.io.File;
import java.io.IOException;

/**
 * Class used for analyzing sentiment in the sample taken from Twitter.
 *
 * In order to use, a user must first call the @train method to create and train a LingPipe classifier.
 * This will be done using a known data-set. All relevant data used for this purpose can be found in
 * the directory /POLARITY_DIR/txt_sentoken.
 *
 * User can also call @evaluate to get a readout of how accurate the trained classifier is,
 * again tested on a known-data set. Anything over about 80-percent accuracy is generally considered
 * acceptable for LingPipe.
 *
 * Calling @invest, and passing in a data file to evaluate, returns a determination on whether the
 * user should invest in the stock market in the near future (next few hours), assuming the data passed in
 * represents public sentiment collected immediately prior.
 */
public class PolarityBasic {

    // Setup class variables to be used in the methods below
    File mPolarityDir;
    String[] mCategories;
    DynamicLMClassifier<NGramProcessLM> mClassifier;

    PolarityBasic(String[] args) {
        System.out.println("\nTRAINING LINGPIPE CLASSIFIER & TESTING ITS ACCURACY:");
        System.out.println("------------------------------------------------------");
        mPolarityDir = new File(args[0],"txt_sentoken");
        System.out.println("\nData Directory=" + mPolarityDir);
        mCategories = mPolarityDir.list();
        int nGram = 8;
        mClassifier
                = DynamicLMClassifier
                .createNGramProcess(mCategories,nGram);
    }

    /**
     * Method used for running the @train and @evaluate methods all in one  call
     * @throws ClassNotFoundException
     * @throws IOException
     */
    void run() throws ClassNotFoundException, IOException {
        train();
        evaluate();
    }

    /**
     * Utility method used within @train and @evaluate for keeping track of which files should be
     * used for training and which should be used for evaluation and confirmation that sentiment
     * analysis algorithm is working accurately.
     *
     * Changing the number below will change the data set on which the @evaluation method is tested.
     * User can select a number 1-9, corresponding to file names within the /POLARITY_DIR/txt_sentoken
     * folders.
     *
     * @param file - Pass in a file and determine whether it should be used for training, based on
     *             file name.
     * @return - A boolean value, "True" if it should be used for training, and not evaluation.
     * "False" if the file should be used for evaluation and not training.
     */
    boolean isTrainingFile(File file) {
        return file.getName().charAt(2) != '9';  // test on fold 9
    }


    /**
     * This method is used to train LingPipe's classifier on an example data set.
     * @throws IOException - when input files cannot be found.
     */
    void train() throws IOException {
        int numTrainingCases = 0;
        int numTrainingChars = 0;
        System.out.println("\nTraining.");
        for (int i = 0; i < mCategories.length; ++i) {
            String category = mCategories[i];
            System.out.println(category);
            Classification classification
                    = new Classification(category);
            File file = new File(mPolarityDir,mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (isTrainingFile(trainFile)) {
                    ++numTrainingCases;
                    String review = Files.readFromFile(trainFile,"ISO-8859-1");
                    numTrainingChars += review.length();
                    Classified<CharSequence> classified
                            = new Classified<CharSequence>(review,classification);
                    mClassifier.handle(classified);
                }
            }
        }
        System.out.println("  # Training Cases=" + numTrainingCases);
        System.out.println("  # Training Chars=" + numTrainingChars);
    }


    /**
     * Method used for testing the trained classifier. Calling this class outputs a stream
     * showing what data is being evaluated and the result of that evaluation.
     *
     * At the end of the calculation, the method prints a String detailing how accurate the trained classifier
     * is, when tested against a known data-set.
     *
     * @throws IOException - when input files cannot be found
     */
    void evaluate() throws IOException {
        System.out.println("\nEvaluating.");
        int numTests = 0;
        int numCorrect = 0;
        for (int i = 0; i < mCategories.length; ++i) {
            String category = mCategories[i];
            File file = new File(mPolarityDir,mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (!isTrainingFile(trainFile)) {
                    String review = Files.readFromFile(trainFile,"ISO-8859-1");
                    ++numTests;
                    Classification classification
                            = mClassifier.classify(review);
                    System.out.println("classification is: " + classification.bestCategory());
                    if (classification.bestCategory().equals(category))
                        ++numCorrect;
                }
            }
        }
        System.out.println("  # Test Cases=" + numTests);
        System.out.println("  # Correct=" + numCorrect);
        System.out.println("  % Correct="
                + ((double)numCorrect)/(double)numTests);
    }


    /**
     * Method used for testing a trained classifier on an input file. The method will evaluate
     * the file passed in and make a determination on whether the text inside shows
     * positive or negative sentiment.
     *
     * Method is intended to for use in context of an investment algorithm, under the presupposition
     * that a user will want to make an investment only when the file passed in contains positive sentiment.
     *
     * @param fileName - File to test for positive/negative sentient
     * @return - a boolean value:  True, if sentiment is positive.  False, if sentiment is negative.
     * @throws IOException - when input file cannot be found.
     */
    public boolean invest(String fileName) throws IOException{
        boolean makeInvestment = false;

        // Open file, get its data, perform classification
        File twitterFile = new File(fileName);
        System.out.println("\n\nUsing this file to make determination: " + fileName);
        String twitterData = Files.readFromFile(twitterFile,"ISO-8859-1");
        Classification classification
                = mClassifier.classify(twitterData);

        // If classification is positive, return boolean true, otherwise return false
        System.out.println("Analysis has determined overall public sentiment to be: " + classification.bestCategory());
        if (classification.bestCategory().equals("pos")) {
            makeInvestment = true;
        }
        System.out.println("\n\n");

        return makeInvestment;
    }


}


