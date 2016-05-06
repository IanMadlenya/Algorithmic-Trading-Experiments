package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for checking experiment results. Calling @getExperimetnResults will print out details on the
 * experiment, from its inception to present day.
 *
 */

public class DataReview {

    /**
     * Method used for printing out the experiment's details.
     *
     */
    public static void getExperimentResults() {
        File experimentFile = new File("experimentHistory.txt");
        File controlFile = new File("controlHistory.txt");

        // Get details on when and how the experiment was initialized
        try {
            FileReader fr = new FileReader(experimentFile);
            BufferedReader input = new BufferedReader(fr);
            String line = "";

            line = input.readLine();

            ArrayList<String> list = new ArrayList<String>(Arrays.asList(line.split(",")));
            System.out.println("\n\n");
            System.out.println("Experiment started on: " + list.get(0) );
            System.out.println("Experiment was seeded with 100 Shares of SPY Index stock");
            System.out.println("Details were: " +
                    "\t" + list.get(2) +
                    "\t" + list.get(3) +
                    "\t" + "Total assets invested: " + list.get(1));
            System.out.println("\n\n");

            input.close();
            fr.close();

        } catch (FileNotFoundException fnfe) {
            System.out.println("File was not found");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get current information from experiment files
        double currentControlTotal = DataOps.getControlTotal(controlFile);
        double currentExperimentToal = DataOps.getExperimentTotal(experimentFile);

        System.out.println("Two files were created at experiment start.");
        System.out.println("\n");
        System.out.println("In the first file, assets have only appreciated or depreciated in value on days " +
                "when Twitter Sentiment was determined to positive,\nusing LingPipe's sentiment analysis algorithms");
        System.out.println("\tThe total assets currently, using this investment strategy would be: " +
                currentExperimentToal);
        System.out.println("\n");
        System.out.println("In the second file, the same amount money has been left in the market untouched during " +
                "the same time period ");
        System.out.println("That means value has been appreciated and depreciated in accordance with the market, " +
                "not based on any particular strategy other than passive asset management.");
        System.out.println("\tUsing a passive management strategy, the same investment would today be valued at: " +
                currentControlTotal);


    }

}
