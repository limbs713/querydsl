package study.querydsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static study.querydsl.entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void setUp(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10, teamA);
        Member member2 = new Member("member2",20, teamA);

        Member member3 = new Member("member3",30, teamB);
        Member member4 = new Member("member4",40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL(){
        //member1을 찾아라

        Member findByJpql = em.createQuery("SELECT m "
                        + "FROM Member m "
                        + "where m.name = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertEquals(findByJpql.getName(),"member1");
    }

    @Test
    public void startQuerydsl(){
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.name.eq("member1"))
                .fetchOne();

        assertEquals(findMember.getName(),"member1");
    }

    @Test
    public void search()
    {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.name.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertEquals(findMember.getName(),"member1");
    }

    @Test
    public void searchAndParam()
    {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.name.eq("member1"),
                        member.age.between(10,30)
                )
                .fetchOne();

        assertEquals(findMember.getName(),"member1");
    }
}
