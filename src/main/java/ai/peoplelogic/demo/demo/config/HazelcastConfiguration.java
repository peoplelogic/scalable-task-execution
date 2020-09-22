package ai.peoplelogic.demo.demo.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.spring.context.SpringManagedContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static java.util.Collections.singletonList;

@Configuration
public class HazelcastConfiguration {

    @Bean
    public SpringManagedContext managedContext() {
        return new SpringManagedContext();
    }

    @Bean
    @Profile("local")
    public Config localConfig() {

        Config config = new Config();
        config.setInstanceName("scaleable-task-demo");
        config.setManagedContext(managedContext());
        config.getNetworkConfig().setPort(3000);
        config.getGroupConfig().setName("scaleable-task-demo");

        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true).setMembers(singletonList("127.0.0.1"));

        return config;
    }

    @Bean
    @Profile("prod")
    public Config awsConfig() {

        Config config = new Config();
        config.setInstanceName("scaleable-task-demo");
        config.setManagedContext(managedContext());
        config.getGroupConfig().setName("scaleable-task-demo");

        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getAwsConfig().setEnabled(true);

        /**
         * Shared instance IAM roles are preferred for locating other nodes in the cluster
         */
        //joinConfig.getAwsConfig().setEnabled(true).setProperty("access-key", hazelcastAccessKey).setProperty("secret-key", hazelcastAccessSecret);

        return config;
    }
}