package cn.voidnet.scoresystem.judging.judgingcode;

import cn.voidnet.scoresystem.experiment.Experiment;
import cn.voidnet.scoresystem.experiment.ExperimentInfoVO;
import cn.voidnet.scoresystem.experiment.ExperimentService;
import cn.voidnet.scoresystem.share.auth.AccessControl;
import cn.voidnet.scoresystem.share.util.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class JudgingCodeController {
    @Resource
    JudgingCodeService judgingCodeService;


    //    @AccessControl(ExperimentType.ADMIN)
    @PutMapping("judging-code/{expId}")
    public Long uploadJudgingProgram(
            @PathVariable Long expId,
            MultipartFile code
    ) {
        return judgingCodeService.addJudgingProgram(expId,code);
    }

    @GetMapping("judging-code/{expId}")
    public ResponseEntity<org.springframework.core.io.Resource>
    judgingWorkerDownloadJudgingCode(
            @PathVariable Long expId,
            @RequestParam String key,
            @RequestParam(required = false) String lastHash,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        var resWithHash = judgingCodeService
                .getJudgingCodeByWorkerKeyIfDirty(key,expId, lastHash);
        response.setHeader("hash", resWithHash.getHash());
        return ResponseUtil
                .getDownloadEntity(resWithHash.resource, request);
    }




}










