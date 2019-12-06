package jim.net.statistics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class Statistics {

    final static private int MERGE_INTERVAL = 1000;
    
    private Map<String, StatData> map = new ConcurrentHashMap<String, StatData>();

    private Timer mergeTimer;

    private ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        mergeTimer = new Timer(true);
        mergeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (map.size() > 0) {
                        Map<String, StatData> copy = new HashMap<String, StatData>(map.size());
                        copy.putAll(map);
                        map.clear();

                        mergeData(copy);
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }, MERGE_INTERVAL, MERGE_INTERVAL);
    }

    private void mergeData(Map<String, StatData> copy) {
        lock.lock();
        try {
        	StringBuffer sb = new StringBuffer();
            for (StatData data : copy.values()) {
                try {
                	sb.append("operationType: "+data.getEvent());
                	sb.append("  requests: "+ data.getCount());
                	sb.append("  average: "+data.getAverage());
                	sb.append("  max: "+data.getMax());
                	sb.append("  min: "+data.getMin());
                	sb.append("\n");
                }catch (Exception e){
                    log.error("Collect stat error: ", e);
                }
    			log.info("statistic:{}", JSON.toJSONString(data, SerializerFeature.UseISO8601DateFormat));
            }
//        	log.info(sb.toString());
        }finally {
            lock.unlock();
        }
    }


    @PreDestroy
    public void destroy() {
        mergeTimer.cancel();
    }


    public void accumulate(String event, long count, double cost) {
        try {
            StatData c = map.get(event);
            if (c == null) {
                c = new StatData(event, new ReentrantLock());
                map.putIfAbsent(event, c);
                c = map.get(event);
            }
            c.accumulate(count, cost);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
