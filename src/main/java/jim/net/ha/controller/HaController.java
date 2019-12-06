package jim.net.ha.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.client.HazelcastClientNotActiveException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.spi.exception.TargetDisconnectedException;

import jim.net.statistics.Statistics;
import jim.net.tast.ITask;
import jim.net.tast.TaskExecutor;
import jim.net.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/ha")
public class HaController {
	
	private static final Logger log = LoggerFactory.getLogger(HaController.class);

	@Autowired
	@Qualifier("hazelcastClientCase")
	private HazelcastInstance hzClientInstance;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private Statistics statistics;

	private IMap<String, String> iMap;

	private static boolean running = false;

	private static int THREAD_NUM = 10;

	private static int VALUE_LENGTH = 1024;

	private static String SUCCESS_KEY = "SUCCESS";

	private static String FAIL_KEY = "FAIL";

	private static String MAP_NAME = "SimpleMap";

	private AtomicLong valueIndex = new AtomicLong(0);
	
//	private static int MAX_SIZE = 500000;
	private int MAX_SIZE;

	@RequestMapping("/begin")
	public String begin(String maxSize) {
		if (running)
			return "running, stop first";
		
		
		initTest(maxSize);

		StringBuilder result = new StringBuilder();
		
		try {
			log.info("HA test begin ... ");

			for (int i = 0; i < THREAD_NUM; i++) {
				taskExecutor.executeImmediately(new ITask() {
					@Override
					public void run() throws Exception {
						genValueAndPut();
					}
				});
			}

			result.append("HA test begin ... finish ");

		} catch (Exception e) {
			result.append("HA test begin ... fail cause:").append(e.getMessage());
			log.error("HA test begin ... fail.", e);
		} finally {
			log.info("{}", result);
		}
		return result.toString();
	}

	private void initTest(String maxSize) {

		iMap = hzClientInstance.getMap(MAP_NAME);
		iMap.clear();
		running = true;
		MAX_SIZE = Integer.parseInt(maxSize);
		
	}

	private void genValueAndPut() {
		boolean isProcessSuccess = false;
		while (true) {
			isProcessSuccess = false;
			if (!running)
				break;
			long nowIndex = genValue();
			String value = StringUtils.leftPad(String.valueOf(nowIndex), VALUE_LENGTH, "0");
			String key = String.valueOf(nowIndex % MAX_SIZE);
			long start = 0;
			long end = 0;
			try {
				try {
					start = System.currentTimeMillis();
					iMap.put(key, value);
					end = System.currentTimeMillis();
					statistics.accumulate(SUCCESS_KEY, 1, end - start);
					isProcessSuccess = true;
				} catch (Exception e) {
					end = System.currentTimeMillis();
					isProcessSuccess = false;
					throw e;
				}
			} catch (Exception e) {
				statistics.accumulate(FAIL_KEY, 1, end - start);
				String errMsg;
				if( e instanceof HazelcastClientNotActiveException ){
					errMsg = String.format("key:[%s] value:[%d] operation failed ，cause: Client is not Active",key,Long.parseLong(value));
				}else if( e instanceof OperationTimeoutException ){
					errMsg = String.format("key:[%s] value:[%d] operation failed ，cause: Operation Timeout",key,Long.parseLong(value));
				}else if( e instanceof TargetDisconnectedException){
					errMsg = String.format("key:[%s] value:[%d] operation failed ，cause: Connection closed by the other side",key,Long.parseLong(value));
				}else{
				    errMsg = String.format("key:[%s] value:[%d] operation failed ，cause:%s", key, Long.parseLong(value), e.getMessage());
				}
				log.error(errMsg);
			}
		}

	}

	private long genValue() {
		return valueIndex.getAndIncrement();
	}

	@RequestMapping("/stop")
	public String stop() {
		if (!running)
			return "not running ";

		running = false;
		return "stop ... finished";
	}

}
