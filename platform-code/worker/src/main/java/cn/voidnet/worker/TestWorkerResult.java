
package cn.voidnet.worker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestWorkerResult {
    ResultType resultType;//消息类型
    Long testPointId;//测试点通过和拒绝应给出测试点id
    Long judgingId;//评测会话id
    String reason;//错误原因
    String workerName;//评测机名称，用于定位错误
}







