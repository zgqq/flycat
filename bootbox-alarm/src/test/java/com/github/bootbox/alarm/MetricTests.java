package com.github.bootbox.alarm;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.junit.Test;

public class MetricTests {

    @Test
    public void testMetric() throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        final Meter ok = registry.meter("ok");
        ok.mark();
//        Thread.sleep(2000L);
        final double oneMinuteRate = ok.getOneMinuteRate();
        System.out.println(oneMinuteRate);
        ok.mark();
        Thread.sleep(2000L);
        final double oneMinuteRate1 = ok.getOneMinuteRate();
        System.out.println(oneMinuteRate1);
        ok.mark();
        Thread.sleep(2000L);
        final double oneMinuteRate2 = ok.getOneMinuteRate();
        System.out.println(oneMinuteRate2);
        ok.mark(2);
        Thread.sleep(2000L);
        final double oneMinuteRate3 = ok.getOneMinuteRate();
        System.out.println(oneMinuteRate3);

    }
}
