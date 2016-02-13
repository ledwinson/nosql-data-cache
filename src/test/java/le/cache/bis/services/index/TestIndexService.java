package le.cache.bis.services.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import le.cache.bis.services.Cache;
import le.cache.bis.services.IndexService;
import le.cache.bis.services.Indexer;
import le.cache.bis.services.exception.SnapshotException;
import le.cache.bis.services.impl.PrimaryKeyType;
import le.cache.bis.services.impl.TestConfig;
import le.cache.bis.services.impl.data.Membership;
import le.cache.bis.services.impl.data.SampleDataSource;
import le.cache.util.Property;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestIndexService {
    
    @Autowired
    private Cache<Long, Membership> cache;
    
    @Autowired
    private ApplicationContext context;        
    
    @Test
    public void testCacheForEmptyIndexing() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final int size = 10;
        cache.cache(new SampleDataSource(size).load(), keyType);

        @SuppressWarnings("unchecked")
        final IndexService<Long, Membership, EmptySnapshot> indexService = context.getBean(IndexService.class);

        try{
            indexService.index(cache, false, keyType , EmptySnapshot.class);
        }catch(SnapshotException e) {
            Assert.assertTrue(e.getMessage().equals("There are no findBy methods registered with " + EmptySnapshot.class.getName()));            
        }        
        Assert.assertTrue(cache.getIndexNames().size() == 0);
    }
    
    @Test
    public void testCacheForSingleMethodIndexing() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final int size = 10;
        cache.cache(new SampleDataSource(size).load(), keyType);
        
        @SuppressWarnings("unchecked")
        final IndexService<Long, Membership, SigleMethodSnapShot> indexService = context.getBean(IndexService.class);        
        indexService.index(cache, false, keyType , SigleMethodSnapShot.class);
        
        final Indexer<Long> indexer = cache.getIndexer("findByMemberNumberAndTfnForEmployer");
        assertNotNull(indexer);
        
        assertEquals(indexer.getName(), "findByMemberNumberAndTfnForEmployer");
        assertEquals(indexer.getQueryParams().length, 2);
        
        final int first = 0;
        final Property firstProperty = indexer.getQueryParams()[first];
        
        assertEquals(firstProperty.value(), "memberNumber");
        assertEquals(firstProperty.on(), "");
        
        final int second = 1;
        final Property secondProperty = indexer.getQueryParams()[second];
        
        assertEquals(secondProperty.value(), "superFundGenEmprId");
        assertEquals(secondProperty.on(), "");
    }
    
    
    @Test
    public void testCacheForSingleMethodButChainedPropertiesIndexing() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final int size = 10;
        cache.cache(new SampleDataSource(size).load(), keyType);
        
        @SuppressWarnings("unchecked")
        final IndexService<Long, Membership, SigleMethodJoinPropertySnapShot> indexService = context.getBean(IndexService.class);        
        indexService.index(cache, false, keyType , SigleMethodJoinPropertySnapShot.class);
        
        final Indexer<Long> indexer = cache.getIndexer("findByMemberNumberAndTfnAndDob");
        assertNotNull(indexer);
        
        assertEquals(indexer.getName(), "findByMemberNumberAndTfnAndDob");
        assertEquals(indexer.getQueryParams().length, 2);
        
        final int first = 0;
        final Property firstProperty = indexer.getQueryParams()[first];
        
        assertEquals(firstProperty.value(), "memberNumber");
        assertEquals(firstProperty.on(), "");
        
        final int second = 1;
        final Property secondProperty = indexer.getQueryParams()[second];
        
        assertEquals(secondProperty.value(), "dob");
        assertEquals(secondProperty.on(), "employee");
    }
}
