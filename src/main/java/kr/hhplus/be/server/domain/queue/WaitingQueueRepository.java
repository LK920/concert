package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.infra.queue.WaitingQueueRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaitingQueueRepository extends JpaRepository<WaitingQueue, Long>, WaitingQueueRepositoryCustom {}
