package cn.voidnet.scoresystem.judging;

import cn.voidnet.scoresystem.experiment.Experiment;
import cn.voidnet.scoresystem.judging.event.Event;
import cn.voidnet.scoresystem.judging.testprocess.TestProcess;
import cn.voidnet.scoresystem.judging.usercode.UserCode;
import cn.voidnet.scoresystem.share.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judging {
    @Id
    @GeneratedValue
    Long id;

    @OneToOne
    Experiment experiment;

    @ManyToOne
    User user;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    List<TestProcess> testProcesses;

    Long lastSubmitTime;

    @OneToMany(cascade = {CascadeType.REMOVE,CascadeType.PERSIST})
    List<Event> events;

    @OneToOne()
    Event currEvent;

    @OneToOne
    @JsonIgnore
    UserCode lastSubmitCode;

    @JsonInclude()
    String errorMessage;
    @JsonInclude()
    String compileWarning;
    @JsonInclude()
    String workerName;


}