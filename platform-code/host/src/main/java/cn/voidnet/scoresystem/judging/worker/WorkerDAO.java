package cn.voidnet.scoresystem.judging.worker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerDAO extends JpaRepository<Worker, Long> {
    @Modifying
    @Query("update Worker w set w.online=true " +
            "where w.name=:name")
    void setOnline(String name);

    @Modifying
    @Query("update Worker w set w.online=false " +
            "where w.name=:name")
    void setOffline(String name);

    @Query("select w.online from Worker w where w.name=:name ")
    boolean isOnline(String name);

    List<Worker> findByOnlineEquals(Boolean isOnline);

    Optional<Worker> findByName(String name);
}







