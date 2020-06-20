package rule;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.udojava.evalex.Expression;


public class ExpressionTest {
	@Test
	public void test() {
		
			this.validateExpression();
	}
	public boolean validateExpression() {
		Expression expression = new Expression("sin(5)*(9-6)");
		try {
	        for (String var : expression.getUsedVariables()) {
	            expression.setVariable(var, "1");
	        }
	        BigDecimal value=expression.eval();
	        System.out.println(value.toPlainString());
	        return true;
	    } catch (Exception e) {
	      //throw new RuntimeException(e);
	    	return false;
	    }
	
	}
}
