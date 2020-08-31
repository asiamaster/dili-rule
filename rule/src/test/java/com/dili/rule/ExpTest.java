package com.dili.rule;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;

import com.udojava.evalex.AbstractLazyFunction;
import com.udojava.evalex.Expression;
import com.udojava.evalex.Expression.LazyNumber;

import org.junit.jupiter.api.Test;

public class ExpTest {
    @Test
    public void test() {
        Expression e = new Expression("IF((diffMinute(_start,now())-5)<=0,10,10+(diffMinute(_start,now())-5)*5/2)");
        e.addLazyFunction(new AbstractLazyFunction("diffMinute", 2) {
            @Override
            public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
            //   /  lazyParams.get(0).eval()
       
                LocalDateTime firstArg = 
                LocalDateTime.ofEpochSecond(lazyParams.get(0).eval().longValue()/1000,0,OffsetDateTime.now().getOffset());
                LocalDateTime secondArg = LocalDateTime.ofEpochSecond(lazyParams.get(1).eval().longValue()/1000,0,OffsetDateTime.now().getOffset());
                long minutes = Duration.between(firstArg, secondArg).toMinutes();
                System.out.println(firstArg.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                System.out.println(secondArg.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                System.out.println(minutes);
                return new LazyNumber() {
                    public BigDecimal eval() {
                        return new BigDecimal(minutes);
                    }

                    public String getString() {
                        return String.valueOf(minutes);
                    }
                };
            }

        });
        e.with("_start", new LazyNumber() {
            LocalDateTime obj = LocalDateTime.parse("2020-07-12 16:19:12",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            public BigDecimal eval() {
                return new BigDecimal(obj.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
            }
            public String getString() {
                return String.valueOf("2020-07-12 16:19:12");
            }
        });
        // e.with("start", "2020-07-12 16:19:12");
        e.addLazyFunction(new AbstractLazyFunction("now", 0) {
            LocalDateTime now = LocalDateTime.now();
            @Override
            public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
                return new LazyNumber() {
                    public BigDecimal eval() {
                        return new BigDecimal(now.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                    }

                    public String getString() {
                        return String.valueOf(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                };
            }

        });
        e.with("a", new BigDecimal(10));

        System.out.println(e.eval().toPlainString());
        // BigDecimal result = new Expression("(a==b)?10:30").with("a", new
        // BigDecimal(new Date().getHours()))
        // .and("b", new BigDecimal(new Date().getHours())).eval(); // 9.5591845
        // System.out.println(result.toPlainString());
    }
}