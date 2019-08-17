package com.github.flycat.spi.alarm;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MetricTest {

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
        ConsoleReporter.forRegistry(registry).outputTo(System.out).build().start(
                1, TimeUnit.SECONDS
        );
        Thread.sleep(10000L);
    }
}
