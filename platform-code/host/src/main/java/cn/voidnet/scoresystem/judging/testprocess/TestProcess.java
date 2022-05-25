package cn.voidnet.scoresystem.judging.testprocess;

import cn.voidnet.scoresystem.test.TestPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestProcess {
    @Id
    @GeneratedValue
    Long id;

    @OneToOne
    TestPoint test;

    @Enumerated(EnumType.STRING)
    TestState state;


    String rejectReason;
}







