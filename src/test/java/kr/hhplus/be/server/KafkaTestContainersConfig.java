package kr.hhplus.be.server;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

@Configuration
public class KafkaTestContainersConfig {

    private static final KafkaContainer kafkaContainer;

    static {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
        kafkaContainer.addExposedPorts(9092,9092);
        kafkaContainer.start();
    }

    public static String getBootstrapServers(){
        return kafkaContainer.getBootstrapServers();
    }

    public AdminClient getAdminClient(){
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        return AdminClient.create(props);
    }
}
