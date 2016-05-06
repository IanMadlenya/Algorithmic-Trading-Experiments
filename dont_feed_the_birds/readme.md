### Anthony Poerio (no group members)
### Project 2 Proposal  
## Don’t Feed the Birds:  Create a Medium-Frequency Trading Algorithm Based on Twitter Sentiment

## Introduction
I would like to create a simple simulation experiment to determine whether public sentiment, as expressed on social networks, can be used to gain a competitive advantage in the stock market, using only publicly available resources.

My program will test the following hypothesis:  When overall public sentiment is **positive**, the value of the stock market will *rise*. When overall public sentiment is **negative**, the value of the stock market will *fall*. Therefore, if we are able to mine a representative sample of data containing the overall public sentiment, we can use this information to create a successful trading strategy. 

I plan to use Twitter as my data source for public sentiment. I’d like to obtain my data from Twitter’s API through the **Java Twitter4J** library. I will then run my Twitter data sample through the sentiment analysis algorithms available within **LingPipe** to determine overall public sentiment over a given time period. If sentiment is positive, the program will maintain its market position or invest. If sentiment is negative, the program will sell its assets. 



## Description

#### Design
My plan is to seed the simulation with 100 shares of the **SPY S&P 500 Index Fund** run by State Street [(http://finance.yahoo.com/q/hp?s=SPY+Historical+Prices)](http://finance.yahoo.com/q/hp?s=SPY+Historical+Prices), then run an algorithm to determine if OVERALL public sentiment has been net positive over the past 24 hours.

If sentiment is POSITIVE, the money will remain in the market for the day. 

If public sentiment is NEGATIVE, the program will sell its investments as soon as trading begins for the day. After a sale of its stock, the program will not buy back into market until sentiment is net POSITIVE.

The goal is to only take gains or losses on days when public sentiment is positive. The experiment will assume ideal conditions, such that: stocks are bought or sold immediately at market open for the day, and there are no trading fees. 

#### Data Analysis and Manipulation
My program will access Twitter’s “Random Sample” API endpoint, using the **Twitter4J Java library**. 
- Details at: 
     *   [http://twitter4j.org/javadoc/twitter4j/TwitterStream.html#sample--](http://twitter4j.org/javadoc/twitter4j/TwitterStream.html#sample--)
     *  [https://dev.twitter.com/streaming/reference/get/statuses/sample](https://dev.twitter.com/streaming/reference/get/statuses/sample) 

The program will then run this sample through **LingPipe’s sentiment analysis algorithms** to determine whether the sentiment of the sample is OVERALL positive or Negative:
- Details at:
     *  [http://alias-i.com/lingpipe/demos/tutorial/sentiment/read-me.html](http://alias-i.com/lingpipe/demos/tutorial/sentiment/read-me.html)

After determining overall sentiment, the program will take an action (buy, sell, hold), and record the result in a **File**. 

As a **control for the experiment**, I will also store the results of 100 shares of SPY stock added into the market on the same date and never removed. This will help us determine if using Twitter sentiment as an investment strategy is *more* or *less* effective than passive management. 

If stock values depreciate such that either total reaches zero (hopefully not), the simulation will end. 

#### Data Review and Control
For data review, the program will require a **class** and set of **methods** which do the following:
- Check the results of the **S&P 500 SPY Index Fund**’s daily performance, then apply gains or losses to each running total
     *  This can be done using the “OPEN” and “CLOSE” data points from the spreadsheets at: [(http://finance.yahoo.com/q/hp?s=SPY&a=02&b=18&c=2015&d=02&e=18&f=2015&g=d)](http://finance.yahoo.com/q/hp?s=SPY&a=02&b=18&c=2015&d=02&e=18&f=2015&g=d]) 
     *  I plan on using this link to download each day’s sheet directly, by manipulating this URL string in the program:  [(http://real-chart.finance.yahoo.com/table.csv?s=SPY&a=02&b=18&c=2015&d=02&e=18&f=2015&g=d&ignore=.csv)](http://real-chart.finance.yahoo.com/table.csv?s=SPY&a=02&b=18&c=2015&d=02&e=18&f=2015&g=d&ignore=.csv)
     *  I’d like to use the **Crawler4J** WebCrawler for assistance [(https://github.com/yasserg/crawler4j)](https://github.com/yasserg/crawler4j).
- A method which handles printing results for comparison and confirmation, when asked via the command line 

Once this data is obtained, I will setup a **Control class** and set of **methods**, so that the program’s actions and logic can be confirmed with a series of rapid transactions, without needing to wait for time to pass. (** *To note:  I don’t see an easy way to get real-time data to evaluate as part of this rapid transaction process, so I’ll only be testing that the correct actions are performed based on current sentiment, using the correct data points for OPEN and CLOSE.*) 

Once the logic is confirmed, my goal will be to configure the program so that it can run once a day, using the **Quartz Scheduler Library** [(http://www.quartz-scheduler.org/)](http://www.quartz-scheduler.org/). The set of operations specified above will execute daily, and the results will be stored in a **File**, so they can be followed over time. 

The ideal environment for the program will be a web server, so that it does not need to constantly run in the background on a user’s local machine in order to make its daily transactions. If time permits, I’d like to configure the program to run on **Heroku** ([https://devcenter.heroku.com/categories/java](https://devcenter.heroku.com/categories/java)). This would be a stretch goal.



## Extra Credit
I would like to work on the following for extra credit: 
- **Deploy application on Heroku** [(https://devcenter.heroku.com/categories/java)](https://devcenter.heroku.com/categories/java) and **Auto-post results to a public facing web-page**, so they can be easily viewed and followed over time.
- Use a chart creating library, such as **Charts4J** [(https://github.com/julienchastang/charts4j)](https://github.com/julienchastang/charts4j) to better display the results of the experiment
- Store all information in a **database, instead of a file**
- **Allow user to update the algorithm**, so that investment decisions can be made based on overall positive sentiment for a week, month, or other user-specified timeframe. This would allow  a user to tweak and refine the algorithm’s logic on an ongoing basis. 
- **Compare performance** against a set of well-known hedge funds and algorithmic investment vehicles, seeing how a rudimentary strategy based on Twitter sentiment compares to more sophisticated financial products. 


## Topics and Libraries Used
The full list of topics and libraries I plan to use is: 
- **Methods, classes, and objects** - I will need to make use of methods, classes, and objects to cleanly organize and perform the following operations:
     *  Download sentiment data from Twitter’s API
     *  Feed daily Twitter data into LingPipe
     *  Perform LingPipe analysis and determine sentiment
     *  Test to ensure program logic is working working as expected
     *  Keep money in stock market or pull it out based on the result of the daily sentiment analysis
     *  Crawl web and get daily closing price results
     *  Apply gains/losses to overall running tab for the simulation
     *  Automate the analysis algorithm to run once daily
     *  Write data to a file a each day, to keep track of results
     *  Print data when requested, to show results of experiment
- **Files** - I will write all data to CSV or Text files so that they can be tracked over time. I will also need to get daily market information from a CSV file, and Twitter sentiment data will be analyzed from data in a text file. 
- **Twitter4J Java library** - This is for getting a daily sample of sentiment data from Twitter. I plan to use the Twitter API’s *GET statuses/sample* endpoint.[(http://twitter4j.org/)](http://twitter4j.org/)
- **LingPipe Library** - I plan to use LingPipe’s sentiment analyses tools to determine whether overall sentiment is positive or negative. [(http://alias-i.com/lingpipe/)](http://alias-i.com/lingpipe/)
- **Crawler4J Webcrawler** - I will use the Crawler4J Webcrawler to download a daily report of the SPY Index fund’s performance. [(https://github.com/yasserg/crawler4j)](https://github.com/yasserg/crawler4j) 
- **Quartz Scheduler** - I will use Quartz Scheduler [(http://www.quartz-scheduler.org/)](http://www.quartz-scheduler.org/) to automate the algorithm, so it can run once daily.