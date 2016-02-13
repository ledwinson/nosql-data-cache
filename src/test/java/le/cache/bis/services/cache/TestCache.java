package le.cache.bis.services.cache;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import le.cache.bis.services.Cache;
import le.cache.bis.services.impl.PrimaryKeyType;
import le.cache.bis.services.impl.TestConfig;
import le.cache.bis.services.impl.data.Membership;
import le.cache.bis.services.impl.data.SampleDataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TestCache {

    @Autowired
    private Cache<Long, Membership> cache;

    @Test
    public void testCache() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final int size = 1;
        cache.cache(new SampleDataSource(size).load(), keyType);
        
        Assert.assertTrue(cache.getData().size() == size);
        Assert.assertFalse(cache.isEmpty());
        
        final Membership memberToMatch = SampleDataSource.getMember(size);
        
        Assert.assertNotNull(cache.get(memberToMatch.getMemberId()));
        Assert.assertFalse(cache.isInvalidated());
        
        cache.release();
        
        Assert.assertTrue(cache.isInvalidated());        
    } 
    
}
