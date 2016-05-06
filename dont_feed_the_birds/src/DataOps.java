package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;


/**
 * Class for performing operations on Files containing financial data.
 *
 */
public class DataOps {

    /**
     * Method for obtaining a HashMap of the day's stock market data
     * Uses a file name "finance.csv", created by the Crawler#downloadFile method
     * @return HashMap containing Market data for the day
     */
    public static HashMap getMarketData() {
        ArrayList<String> csvRows = new ArrayList<String>();
        HashMap<String, String> csvHash = new HashMap<String, String>();

        // Create scanner
        try {
            Scanner financialCSV = new Scanner(
                    new File("finance.csv"));

            // Set the delimiter used in file
            financialCSV.useDelimiter(",");

            // Store everything in an ArrayList
            while (financialCSV.hasNext()) {
                if (financialCSV.hasNext()) {
                    csvRows.add(financialCSV.next());
                }
            }
            System.out.println("final CSV file is: " + csvRows);

            // Convert ArrayList to HashMap, in order to access open and close data more easily
            // Note: skips "Date" field
            for (int i=1; i < (csvRows.size()/2); i++) {
                csvHash.put(csvRows.get(i), csvRows.get((i+6)));
            }

            // Close Scanner
            financialCSV.close();

        } catch (FileNotFoundException fnfe) {
            System.out.println("Sorry, that file could not be found");
            fnfe.printStackTrace();
        } catch (NoSuchElementException nse) {
            System.out.println("Sorry, there was an element missing in the data file");
            nse.printStackTrace();
        } catch (Exception e) {
            System.out.println("Sorry, there was an error.");
            e.printStackTrace();
        }

        // return HashMap containing the day's market data
        return csvHash;
    }

    /**
     * Initialize the experiment by creating CSV files for CONTROL and EXPERIMENTAL data
     * 100 Shares of market index fund (use the hashMap from get @getMarketData - for both files.
     * --> WARNING:  Calling this method will ERASE all current data from the running total.
     */
    public static void initializeExperiment(HashMap marketData) {

        try {

            // Get OPEN and CLOSE price for the day, parse as a Doubles
            String stockClosingPriceString = (String) marketData.get("Close");
            double stockClosingPrice = Double.parseDouble(stockClosingPriceString);
            String stockOpeningPriceString = (String) marketData.get("Open");
            double stockOpeningPrice = Double.parseDouble(stockOpeningPriceString);


            // Multiply closing price by 100. This equals price for 100 shares of stock.
            double purchaseTotal = stockClosingPrice * 100;


            // Get day's date for timestamp
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));


            // Write data to control file
            File controlFile = new File("controlHistory.txt");
            PrintWriter controlWriter = new PrintWriter(controlFile);
            controlWriter.print(dateFormat.format(date) + ",");
            controlWriter.print(purchaseTotal + ",");
            controlWriter.print("Opening price: " + stockOpeningPrice + ",");
            controlWriter.print("Closing price: " + stockClosingPrice + ",\n");
            // Close Writer
            controlWriter.close();


            // Write data to experiment file
            File experimentFile = new File("experimentHistory.txt");
            PrintWriter experimentWriter = new PrintWriter(experimentFile);
            experimentWriter.print(dateFormat.format(date) + ",");
            experimentWriter.print(purchaseTotal + ",");
            experimentWriter.print("Opening price: " + stockOpeningPrice + ",");
            experimentWriter.print("Closing price: " + stockClosingPrice + ",\n");
            // Close writer
            experimentWriter.close();


        } catch (FileNotFoundException fnfe) {
            System.out.println("Could not write file and initialize experiment. Location on File System not found");
            fnfe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error encountered. See stack trace below: ");
            e.printStackTrace();
        }

    }

    /**
     * Get Data from the ongoing experiment TXT or CSV file
     * Format it for writing
     */
    public static double getExperimentTotal(File inputFile) {

        double experimentTotal = -1;

        // Open inputFile, read last line, store its data in an ArrayList, separated by commas
        try {
            FileReader fr = new FileReader(inputFile);
            BufferedReader input = new BufferedReader(fr);
            String last = "";
            String line = "";

            while ((line = input.readLine()) != null) {
                last = line;
            }
            input.close();
            fr.close();

            ArrayList<String> list = new ArrayList<String>(Arrays.asList(last.split(",")));
            experimentTotal = Double.parseDouble(list.get(1));

        } catch (FileNotFoundException fnfe) {
            System.out.println("File was not found");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return experimentTotal;

    }

    /**
     * Get data from the ongoing Control TXT or CSV file
     * Format it for writing
     */
    public static double getControlTotal(File inputFile) {
        double controlTotal = -1;

        try {
            FileReader fr = new FileReader(inputFile);
            BufferedReader input = new BufferedReader(fr);
            String last = "";
            String line = "";

            while ((line = input.readLine()) != null) {
                last = line;
            }
            input.close();
            fr.close();

            //System.out.println("Last line of controlFile is: " + last);
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(last.split(",")));
            //System.out.println("The list just created is:  " + list);
            //System.out.println("List item #1 is the TOTAL COST, which is:  " + list.get(1));

            controlTotal = Double.parseDouble(list.get(1));


        } catch (FileNotFoundException fnfe) {
            System.out.println("File was not found");
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return controlTotal;

    }

    /**
     * Operation for performing daily investment, based on stock price movements.
     * Return value is the result from daily investment activity, the updated value of
     * total assets in the market.
     * @param marketData - HashMap containing the daily price report
     * @param runningTotal - Current total assets, taken from running total in a file
     * @param positiveSentiment - determination on whether market sentiment is positive for the day
     * @return - a double for the adjusted total, only changes if sentiment was positive and an investment was made
     */
    public static double investmentResult(HashMap marketData, double runningTotal, boolean positiveSentiment) {
        String updatedStockPriceString = (String) marketData.get("Close");
        double updatedStockPrice = Double.parseDouble(updatedStockPriceString);
        double totalAssets = runningTotal;

        if(positiveSentiment) {
           totalAssets = runningTotal * updatedStockPrice;

        }
        return totalAssets;

    }


    /**
     * Method used for writing experiment data to a file on an ongoing basis
     * @param marketData - HashMap containing the daily price report
     * @param experimentTotal - current total assets from the ExperimentFile
     * @param controlTotal - current total assets from the ControlFile
     */
    public static void writeMarketResults(HashMap marketData, double experimentTotal, double controlTotal)  {
        System.out.println("\n\n");
        System.out.println("Obtained the following market data for the day:  " + marketData);
        System.out.println("Opening price is: " + marketData.get("Open"));
        System.out.println("Closing price is: " + marketData.get("Close"));

        // Create date object for writing to file
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        try {
            // Create Experiment File, append to it
            File experimentFile = new File(
                    "experimentHistory.txt");
            FileWriter experimentDataFile = new FileWriter(experimentFile, true);
            PrintWriter experimentWriter = new PrintWriter(experimentDataFile, true);
            experimentWriter.print(dateFormat.format(date) + ",");
            experimentWriter.print(experimentTotal + ",");
            experimentWriter.print("Opening price: " + marketData.get("Open") + ",");
            experimentWriter.print("Closing price: " + marketData.get("Close") + ",\n");
            // Close PrintWriter
            experimentWriter.close();

        } catch (IOException ioe) {
            System.out.println("Sorry, there was an error data to the file.");
            ioe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Sorry, there was an error.");
            e.printStackTrace();
        }

        try {
            // Create Control File, append to it
            File controlFile = new File(
                    "controlHistory.txt");
            FileWriter controlDataFile = new FileWriter(controlFile, true);
            PrintWriter controlWriter = new PrintWriter(controlDataFile, true);
            controlWriter.print(dateFormat.format(date) + ",");
            controlWriter.print(controlTotal + ",");
            controlWriter.print("Opening price: " + marketData.get("Open") + ",");
            controlWriter.print("Closing price: " + marketData.get("Close") + ",\n");
            //close PrintWriter
            controlWriter.close();

        } catch (IOException ioe) {
            System.out.println("Sorry, there was an error data to the file.");
            ioe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Sorry, there was an error.");
            e.printStackTrace();
        }

    }

}
