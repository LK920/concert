package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.KafkaTestContainersConfig;
import kr.hhplus.be.server.domain.reservation.eventHandler.ReservationEventConsumer;
import kr.hhplus.be.server.domain.reservation.events.ReservationCompletedEvent;
import kr.hhplus.be.server.infra.external.kafka.KafkaMessageProducer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class KafkaReservationEventTest {

    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    @MockitoSpyBean
    private ReservationEventConsumer reservationEventConsumer;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry){
        registry.add("spring,kafka.bootstrap-servers", KafkaTestContainersConfig::getBootstrapServers);
    }

    @BeforeAll
    static void printKafkaInfo() {
        System.out.println("🟢 Kafka Testcontainer Bootstrap Server: " + KafkaTestContainersConfig.getBootstrapServers());
    }

    @Test
    void testKafkaEventProducedAndConsumed() {
        // Given
        ReservationCompletedEvent event = new ReservationCompletedEvent(1L, 10L, 100L, 1000L);

        // When
        kafkaMessageProducer.sendReservationCompletedEvent(event);

        // Then
        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(reservationEventConsumer, atLeastOnce())
                            .consume(any(ReservationCompletedEvent.class), any(Acknowledgment.class));
                });
    }


}
