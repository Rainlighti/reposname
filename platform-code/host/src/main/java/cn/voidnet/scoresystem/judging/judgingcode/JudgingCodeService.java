package cn.voidnet.scoresystem.judging.judgingcode;

import cn.voidnet.scoresystem.experiment.Experiment;
import cn.voidnet.scoresystem.experiment.ExperimentDAO;
import cn.voidnet.scoresystem.judging.JudgingDAO;
import cn.voidnet.scoresystem.judging.worker.WorkerKeyWrongException;
import cn.voidnet.scoresystem.share.exception.ResourceNotFoundException;
import cn.voidnet.scoresystem.share.upload.FileService;
import cn.voidnet.scoresystem.share.upload.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class JudgingCodeService {
    @Value("${score.worker-key}")
    String workerKey;
    @Resource
    JudgingCodeDAO judgingCodeDAO;
    @Resource
    JudgingDAO judgingDAO;
    @Resource
    ExperimentDAO experimentDAO;
    @Resource
    FileService fileService;


    @Transactional
    public Long addJudgingProgram(Long expId, MultipartFile program) {
        var exp = experimentDAO.findById(expId)
                .orElseThrow(ResourceNotFoundException::new);
        if (exp.getJudgingCode() != null) {
            var id = exp.getJudgingCode().getId();
            exp.setJudgingCode(null);
            fileService.deleteIfExist(id);
        }
        var uploadedProgram =
                (JudgingCode)
                        fileService.upload(program,
                                JudgingCode.class,
                                "judge_" + exp.getId().toString());
        var md5 = fileService.getHash(uploadedProgram.getId());
        uploadedProgram.setJudgingCodeHash(md5);
        judgingCodeDAO.save(uploadedProgram);
        exp.setJudgingCode(uploadedProgram);
        experimentDAO.save(exp);
        return uploadedProgram.getId();
    }

    @Transactional
    public void removeJudgingProgramIfPresent(Long expId) {
        var judgingCodeOpt = judgingCodeDAO
                .findByExperimentId(expId);
        if (judgingCodeOpt.isPresent()) {
            var judgingCode = judgingCodeOpt.get();
            Optional.ofNullable(judgingCode)
                    .map(UploadedFile::getId)
                    .ifPresent(fileService::deleteIfExist);
            judgingCodeDAO.delete(judgingCode);
        }
    }

    public boolean existJudgingCode(Long expId) {
        return judgingCodeDAO.existsByExperimentId(expId);

    }

    public ResourceWithHash
    getJudgingCodeByWorkerKeyIfDirty
            (String key, Long expId, String lastProgramHash)
            throws JudgingCodeNotDirtyException {
        if (!key.equals(workerKey))
            throw new WorkerKeyWrongException();
        var code = experimentDAO
                .findById(expId)
                .map(Experiment::getJudgingCode)
                .orElseThrow(ResourceNotFoundException::new);
        if (lastProgramHash != null)
            if (lastProgramHash.equals(code.getJudgingCodeHash()))
                throw new JudgingCodeNotDirtyException();
        return ResourceWithHash
                .builder()
                .resource(
                        fileService.download(code)
                )
                .hash(code.getJudgingCodeHash())
                .build();


    }

}



















