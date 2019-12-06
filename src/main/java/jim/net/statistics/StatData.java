package jim.net.statistics;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class StatData implements Serializable {
    private String event;
    private long count;
    private long size;
    private double min;
    private double max;
    private double total;
    private double average;

    transient private ReentrantLock lock;

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(event);
        buf.append(" ");
        buf.append(count);
        buf.append(" ");
        buf.append(size);
        buf.append(" ");
        buf.append(min);
        buf.append(" ");
        buf.append(max);
        buf.append(" ");
        buf.append(total);
        buf.append(" ");
        buf.append(average);
        return buf.toString();
    }

    public StatData(String event, ReentrantLock lock) {
        this.lock = lock;

        this.event = event;
        count = 0;
        size = 0;
        min = 999999;
        max = 0;
        total = 0;
        average = 0;
    }

    public void accumulate(long count, double cost) {
        if (lock != null)
            lock.lock();
        try {
            this.count += count;
            this.size += 1;
            if (cost < min)
                min = cost;
            if (cost > max)
                max = cost;
            total += cost;
            if (this.count > 0)
                average = total / this.count;
        } finally {
            if (lock != null)
                lock.unlock();
        }
    }

    public void accumulate(StatData data) {
        if (lock != null)
            lock.lock();
        try {
            this.count += data.count;
            this.size += 1;
            if (min < 0.00000001 || data.min < min)
                min = data.min;
            if (data.max > max)
                max = data.max;
            total += data.total;
            if (this.count > 0)
                average = total / this.count;
        } finally {
            if (lock != null)
                lock.unlock();
        }
    }
}
