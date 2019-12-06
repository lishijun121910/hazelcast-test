package jim.net.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class HazelcastConfiguration {
	
	@Bean
	public HazelcastInstance hazelcastClientCase(){
		return HazelcastClient.newHazelcastClient();
	}
	
	
}
