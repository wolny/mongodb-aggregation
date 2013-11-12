Sample web application demonstrating how to efficiently aggregate numerical data from multiple sources
using MongoDB in order to perform real-time statistical analytics.

Application can be used as a part of the monitoring subsystem in which
we can specify some numerical resources to be monitored (e.g user session time, stock prices, etc.),
and for these resources we want to compute statistics like: average and standard deviation.
We also want the statistics to be available for different time range (minute, hour, day, etc.)
and different groups (e.g users from different countries, stocks from different companies, etc.).
These statistics must be computed almost instantly (from milliseconds up to a second) for a given time range.

Application is based on Spring MVC deployed to Jetty web server.
Two JSON REST services are exposed to the client:
* to retrieve real-time statistics aggregated by hour for a given time range (URI template: /rest/{resource}/{start}/{end})
* to collect numerical time series for a given resource (URI template: /rest/collector)

To run the application type: gradlew jettyRunWar
Application home page can be seen at: http://localhost:8080/
Date format to be used to specify time range in the main form: 'yyyy-MM-dd_HH:mm:ss'

In order to generate normally distributed sample data for a given time range use SamplesGenerator class, for which you can specify:
* the name of the resource to be analyzed
* start date of the time range
* end date of the time range
* mean value of Gaussian distribution
* standard deviation of Gaussian distribution

then just run the application and run SamplesGenerator using your favourite IDE.

