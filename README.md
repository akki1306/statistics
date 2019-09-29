## General Description

API is supposed to calculate real-time statistics summary based on transaction data records received for the last 60 seconds. Calculation of statistic is made in constant time and memory (O(1)).

### Steps to Build

Run command mvn spring-boot:run 

### Assumptions

- clean up thread runs every second to remove old transaction data, parallelism threshold is currently 1 but can be incremented to increase quick removal of entries from concurrent hash map.
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
    	"sum": 1000,
    	"avg": 100,
    	"max": 200,
    	"min": 50,
    	"count": 10
    }

where:
 - sum: double specifying total sum of transaction values in the last 60 seconds
 - avg: double specifying average of all transaction values in the last 60 seconds
 - max: double specifying highest transaction value in the last 60 seconds
 - max: double specifying lowest transaction value in the last 60 seconds
 - count: long specifying total number of transactions happened in the last 60 seconds


#### GET /statistics/{id}

This is the endpoint with statistics for a given instrument.It returns the statistic based on the ticks with a given instrument identifier happened in the last 60 seconds. Response is the same as for previous endpoint but with instrument specific statistics.
