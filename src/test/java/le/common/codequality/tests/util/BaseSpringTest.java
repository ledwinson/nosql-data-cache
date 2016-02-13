package le.common.codequality.tests.util;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import le.cache.bis.services.impl.TestConfig;

/**
 * Extend this class for JUnit tests so that Spring will manage your test class - so services can be @Autowired, methods can be @Transactional and so on
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public abstract class BaseSpringTest {

}
