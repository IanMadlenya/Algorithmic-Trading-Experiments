package com.company;
import twitter4j.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This is the first iteration of a program intended to test whether public sentiment,
 * as expressed on TWitter, can be used to gain a competitive advantage for investing the
 * stock market, and whether that advantage can be applied algorithmically to earn money
 * passively over time.
 *
 * This iteration has the functionality needed and logic in place. The next step will be to
 * automate the program to run real-time.
 *
 * This class is the main entry-point of the program, and it can be used to test that the
 * logic for the experiment is in place. The test can be initiated by running class, through
 * its @main method.
 *
 * The experiment is intended to test the results of a Twitter-informed investment strategy
 * versus a strictly passive strategy. The Twitter informed Strategy is referred to with
 * variables names such as "experimentTotal", while the passive strategy is referred to with
 * variables names such as "controlTotal".
 *
 * ------------------------
 *   Optionally the user can run a set of Unit Tests designed to ensure that LingPipe's sentiment
 *   analysis is functioning as expected, and that the financial math is working as expected, based
 *   on the result of this valid data-analysis.
 *
 *   This can be down by calling UnitTests#runTestSuite.
 *
 */

public final class Main {
    /**
     * Running this method will:
     *   - Open a TwitterStream and download 100 statuses, then store them in a file
     *   - Run a LingPipe sentiment analysis on the Twitter File data and determine whether it
     *   contains either POSITIVE or NEGATIVE sentiment
     *   - Crawl the web and download the current data on the SPY Index Fund from Yahoo Finance
     *   - Buy into the stock market, if the Twitter Sentiment was positive
     *   - Check closing cost gained from market data, and write the result to a file
     *   - Output information on the Experiments progress to date
     *
     *   In general, this method is printing the results at each step, in order to confirm that
     *   the logic is functioning as expected.
     */
    public static void main(String[] args) throws TwitterException, IOException {

        // Setup Files and variables
        File experimentFile = new File("experimentHistory.txt");
        File controlFile = new File("controlHistory.txt");
        boolean invest = false;


        // Get 100 Twitter statuses in real time and write them to a file named: "outputdata.txt"
        System.out.println("Initiating Twitter Stream:");
        Stream stream = new Stream();
        try {
            stream.execute();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (TwitterException te) {
            te.printStackTrace();
        }

        // Run sentiment analysis on the Twitter data just downloaded, using LingPipe
        try {
            PolarityBasic pb = new PolarityBasic(new String[]{
                    "POLARITY_DIR"});
            pb.run();
            invest = pb.invest("outputdata.txt");
        } catch (Throwable t) {
            System.out.println("Thrown: " + t);
            t.printStackTrace(System.out);
        }

        // Download most recent financial CSV for SPY Index fund from Yahoo Finance
        Crawler crawler = new Crawler();
        crawler.downloadFile();

        // Parse daily CSV data, store it in a HashMap
        HashMap<String, String> dailyData = new HashMap<String, String>(DataOps.getMarketData());

        // Update experiment results, based on public sentiment
        double adjustedTotal = DataOps.investmentResult(dailyData, DataOps.getExperimentTotal(experimentFile),
                invest);

        // Write performance data to both the Control File and Experiment File
        DataOps.writeMarketResults(dailyData, adjustedTotal, DataOps.getControlTotal(controlFile));


        // Check Current experiment Results, print to console
        DataReview.getExperimentResults();

        // Uncomment the line below to run test suite and check application logic
        // UnitTests.runTestSuite();
    }
}
