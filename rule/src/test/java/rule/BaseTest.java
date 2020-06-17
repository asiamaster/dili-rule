package rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dili.rule.RuleApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = RuleApplication.class)
@ActiveProfiles("dev")
@WebAppConfiguration("src/main/resources")

@EnableTransactionManagement

@Rollback
@TestInstance(Lifecycle.PER_CLASS)
public class BaseTest {
	@MockBean
	ErrorAttributes ErrorAttributes;


	@Transactional(propagation = Propagation.REQUIRED)
	@Test
	public void test() {
		System.out.println("demo");
	}

}
