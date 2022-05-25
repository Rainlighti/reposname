package cn.voidnet.scoresystem.experiment;

import cn.voidnet.scoresystem.judging.judgingcode.JudgingCode;
import cn.voidnet.scoresystem.test.TestPoint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Experiment {
    @Id
    @GeneratedValue
    Long id;

    @Column(name = "order_")
            @NotBlank
    Integer order;

    @NotBlank
    String title;

    String intro;

    String documentLink;



    @JsonIgnore
    @OneToOne
    JudgingCode judgingCode;


    @OneToMany(cascade = {CascadeType.PERSIST,
            CascadeType.REMOVE,
            CascadeType.MERGE
    })
    List<TestPoint> tests;
}








