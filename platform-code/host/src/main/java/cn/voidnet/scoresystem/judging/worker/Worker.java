package cn.voidnet.scoresystem.judging.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.openjpa.persistence.jdbc.Unique;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Worker {
    @Id
    @GeneratedValue
    Long id;

    @Unique
    String name;

    boolean online;

}









