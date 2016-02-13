/**
 * 
 */
package le.cache.bis.services.impl;



import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import le.cache.bis.services.CacheService;
import le.cache.bis.services.exception.MultipleElementsInResultSetException;
import le.cache.bis.services.exception.SnapshotException;
import le.cache.bis.services.impl.PrimaryKeyType;
import le.cache.bis.services.impl.data.Membership;
import le.cache.bis.services.impl.data.SampleDataSource;
import le.cache.bis.services.impl.data.SampleDataSourceForDuplicates;
import le.cache.bis.services.impl.data.SampleDataSourceWithLinkedEntities;
import le.cache.bis.services.impl.query.TestSnapshot;

/**
 * @author ledwinson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TestCacheService {
    
    private static final int TEN = 10;

    @Autowired
    private CacheService<Membership, TestSnapshot, Long> cacheService;
    
    @Test
    public void testCacheForNormalBehaviour() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final int hundred = 100;
        cacheService.cache(new SampleDataSource(hundred ), false, keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();

        for(int i = 1; i < hundred; i++) {
            final Membership memberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+i, String.valueOf(1 + i));
            final Membership memberToCompare = SampleDataSource.getMember(i);

            Assert.assertNotNull(memberFromCache);
            compare(memberFromCache, memberToCompare);
        }
        
        //do was null and hence none might have cached for this.
        Assert.assertNull(snapshot.findByMemberNumberAndTfnAndDob("MemNumber1", String.valueOf(1), new Date()));        
        Assert.assertEquals(Integer.valueOf(hundred), snapshot.size());
        //release snapshot after done with it.
        snapshot.release();
        Assert.assertEquals(Integer.valueOf(0), snapshot.size());
    }
    
    @Test
    public void testCacheForDuplicatesWithQueryExpectingExactlyOneResult() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);        
        cacheService.cache(new SampleDataSourceForDuplicates(TEN), false, keyType , TestSnapshot.class);
        
        final TestSnapshot snapshot = cacheService.getSnapshot();
        try{
            snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));
        }catch(MultipleElementsInResultSetException e) {
            Assert.assertTrue("Expected exception here for incorrect data size.", true);
            snapshot.release();
            return;
        }        
        Assert.assertFalse("Conroll is not expected to come here.", true);
    }
    
    @Test
    public void testCacheForDuplicatesWithPropertQueryExpectingMultipleResults() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        cacheService.cache(new SampleDataSourceForDuplicates(TEN), false, keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();
        final List<Membership> memberList = snapshot.findByMemberNumberAndTfnForEmployerForDuplicates("MemNumber"+1, String.valueOf(2));        
        Assert.assertEquals(memberList.size(), TEN);
        
        final Membership memberToCompare = SampleDataSourceForDuplicates.getMember(1);
        
        for(Membership memberFromCache: memberList) {
            compare(memberFromCache, memberToCompare);
        }
        snapshot.release();
    }
    
    @Test
    public void testNonCaseSensitiveSearch() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = false;
        cacheService.cache(new SampleDataSource(1), isCaseSensitive , keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();
        
        final Membership memberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));
        final Membership memberToCompare = SampleDataSource.getMember(1);
        Assert.assertNotNull(memberFromCache);
        compare(memberFromCache, memberToCompare);

        final Membership memberFromCache2 = snapshot.findByMemberNumberAndTfnForEmployer("memnumber"+1, String.valueOf(2));
        Assert.assertNotNull(memberFromCache2);
        compare(memberFromCache2, memberToCompare);
        
        final Membership memberFromCache3 = snapshot.findByMemberNumberAndTfnForEmployer("memnumBER"+1, String.valueOf(2));
        Assert.assertNotNull(memberFromCache3);
        compare(memberFromCache3, memberToCompare);
        
        snapshot.release();
    }
    
    
    @Test
    public void testCaseSensitiveSearch() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = true;
        cacheService.cache(new SampleDataSource(1), isCaseSensitive , keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();
        
        final Membership memberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));
        final Membership memberToCompare = SampleDataSource.getMember(1);
        Assert.assertNotNull(memberFromCache);
        compare(memberFromCache, memberToCompare);

        final Membership memberFromCache2 = snapshot.findByMemberNumberAndTfnForEmployer("memnumber"+1, String.valueOf(2));
        Assert.assertNull(memberFromCache2);
                
        final Membership memberFromCache3 = snapshot.findByMemberNumberAndTfnForEmployer("memnumBER"+1, String.valueOf(2));
        Assert.assertNull(memberFromCache3);
        
        snapshot.release();
    }
    
    @Test
    public void testQueryWithPropertiesInEmployeeEntityThatIsLinkedWithMemberEntity() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = true;
        cacheService.cache(new SampleDataSourceWithLinkedEntities(), isCaseSensitive , keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();
        
        final Membership memberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+2, String.valueOf(3));
        final Membership memberToCompare = SampleDataSourceWithLinkedEntities.getMember(2, null);
        Assert.assertNotNull(memberFromCache);
        Assert.assertNull(memberFromCache.getEmployee());
        compare(memberFromCache, memberToCompare);

        
        
        final Date dob = new Date();
        
        final Membership memberFromCacheWithDateComparison = snapshot.findByMemberNumberAndTfnAndDob("MemNumber"+1, String.valueOf(2), dob);
        Assert.assertNotNull(memberFromCacheWithDateComparison);
        Assert.assertNotNull(memberFromCacheWithDateComparison.getEmployee());
        
        final Membership memberToCompareWithDate = SampleDataSourceWithLinkedEntities.getMember(1, dob);
        compare(memberFromCacheWithDateComparison, memberToCompareWithDate);
        
        //Assert.assertEquals(memberFromCacheWithDateComparison.getPvsEmployerEmployee().getDob(), memberToCompareWithDate.getEmployee().getDob());
        
        snapshot.release();
    }
    
    
    @Test
    public void testAccessingElementsFromCacheAfterReleasingTheSnapshot() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = true;
        cacheService.cache(new SampleDataSource(1), isCaseSensitive , keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();
        
        final Membership memberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));
        final Membership memberToCompare = SampleDataSource.getMember(1);
        Assert.assertNotNull(memberFromCache);
        compare(memberFromCache, memberToCompare);
        
        snapshot.release(); //I am done with it
        
        try{
            snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));
        }catch(SnapshotException exception) {            
            Assert.assertTrue("Exception because the cache is invalidated already and trying to acces any elements again will throw exception.", true);
            return;
        }        
        Assert.assertFalse("Conroll is not expected to come here.", true);        
    }
    
    
    
    @Test
    public void testAccessingSnapshotAgainFromCacheServiceAfterReleasingTheSnapshot() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = true;
        cacheService.cache(new SampleDataSource(1), isCaseSensitive , keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();
        
        final Membership memberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));
        final Membership memberToCompare = SampleDataSource.getMember(1);
        Assert.assertNotNull(memberFromCache);
        compare(memberFromCache, memberToCompare);
        
        snapshot.release(); //I am done with it
        
        try{
            cacheService.getSnapshot();
        }catch(SnapshotException exception) {
            Assert.assertTrue("Exception because the cache is invalidated already and trying to access snapshot again will throw exception.", true);
            return;
        }
        Assert.assertFalse("Conroll is not expected to come here.", true);
        
    }
    
    
    
    @Test
    public void testCachingANewElementAfterIntiallyLoadingAllTheElements() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = true;
        cacheService.cache(new SampleDataSource(1), isCaseSensitive , keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();
        
        final Membership memberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));
        final Membership memberToCompare = SampleDataSource.getMember(1);
        Assert.assertNotNull(memberFromCache);
        compare(memberFromCache, memberToCompare);                
        
        //add a new Element to cache.
        cacheService.cache(SampleDataSource.getMember(2), isCaseSensitive, keyType, TestSnapshot.class);
        final Membership newlyAddedMemberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+2, String.valueOf(3));
        final Membership newMemberToCompare = SampleDataSource.getMember(2);
        Assert.assertNotNull(newlyAddedMemberFromCache);
        compare(newlyAddedMemberFromCache, newMemberToCompare);
        
        snapshot.release(); //I am done with it
    }
    
    @Test
    public void testCachingANewElementWithoutIntiallyLoadingAllTheElements() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = true;
                        
        //add a new Element to cache.
        cacheService.cache(SampleDataSource.getMember(2), isCaseSensitive, keyType, TestSnapshot.class);
                
        final TestSnapshot snapshot = cacheService.getSnapshot();        
        final Membership newlyAddedMemberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+2, String.valueOf(3));
        final Membership newMemberToCompare = SampleDataSource.getMember(2);
        Assert.assertNotNull(newlyAddedMemberFromCache);
        compare(newlyAddedMemberFromCache, newMemberToCompare);
        
        snapshot.release(); //I am done with it
    }
    
    @Test
    public void testRemovingAnElementFromCache() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = true;
                        
        //add a new Element to cache.
        cacheService.cache(SampleDataSource.getMember(2), isCaseSensitive, keyType, TestSnapshot.class);
                
        final TestSnapshot snapshot = cacheService.getSnapshot();        
        final Membership newlyAddedMemberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+2, String.valueOf(3));
        final Membership newMemberToCompare = SampleDataSource.getMember(2);
        Assert.assertNotNull(newlyAddedMemberFromCache);
        
        compare(newlyAddedMemberFromCache, newMemberToCompare);
        
        //now remove the element from cache.
        cacheService.removeElement(SampleDataSource.getMember(2), keyType);
        
        final Membership elementAfterRemovingItFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+2, String.valueOf(3));
        
        Assert.assertNull(elementAfterRemovingItFromCache);
        
        snapshot.release(); //I am done with it
    }
    
    
    @Test
    public void testUpdatingAnExistingElementInCache() {
        final PrimaryKeyType<Long> keyType = new PrimaryKeyType<Long>(Long.class, "memberId", Membership.class);
        final boolean isCaseSensitive = true;
        cacheService.cache(new SampleDataSource(1), isCaseSensitive , keyType , TestSnapshot.class);

        final TestSnapshot snapshot = cacheService.getSnapshot();
        
        final Membership memberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));
        final Membership memberToCompare = SampleDataSource.getMember(1);
        Assert.assertNotNull(memberFromCache);
        compare(memberFromCache, memberToCompare);                
        
        //add a new Element to cache.
        
        final String updatingFinToNewVlaue = "670";
        memberFromCache.setSuperFundGenEmprId(updatingFinToNewVlaue);
        cacheService.cache(memberFromCache, isCaseSensitive, keyType, TestSnapshot.class);
        
        final Membership tryToGetOldMemberWithOldDetails = cacheService.getSnapshot().findByMemberNumberAndTfnForEmployer("MemNumber"+1, String.valueOf(2));        
        Assert.assertNull(tryToGetOldMemberWithOldDetails); // the old value is replaced and re-indexed
        
        
        final Membership updatedMemberFromCache = snapshot.findByMemberNumberAndTfnForEmployer("MemNumber"+1, updatingFinToNewVlaue);
        Assert.assertNotNull(updatedMemberFromCache);
        
        final Membership newMemberToCompare = SampleDataSource.getMember(1);
        newMemberToCompare.setSuperFundGenEmprId(updatingFinToNewVlaue);
        
        compare(updatedMemberFromCache, newMemberToCompare);
        
        snapshot.release(); //I am done with it
    }
    
    private void compare(Membership memberFromCache, Membership memberToCompare) {
        Assert.assertEquals(memberToCompare.getMemberNumber(), memberFromCache.getMemberNumber());
        Assert.assertEquals(memberToCompare.getSuperFundGenEmprId(), memberFromCache.getSuperFundGenEmprId());
    }
}
