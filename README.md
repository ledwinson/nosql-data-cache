# nosql-data-cache
Faster data look up. Can be easily used in any Spring based projects.

##Why should I use this? Why can't I leverage from existing cache technologies?
  
  This API addresses a special use case. Normally the cache APIs works by caching the results of a particular query after loading the results for the first time from database. We don't want to make this queries once the business processing is started. In our case, we load the entire data initially and make the queries to this in memory cache instead to database. 

Do you access data from  database frequently to acheive a business process?

Did you try fine tuning your databae/jpql many times and still code is not faster?

Do you have a large file that you have to iterate and identify some records frequently?

Try this nosql-data-cache and this will simplify the look up.

This API will help you to load and index the minimal data required to acheive a business process based on the look up queries.  

Once you done with the cache,release the cache. 

With 200K records in cache to look up all records with five look up queries, it took about 150 milli seconds.

Java version: JDK 7 or above. 

Note: I am in the process of uploading this to maven central. For the time being you can check out the project and build it locally as mvn package.

           <dependency>
			<groupId>com.github.ledwinson</groupId>
			<artifactId>nosql-data-cache</artifactId>
			<version>1.0.0</version>
		</dependency>

### Test it before using it and see how it fits to your use case.
  This cache mainly depedens on two things, 
  1) one is the data and 
  2) second is the look up queries.   
  
  The data can be loaded to cache by passing an implementation of DataSourceResolver<T>.  
  The lookup queries can be declared in an interface exting from Snapshot interface. The method names must start with findBy

### See Wiki for an example.
