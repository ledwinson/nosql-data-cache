# nosql-data-cache
faster data look up 

Lookup your data fast and process it fast.

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
  
