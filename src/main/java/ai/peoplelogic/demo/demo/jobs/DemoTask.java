package ai.peoplelogic.demo.demo.jobs;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.spring.context.SpringAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@SpringAware
public class DemoTask implements HazelcastInstanceAware, Callable<Map<String, Object>>, Serializable, ApplicationContextAware {
    transient HazelcastInstance hazelcastInstance;
    transient ApplicationContext context;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public DemoTask() {
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public Map<String, Object> call() throws Exception {
        // Here you would do some really fancy logic.  We're just going to sleep for 10 seconds and store some values
        TimeUnit.SECONDS.sleep(10);

        log.info("Executing task");

        Map<String, Object> result = new HashMap<>();
        result.put("name", "Demo Task " + hazelcastInstance.getCluster().getLocalMember().getUuid());
        result.put("success", true);
        return result;
    }
}
