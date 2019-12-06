package jim.net.statistics;

import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;

@Data
public class TotalStatistics {
    private String operation;
    private long count;
    private double min;
    private double max;
    private double total;
    private double average;
    private double less1;
    private double less2;
    
    transient private ReentrantLock lock;

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("统计结果:\n");
        buf.append("operation:"+operation);
        buf.append("\n");
        buf.append("count:"+count);
        buf.append("\n");
        buf.append("min:"+min);
        buf.append("\n");
        buf.append("max:"+max);
        buf.append("\n");
        buf.append("total:"+total);
        buf.append("\n");
        buf.append("average:"+average);
        buf.append("\n");
        buf.append("qps:"+ (count/total)*1000);
        buf.append("\n");

        DecimalFormat df = new DecimalFormat("0.00%");     
        Double less1pst = less1/count;
        Double less2pst = less2/count;
        buf.append("less then 1 milliseconds:"+df.format(less1pst));
        buf.append("\n");
        buf.append("less then 2 milliseconds:"+df.format(less2pst));
        
        return buf.toString();
    }

    public TotalStatistics(String operation, ReentrantLock lock) {
        this.lock = lock;

        this.operation = operation;
        count = 0;
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
            if (cost < min)
                min = cost;
            if (cost > max)
                max = cost;
            total += cost;
            if (this.count > 0)
                average = total / this.count;
            if(cost<=1){
            	less1+=count;
            }
            if(cost<=2){
            	less2+=count;
            }
        } finally {
            if (lock != null)
                lock.unlock();
        }
    }
}
