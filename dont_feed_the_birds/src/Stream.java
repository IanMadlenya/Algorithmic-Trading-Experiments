package com.company;

import java.io.*;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.*;

/**
 * Class for authenticating with Twitter and grabbing a specified number of statuses for a real-time sample.
 * The class writes all data to a text named "outputdata.txt".
 *
 * Twitter keys and secrets must be changed per-user, in-line below.
 * See @execute method to edit output file location and Twitter authentication credentials.
 *
 */
public class Stream {

    private final Object lock = new Object();
    /**
     * Method used for authenticating with Twitter and writing all data to to a specified output File.
     * Twitter keys and secrets must be changed per user, and specified explicitly.
     *
     * @return - A List of the Statuses collected from Twitter
     * @throws TwitterException - When unable to authenticate or read data from Twitter
     * @throws IOException - When output file cannot be found, or data cannot be written to it.
     */
    public List<Status> execute() throws TwitterException, IOException {

        final List<Status> statuses = new ArrayList();
        final FileWriter file = new FileWriter("outputdata.txt");


        // Authenticate with Twitter
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(KEY);
        cb.setOAuthConsumerSecret(SECRET);
        cb.setOAuthAccessToken(TOKEN);
        cb.setOAuthAccessTokenSecret(TOKENSECRET);

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
                .getInstance();

        StatusListener listener = new StatusListener() {


            public void onStatus(Status status) {
                // Set stream to stop after collecting 100 statuses
                statuses.add(status);
                System.out.println(statuses.size() + ":" + status.getText());
                if (statuses.size() > 100) {
                    synchronized (lock) {
                        lock.notify();
                    }
                    System.out.println("unlocked");
                }

                // Parse stream data and write it to a text file
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText() + " -> "+
                        status.getCreatedAt());
                try {
                    file.write(status.getText()+"\n");
                    file.flush();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            public void onDeletionNotice(
                    StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:"
                        + statusDeletionNotice.getStatusId());
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:"
                        + numberOfLimitedStatuses);
            }

            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId
                        + " upToStatusId:" + upToStatusId);
            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onStallWarning(StallWarning sw) {
                System.out.println(sw.getMessage());

            }

        };

        // Filter stream by statuses Geo-located in the United States and written in English
        FilterQuery fq = new FilterQuery();
        fq.locations(new double[][]{new double[]{-126.562500, 30.448674},
                new double[]{-61.171875,44.087585
                }});
        fq.language(new String[]{"en"});
        twitterStream.addListener(listener);
        twitterStream.filter(fq);

        // Collect 100 statuses, confirm completion, shutdown TwitterStream
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("100 statuses received");
        twitterStream.shutdown();

        return statuses;
    }

}
