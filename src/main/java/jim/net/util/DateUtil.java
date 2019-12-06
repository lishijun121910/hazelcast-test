/**
 * 
 */
package jim.net.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ethan
 *
 */
@Slf4j
public class DateUtil extends DateUtils {

	public static Date now() {
		return now(true, true, true, true);
	}

	public static Date now(boolean needHour, boolean needMinute, boolean needSecond, boolean needMills) {
		Calendar calendar = Calendar.getInstance();
		if (!needHour) {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
		}
		if (!needMinute) {
			calendar.set(Calendar.MINUTE, 0);
		}
		if (!needSecond) {
			calendar.set(Calendar.SECOND, 0);
		}
		if (!needMills) {
			calendar.set(Calendar.MILLISECOND, 0);
		}
		return calendar.getTime();
	}

	public static Date convertTimeMillis2Date(long timeMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		return calendar.getTime();
	}

	public static long diffTimeMillis(java.util.Date endDate, java.util.Date startDate) {
		if (endDate == null || startDate == null) {
			log.debug("获取时间比较的毫秒时,开始时间:[{}]或者结束时间:[{}]为空.", startDate, endDate);
			return Long.MAX_VALUE;
		}
		return endDate.getTime() - startDate.getTime();
	}

}
