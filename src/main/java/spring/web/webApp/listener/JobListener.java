package spring.web.webApp.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import spring.web.webApp.mail.EmailUtil;

public class JobListener implements JobExecutionListener {

    @Autowired
    private final EmailUtil emailUtil;

    public JobListener(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        emailUtil.sendEmail("sudhirlthr@gmail.com", jobName+": is starting", "This is body");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        emailUtil.sendEmail("sudhirlthr@gmail.com", jobName+": has finished", "This is body");
    }
}
