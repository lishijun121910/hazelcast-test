/**
 * 
 */
package jim.net.tast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author lishijun
 *
 */
@Component
public class TaskExecutor {

	@Value("${thread.task.core_pool_size}")
	private int corePoolSize;

	@Value("${thread.task.max_pool_size}")
	private int maximumPoolSize;

	@Value("${thread.task.keep_alive_time_seconds}")
	private long keepAliveTimeSeconds;

	private ExecutorService executorService;
	
	private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);

	@PostConstruct
	public void start() {
		executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTimeSeconds, TimeUnit.SECONDS,
				new SynchronousQueue<>());
		log.info("****线程池初始化成功****");
		log.info("线程池最小:{}, 线程池最大:{} 空闲线程回收时间(单位:s):{}", corePoolSize, maximumPoolSize, keepAliveTimeSeconds);
	}

	@PreDestroy
	public void stop() {
		executorService.shutdown();
		log.info("****线程池关闭成功****");
	}

	public void executeImmediately(final ITask task) {
		try {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						task.run();
					} catch (Exception e) {
						log.error("task执行异常："+ e.getMessage());
					}
				}
			});
		} catch (Exception e) {
			log.error("执行任务时异常： " + e.getMessage());
		}
	}

}
