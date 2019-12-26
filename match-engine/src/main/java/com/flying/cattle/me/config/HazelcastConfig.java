package com.flying.cattle.me.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;

@Configuration
public class HazelcastConfig {

	private String app_name = "match";

	// @Value("${spring.profiles.active}")
	private String app_active = "local";

	@Bean
	public Config config() {

		String configKey = app_name + app_active + "-config";
		String instanceKey = app_name + app_active + "-instance";
		String mapKey = app_name + app_active + "-config-map";

		Config config = new Config();
		GroupConfig gc = new GroupConfig(configKey);
		MapConfig mapConfig = new MapConfig();
		mapConfig.setName(mapKey)// 设置Map名称
				.setInMemoryFormat(InMemoryFormat.BINARY)// 设置内存格式
				.setBackupCount(1).setReadBackupData(true);// 默认从主库读写

		config.setInstanceName(instanceKey).addMapConfig(mapConfig).setGroupConfig(gc);
		return config;
	}

	@Bean
	public JetInstance jetInstance(Config config) {
		JetConfig jConfig = new JetConfig();
		jConfig.setHazelcastConfig(config);
		JetInstance jetInstance = Jet.newJetInstance(jConfig);
		return jetInstance;
	}

	@Bean
	public HazelcastInstance hzInstance(JetInstance jetInstance) {
		HazelcastInstance hzInstance = jetInstance.getHazelcastInstance();
		return hzInstance;
	}
}
