# nosql-data-cache
Faster data look up 

Do you access data from  database frequently to acheive a business process?

Do you have a large file that you have to iterate and identify some records frequently?

Try this nosql-data-cache and this will simplify the look up.

This API will help you to load and index the minimal data required to achive a business process based on the look up queries.  

Once you done with the cache,release the cache. 

Java version: JDK 7 or above.

           <dependency>
			<groupId>com.github.ledwinson</groupId>
			<artifactId>nosql-data-cache</artifactId>
			<version>1.0.0</version>
		</dependency>

### Test it before using it and see how it fits to your use case.
  This cache mainly depedens on two things, one is the data and second is the look up queries.   
  The data can be loaded to cache by passing an implementation of DataSourceResolver<T>.  
  The lookup queries can be declared in an interface exting from Snapshot interface.
  
