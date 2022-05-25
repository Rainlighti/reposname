package cn.voidnet.scoresystem.judging.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDAO extends JpaRepository<Event, Long> {
    @Query(value="select * from event where dtype=:type",nativeQuery = true)
    Event findByType(String type);

}
