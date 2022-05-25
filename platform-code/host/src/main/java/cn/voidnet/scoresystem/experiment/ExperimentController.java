package cn.voidnet.scoresystem.experiment;

import cn.voidnet.scoresystem.share.auth.AccessControl;
import cn.voidnet.scoresystem.test.TestPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exp")
public class ExperimentController {
    @Resource
    ExperimentService experimentService;


    @GetMapping("all/brief")
    public List<Experiment> getAllExperimentsOnlyBrief(){
        return experimentService
                .getAllExperimentsBrief()
                .stream()
                .sorted(Comparator.comparingInt(Experiment::getOrder))
                .collect(Collectors.toList());

    }
    //TODO:only admin
    @GetMapping()
    @AccessControl
    public List<ExperimentInfoVO> getAllExperiments(

    ) {
        return experimentService.getAllExperiments();
    }

    //    @AccessControl
    @DeleteMapping("{expId}")
    public void deleteExperiment(@PathVariable Long expId) {
        experimentService.deleteExperiment(expId);
    }

    @PatchMapping("{expId}")
//    @AccessControl
    public void editExperiment(@PathVariable Long expId,
                               @RequestBody Experiment experiment
    ) {
        experimentService.editExperiment(expId, experiment);
    }

    @PutMapping()
//    @AccessControl
    public void addExperiment(
            @RequestBody Experiment experiment
    ) {
        experimentService.addExperiment(experiment);


    }
    //TODO:only admin
    @GetMapping("{expId}/student")
    @AccessControl
    public Page<StudentsExperimentStatusVO> getAllStudentExpStatus(
            @PathVariable Long expId,
            Pageable pageRequest

    ) {
        if (pageRequest == null) {
            pageRequest = PageRequest.of(0, 10);
        }
        return experimentService.getStudentsExperimentStatus(expId,pageRequest);
    }
    @GetMapping("{expId}/title")
    public String getExpTitle(
            @PathVariable Long expId

    ) {
        return experimentService
                .getExpTitle(expId)
                ;
    }
    @GetMapping("{expId}/test-point")
    public List<TestPoint> getExp(
            @PathVariable Long expId

    ) {
        return experimentService
                .getExpTestPoints(expId)
                ;
    }
}









