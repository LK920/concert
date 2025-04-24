package kr.hhplus.be.server.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/*
* Configuration => 스프링에 설정 클래스임을 나타내며 스프링 컨테이너에 의해 관리되는 빈을 정의할 수 있음
* */
@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

    /*
    * PersistenceContext => jpa의 EntityManager를 주입받기 위해 사용,
    * EntityManager => JPA에서 데이터베이스와의 상호작용을 관리하는 클래스
    */
    @PersistenceContext
    private final EntityManager em;

    /*
    * JPAQueryFactory => QueryDsl의 jpa 쿼리 생성을 위한 클래스
    * */
    @Bean
    public JPAQueryFactory jpaFactory(){
        return new JPAQueryFactory(em);
    }
}
