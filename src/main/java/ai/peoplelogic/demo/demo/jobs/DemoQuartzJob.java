package ai.peoplelogic.demo.demo.jobs;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Component
public class DemoQuartzJob implements Job, Serializable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HazelcastInstance hazelcastInstance;

    public void execute(JobExecutionContext context) {
        log.info("Job execution started.");

        try {
            Scheduler scheduler = context.getScheduler();
            List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext job : jobs) {
                if (job.getTrigger().equals(context.getTrigger()) && job.getJobDetail() != context.getJobDetail()) {
                    log.warn("Ignored fetch job because another is running on this cluster.");
                    return;
                }
            }

            executeJobOnCluster();
        } catch (SchedulerException e) {
            log.error("Error executing scheduler: '(", e);
        }
    }

    private void executeJobOnCluster() {
        IExecutorService executorService = hazelcastInstance.getExecutorService("scaleable-task-demo");

        // Execute 100 tasks
        for (int i = 0; i < 100; i++) {
            DemoTask task = new DemoTask();
            executorService.submit(task, new ExecutionCallback<Map<String, Object>>() {
                public void onResponse(Map<String, Object> incomingResponse) {
                    if (incomingResponse.size() > 0 && incomingResponse.containsKey("name")) {
                        System.out.println("Received response on task: " + incomingResponse.get("name"));
                    }
                }

                public void onFailure(Throwable t) {
                    log.error("Error getting distributed task result", t);
                }
            });
        }
    }
}
