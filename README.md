## General Description

API is supposed to calculate real-time statistics summary based on transaction data records received for the last 60 seconds. Calculation of statistic is made in constant time and memory (O(1)).

### Steps to Build

Run command mvn spring-boot:run 

### Assumptions

- Clean up thread runs every second to remove old transaction data, parallelism threshold is currently 1 but can be increased.
- ConcurrentHashMap allows fetching statistics in O(1) time. 
- Statistics values are updated without using any locking mechanism by use of AtomicReference and AtomicLong variables.

### Further improvements

- Use google guava library for industrial strength cache eviction data structures and implement a time base sliding window.


## API endpoints

#### POST /app/tick

This endpoint is called every time a new transaction happened.

Request:

    {
       "instrument": "IBM.N",
       "amount": 12.3,
       "timestamp": 1478192204000
    }
where:
 - instrument - a financial instrument identifier (string; list of instruments is not known to our service in
advance so we add them dynamically).
 - amount: current trade price of a financial instrument (double). 
 - timestamp: tick timestamp in milliseconds (long; this is not current timestamp).

Response: Empty body with either 201 or 204.
 - 201 - in case of success
 - 204 - if transaction is older than 60 seconds
 
#### GET /statistics

This is the main endpoint of this task, this endpoint have to execute in constant time and
memory (O(1)). It returns the statistic based on the transactions which happened in the last 60
seconds.

Response:

    {
    	"sum": 1000.0,
    	"avg": 100.0,
    	"max": 200.0,
    	"min": 50.0,
    	"count": 10
    }

where:
 - sum: total sum of tick values in the last 60 seconds
 - avg: average of all tick values in the last 60 seconds
 - max: highest tick value in the last 60 seconds
 - max: lowest tick value in the last 60 seconds
 - count: specifying total number of tick happened in the last 60 seconds


#### GET /statistics/{id}

This is the endpoint with statistics for a given instrument.It returns the statistic based on the ticks with a given instrument identifier happened in the last 60 seconds. Response is the same as for previous endpoint but with instrument specific statistics.
