package cn.voidnet.scoresystem.judging;

import cn.voidnet.scoresystem.share.auth.AccessControl;
import cn.voidnet.scoresystem.share.auth.CurrUser;
import cn.voidnet.scoresystem.share.user.User;

import javax.transaction.Transactional;

import cn.voidnet.scoresystem.share.util.SetNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/judging")
public class JudgingController {
    @Resource
    JudgingService judgingService;

    @GetMapping("last")
    @AccessControl()
    @Transactional
    Judging getLastJudging(@RequestParam Long expId, @CurrUser User user) {
        return Optional.of(judgingService
                .getLastJudgingIfNotExistThenCreateNew(user.getId(), expId))
                .map(SetNull.of(Judging::setUser))
                .map(SetNull.of(Judging::setLastSubmitCode))
                .get()
                ;
    }

    ;

    @GetMapping("last/dynamic-part")
    @AccessControl()
    Judging getJudgingOnlyDynamicPart(@RequestParam Long expId, @CurrUser User user) {
        return judgingService.getJudgingOnlyDynamicPart(user.getId(), expId);
    }
    @GetMapping("peek")
//    @AccessControl()
    @Transactional
    Judging peekJudging(@RequestParam Long judgingId) {
        assert judgingService!=null;
        return Optional.of(judgingService
                .getJudging(judgingId))
                .map(SetNull.of(Judging::setUser))
                .map(SetNull.of(Judging::setLastSubmitCode))
                .get();
    }

    ;

    @GetMapping("peek/dynamic-part")
//    @AccessControl()
    Judging peekJudgingOnlyDynamicPart(@RequestParam Long judgingId) {
        return judgingService.getJudgingOnlyDynamicPart(judgingId);
    }

    @GetMapping("in-queue")
    List<Judging> getJudgingInQueue() {
        return judgingService.getJudgingInQueue();
    }

    @GetMapping("summary")
    @AccessControl
    List<JudgingSummaryVO> getJudgingSummary(@CurrUser User user) {
        return judgingService.getJudgingSummary(user.getId());
    }

    //TODO:Only Admin
    @GetMapping("student/{userId}/summary")
    @AccessControl
    List<JudgingSummaryVO> getJudgingSummary(@PathVariable Long userId) {
        return judgingService.getJudgingSummary(userId);
    }


}

