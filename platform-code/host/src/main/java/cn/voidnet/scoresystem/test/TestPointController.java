package cn.voidnet.scoresystem.test;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("test-point")
public class TestPointController {
    @Resource
    TestPointService testPointService;


//    @AccessControl(UserType.ADMIN)
    @PutMapping("")
    public void newTestPoint(
            @RequestParam Long expId,
            @RequestBody TestPoint testPoint
    )
    {
        testPointService
                .addTestPoint(expId,testPoint);
    }
    //    @AccessControl
    @DeleteMapping("{testPointId}")
    public void deleteTestPoint(@PathVariable Long testPointId) {
        testPointService
                .deleteTestPoint(testPointId);
    }

    @PatchMapping("{testPointId}")
//    @AccessControl
    public void editTestPoint(@PathVariable Long testPointId,
                               @RequestBody TestPoint testPoint
    ) {
        testPointService.editTestPoint(testPointId, testPoint);
    }


}














