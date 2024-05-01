package notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Queues {

  public static final String queueName = "email";

  @Bean
  public Queue emailQueue() {
    return new Queue(queueName);
  }
}
