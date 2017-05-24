package com.alicp.jetcache.support;

import com.alicp.jetcache.*;
import com.alicp.jetcache.embedded.LinkedHashMapCacheBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2016/11/1.
 *
 * @author <a href="mailto:yeli.hl@taobao.com">huangli</a>
 */
public class DefaultCacheMonitorTest {

    public Cache createCache() {
        return LinkedHashMapCacheBuilder.createLinkedHashMapCacheBuilder()
                .limit(10)
                .expireAfterWrite(200, TimeUnit.SECONDS)
                .buildCache();
    }

    private static void basetest(Cache cache, DefaultCacheMonitor m) {
        CacheStat oldStat = m.getCacheStat().clone();
        cache.get("MONITOR_TEST_K1");
        Assert.assertEquals(oldStat.getGetCount() + 1, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetMissCount() + 1, m.getCacheStat().getGetMissCount());

        oldStat = m.getCacheStat().clone();
        cache.put("MONITOR_TEST_K1", "V1");
        Assert.assertEquals(oldStat.getPutCount() + 1, m.getCacheStat().getPutCount());

        oldStat = m.getCacheStat().clone();
        cache.get("MONITOR_TEST_K1");
        Assert.assertEquals(oldStat.getGetCount() + 1, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetHitCount() + 1, m.getCacheStat().getGetHitCount());

        oldStat = m.getCacheStat().clone();
        cache.remove("MONITOR_TEST_K1");
        Assert.assertEquals(oldStat.getRemoveCount() + 1, m.getCacheStat().getRemoveCount());

        oldStat = m.getCacheStat().clone();
        cache.computeIfAbsent("MONITOR_TEST_K1", (k) -> null);
        Assert.assertEquals(oldStat.getGetCount() + 1, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetMissCount() + 1, m.getCacheStat().getGetMissCount());
        Assert.assertEquals(oldStat.getPutCount(), m.getCacheStat().getPutCount());
        Assert.assertEquals(oldStat.getLoadCount() + 1, m.getCacheStat().getLoadCount());
        Assert.assertEquals(oldStat.getLoadSuccessCount() + 1, m.getCacheStat().getLoadSuccessCount());

        oldStat = m.getCacheStat().clone();
        cache.computeIfAbsent("MONITOR_TEST_K1", (k) -> null, true);
        Assert.assertEquals(oldStat.getGetCount() + 1, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetMissCount() + 1, m.getCacheStat().getGetMissCount());
        Assert.assertEquals(oldStat.getPutCount() + 1, m.getCacheStat().getPutCount());
        Assert.assertEquals(oldStat.getLoadCount() + 1, m.getCacheStat().getLoadCount());
        Assert.assertEquals(oldStat.getLoadSuccessCount() + 1, m.getCacheStat().getLoadSuccessCount());

        oldStat = m.getCacheStat().clone();
        cache.get("MONITOR_TEST_K1");
        Assert.assertEquals(oldStat.getGetCount() + 1, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetHitCount() + 1, m.getCacheStat().getGetHitCount());

        oldStat = m.getCacheStat().clone();
        cache.remove("MONITOR_TEST_K1");
        Assert.assertEquals(oldStat.getRemoveCount() + 1, m.getCacheStat().getRemoveCount());

        oldStat = m.getCacheStat().clone();
        cache.computeIfAbsent("MONITOR_TEST_K2", (k) -> null, false, 10, TimeUnit.SECONDS);
        Assert.assertEquals(oldStat.getGetCount() + 1, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetMissCount() + 1, m.getCacheStat().getGetMissCount());
        Assert.assertEquals(oldStat.getPutCount(), m.getCacheStat().getPutCount());
        Assert.assertEquals(oldStat.getLoadCount() + 1, m.getCacheStat().getLoadCount());
        Assert.assertEquals(oldStat.getLoadSuccessCount() + 1, m.getCacheStat().getLoadSuccessCount());

        oldStat = m.getCacheStat().clone();
        cache.computeIfAbsent("MONITOR_TEST_K2", (k) -> null, true, 10, TimeUnit.SECONDS);
        Assert.assertEquals(oldStat.getGetCount() + 1, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetMissCount() + 1, m.getCacheStat().getGetMissCount());
        Assert.assertEquals(oldStat.getPutCount() + 1, m.getCacheStat().getPutCount());
        Assert.assertEquals(oldStat.getLoadCount() + 1, m.getCacheStat().getLoadCount());
        Assert.assertEquals(oldStat.getLoadSuccessCount() + 1, m.getCacheStat().getLoadSuccessCount());

        oldStat = m.getCacheStat().clone();
        cache.get("MONITOR_TEST_K2");
        Assert.assertEquals(oldStat.getGetCount() + 1, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetHitCount() + 1, m.getCacheStat().getGetHitCount());

        oldStat = m.getCacheStat().clone();
        cache.remove("MONITOR_TEST_K2");
        Assert.assertEquals(oldStat.getRemoveCount() + 1, m.getCacheStat().getRemoveCount());

        oldStat = m.getCacheStat().clone();
        Map map = new HashMap();
        map.put("MONITOR_TEST_multi_k1", "V1");
        map.put("MONITOR_TEST_multi_k2", "V2");
        cache.putAll(map);
        Assert.assertEquals(oldStat.getPutCount() + 2, m.getCacheStat().getPutCount());

        oldStat = m.getCacheStat().clone();
        HashSet keys = new HashSet(map.keySet());
        keys.add("MONITOR_TEST_multi_k3");
        cache.getAll(keys);
        Assert.assertEquals(oldStat.getGetCount() + 3, m.getCacheStat().getGetCount());
        Assert.assertEquals(oldStat.getGetHitCount() + 2, m.getCacheStat().getGetHitCount());
        Assert.assertEquals(oldStat.getGetMissCount() + 1, m.getCacheStat().getGetMissCount());

        oldStat = m.getCacheStat().clone();
        cache.removeAll(keys);
        Assert.assertEquals(oldStat.getRemoveCount() + 3, m.getCacheStat().getRemoveCount());
        Assert.assertEquals(oldStat.getRemoveCount() + 3, m.getCacheStat().getRemoveSuccessCount());
    }

    public static void testMonitor(Cache cache) {
        {
            // old style test
            DefaultCacheMonitor monitor = new DefaultCacheMonitor("Test");
            cache = new MonitoredCache(cache, monitor);
            basetest(cache, monitor);
        }
        {
            // new style test
            while (cache instanceof ProxyCache) {
                cache = ((ProxyCache) cache).getTargetCache();
            }
            List monitors = null;
            if (cache instanceof MultiLevelCache) {
                monitors = ((MultiLevelCache) cache).getMonitors();
            } else if (cache instanceof AbstractCache) {
                monitors = ((AbstractCache) cache).config().getMonitors();
            }
            monitors.clear();
            DefaultCacheMonitor monitor = new DefaultCacheMonitor("Test");
            monitors.add(monitor);
            basetest(cache, monitor);
        }
    }

    @Test
    public void testWithoutLogger() {
        Cache cache = createCache();
        testMonitor(cache);
    }

    @Test
    public void testWithManager() throws Exception {
        Cache c1 = createCache();
        DefaultCacheMonitor m1 = new DefaultCacheMonitor("cache1");
        Cache c2 = createCache();
        DefaultCacheMonitor m2 = new DefaultCacheMonitor("cache2");

        c1 = new MonitoredCache(c1, m1);
        c2 = new MonitoredCache(c2, m2);
        DefaultCacheMonitorManager manager = new DefaultCacheMonitorManager(2, TimeUnit.SECONDS);
        manager.start();
        manager.add(m1, m2);

        basetest(c1, m1);
        basetest(c2, m2);

        Cache mc = new MultiLevelCache(c1, c2);
        DefaultCacheMonitor mcm = new DefaultCacheMonitor("multiCache");
        mc = new MonitoredCache(mc, mcm);
        manager.add(mcm);
        basetest(mc, mcm);

        manager.stop();
//        Thread.sleep(10000);
    }
}
