package cn.voidnet.worker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootTest
class WorkerApplicationTests {
    @Resource
    SyncCodeService syncCodeService;

    void contextLoads() {
//        syncCodeService.syncJudgingCodeToLocal(8l);
//        syncCodeService.syncUserCodeToLocal(845l);
//        syncCodeService.moveAllJudgingDependencyToJudgingWorkDir(845l,8l);
    }
    void testP() throws IOException {
//        ProcessBuilder pb = new ProcessBuilder("python3.7 '/Users/thevoid/dev/os-exp/score-sys/worker/file/user-code/943/judging.py'");

        ProcessBuilder pb =
                new ProcessBuilder(
                        "python3.7 ./judging.py");
        Process p  = pb.start();
        var reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine ()) != null) {
            System.out.println ("Stdout: " + line);
        }

    }

}
