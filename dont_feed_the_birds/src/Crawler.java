package com.company;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.File;


/**
 * Class used for obtaining daily financial data from the Web.
 * Current source is Yahoo Finance, specifically the SPY Index fund.
 * This class contains a number of methods which are all used together in @downloadFile,
 * which is the main method of the class.
 */
public class Crawler {

    /**
     * Method used for downloading a file from the internet and saving to the user's computer.
     * @param filename - Name for the file to be created, containing the information downloaded from the web
     * @param urlString - URL from which to download data
     * @throws MalformedURLException - When the URL is not valid
     * @throws IOException - When the file cannot be created or written to
     */
    public void saveUrl(final String filename, final String urlString)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
            System.out.println(filename);
        }
    }

    /**
     * Method used for getting the current day's date. This is a utility method used for algorithmically
     * constructing a URL, and getting the current day's financial data.
     * @return - a String containing the current day of month, as a number
     */
    public String getDay() {
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        String dayOfMonthStr = String.valueOf(dayOfMonth);

        return dayOfMonthStr;
    }

    /**
     * Method used for getting the current month. This is a utility method used for algorithmically
     * constructing a URL, and getting the current month's financial data.
     * @return - a String containing the current month of year, as a number
     */
    public String getMonth() {
        Calendar cal = Calendar.getInstance();
        int monthOfYear = cal.get(Calendar.MONTH);

        String monthOfYearStr = String.valueOf(monthOfYear);

        if (Integer.parseInt(monthOfYearStr) < 10) {
            monthOfYearStr = "0" + monthOfYearStr;
        }
        return monthOfYearStr;
    }

    /**
     * Method used for getting the current year. This is a utility method used for algorithmically
     * constructing a URL, and getting the current day's financial data.
     * @return - a String containing the current year as a number
     */
    public String getYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        String yearStr = String.valueOf(year);

        return yearStr;
    }

    /**
     * Method used for creating a URL string to pass to @saveURL, formatted such that it can download
     * the current day's SPY Index Fund data from Yahoo Finance.
     * @param day - Day of the week, as a number in a String
     * @param month - Month of the year, as a number in a String
     * @param year - Year, as a number in a String
     * @return - A formatted URL which can be used by @saveURL
     */
    public String createURLString(String day, String month, String year){
        String returnURL = "http://real-chart.finance.yahoo.com/table.csv?s=SPY&a=" +
                month + "&b=" + day + "&c=" + year + "&d=" + month + "&e=" + day +
                "&f=" + year + "5&g=d&ignore=.csv";
        return returnURL;
    }

    /**
     * Method used for algorithmically ensuring that a valid file can be downloaded by @saveURL.
     * The method uses @getDay, @getMonth, @getYear, @subtractDay and @createURLString to ensure that @savURL
     * continues attempting to download files until a valid one is found.
     */
    public void downloadFile() {
        Crawler crawler = new Crawler();

        System.out.println("DAY OF MONTH IS: " + crawler.getDay());
        System.out.println("MONTH OF YEAR IS: " + crawler.getMonth());
        System.out.println("YEAR IS: " + crawler.getYear());

        // Get day, month, year, as Strings
        String day = crawler.getDay();
        String month = crawler.getMonth();
        String year = crawler.getYear();

        // Increment day +1, will subtract below to create
        // a loop that iterates through days until a valid file is found
        int dayInt = Integer.parseInt(day) + 1;
        day = String.valueOf(dayInt);

        ArrayList<String> dates = new ArrayList<String>();

        boolean fileUnavailable = true;
        while (fileUnavailable) {

            // Try tomorrow's day, then today, and so on until a valid file is found
            dates = subtractDay(day, month, year);
            day = dates.get(0);
            month = dates.get(1);
            year = dates.get(2);
            String financeURL = crawler.createURLString(day, month, year);

            try {
                // Attempt to download file
                crawler.saveUrl("finance.csv", financeURL);
                fileUnavailable = false;
            } catch (FileNotFoundException fnfe) {
                System.out.println("File was not found, trying previous day.");
                fileUnavailable = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                fileUnavailable = true;
            }
        }

    }

    /**
     * Method used within @downloadFile to algorithmically create a valid URL string, by subtracting
     * a single day from the calendar date until a valid file is found.
     * @param day - Day of the week, as a number in a String
     * @param month - Month of the year, as a number in a String
     * @param year - Year, as a number in a String
     * @return - an ArrayList<String> object with data for use by the @downloadFile class
     */
    public ArrayList<String> subtractDay(String day, String month, String year) {
        int dayAsInt = Integer.parseInt(day);
        int monthAsInt = Integer.parseInt(month);
        int yearAsInt = Integer.parseInt(year);


        // Drop day/month/year
        dayAsInt = dayAsInt - 1;
        if (dayAsInt == 0) {
            dayAsInt = 31;
            monthAsInt = monthAsInt - 1;

            if (monthAsInt == 0) {
                monthAsInt = 12;
                yearAsInt = yearAsInt - 1;
            }
        }
        // Transform to String
        String returnDay = Integer.toString(dayAsInt);
        String returnMonth = Integer.toString(monthAsInt);
        String returnYear = Integer.toString(yearAsInt);

        // Create array list for return value
        ArrayList<String> returnList = new ArrayList<String>();
        returnList.add(returnDay);
        returnList.add(returnMonth);
        returnList.add(returnYear);

        return returnList;
    }

}
