package cn.voidnet.scoresystem.judging.worker;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TestWorkerResult {
    public enum ResultType
    {
        COMPILE_ERROR,//编译错误
        JUDGE_ERROR,//评测时错误
        PASS,//测试点通过
        REJECT,//测试点被拒绝
        PENDING,//测试点开始运行
        BEGIN_JUDGE,//开始评测
        BEGIN_COMPILE,//开始编译
        END_JUDGE,//评测结束
        END_COMPILE,//编译结束
        SYNC_JUDGING_PROGRAM_ERROR,//下载评测程序时出错
        SYNC_USER_PROGRAM_ERROR,//下载代码包时出错
        WORKER_ERROR,//worker出错，比如worker出RuntimeError了
        JUDGE_PROGRAM_TIMEOUT,//评测时间过长
        WORKER_ONLINE,
        WORKER_OFFLINE,

    }
    ResultType resultType;//消息类型
    Long testPointId;//测试点通过和拒绝应给出测试点id
    Long judgingId;//评测会话id
    String reason;//错误原因
    String workerName;//评测机名称，用于定位错误
}







