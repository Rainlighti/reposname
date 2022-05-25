package cn.voidnet.scoresystem.judging.event;

import cn.voidnet.scoresystem.judging.Judging;
import cn.voidnet.scoresystem.judging.JudgingDAO;
import cn.voidnet.scoresystem.judging.JudgingService;
import cn.voidnet.scoresystem.share.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventService {
    @Resource
    EventDAO eventDAO;
    @Resource
    JudgingDAO judgingDAO;
    @Resource
    JudgingService judgingService;

    List<Class<? extends Event>> initEventClassList =
            List.of(
                    UploadEvent.class,
                    CompileEvent.class,
                    JudgeEvent.class
            );

    // <eventClass,nextState> -> <state transform>
    Map<Map.Entry<Class, EventState>, BiFunction<Judging, Event, Event>> fsm
            = new HashMap<>();

    @PostConstruct
    public void initEventFSM() {
        fsm.put(Map.entry(UploadEvent.class, EventState.RUNNING), (judging, currEvent) -> {
            return currEvent;
        });
        fsm.put(Map.entry(UploadEvent.class, EventState.ERROR), (judging, currEvent) -> {
            currEvent.setIsFinal(true);
            judgingService.updateUserScoreSumIfExist(judging.getUser().getId());
            return currEvent;
        });
        fsm.put(Map.entry(UploadEvent.class, EventState.DONE), (judging, currEvent) -> {
            return switchState(judging, CompileEvent.class, EventState.NOT_START);
        });
        fsm.put(Map.entry(CompileEvent.class, EventState.NOT_START), (judging, currEvent) -> {
            judgingService.dispatchJudgingTaskToWorker(judging);
            return switchState(judging, CompileEvent.class, EventState.WAITING);
        });
        fsm.put(Map.entry(CompileEvent.class, EventState.WAITING), (judging, currEvent) -> {
            return currEvent;
        });
        fsm.put(Map.entry(CompileEvent.class, EventState.RUNNING), (judging, currEvent) -> {
            return currEvent;
        });
        fsm.put(Map.entry(CompileEvent.class, EventState.ERROR), (judging, currEvent) -> {
            currEvent.setIsFinal(true);
            judgingService.updateUserScoreSumIfExist(judging.getUser().getId());
            return currEvent;
        });
        fsm.put(Map.entry(CompileEvent.class, EventState.DONE), (judging, currEvent) -> {
            return switchState(judging, JudgeEvent.class, EventState.NOT_START);
        });
        fsm.put(Map.entry(JudgeEvent.class, EventState.NOT_START), (judging, currEvent) -> {
            return switchState(judging, JudgeEvent.class, EventState.WAITING);
        });
        fsm.put(Map.entry(JudgeEvent.class, EventState.WAITING), (judging, currEvent) -> {
            return currEvent;
        });
        fsm.put(Map.entry(JudgeEvent.class, EventState.RUNNING), (judging, currEvent) -> {
            return currEvent;
        });
        fsm.put(Map.entry(JudgeEvent.class, EventState.DONE), (judging, currEvent) -> {
            currEvent.setIsFinal(true);
            judgingService.updateUserScoreSumIfExist(judging.getUser().getId());
            return currEvent;
        });
        fsm.put(Map.entry(JudgeEvent.class, EventState.ERROR), (judging, currEvent) -> {
            currEvent.setIsFinal(true);
            judgingService.updateUserScoreSumIfExist(judging.getUser().getId());
            return currEvent;
        });
    }

    public List<Class<? extends Event>> getInitEventClassList() {
        return initEventClassList;
    }

    public List<Event> initEventList() {
        var events = initEventClassList
                .stream()
                .map(it -> {
                    try {
                        return it
                                .getDeclaredConstructor()
                                .newInstance();
                    } catch (ReflectiveOperationException ignored) {
                    }
                    return null;
                })
                .map(it->(Event)it)
                .collect(Collectors.toList());
        return events;
    }

    public static Event getEvent(List<Event> events, Class eventClass) {
        return events.stream()
                .filter(it -> eventClass.isAssignableFrom(it.getClass()))
                .findFirst()
                .get();
    }

    synchronized public boolean checkIfStateThenSetState(Judging judging,
                                                         Class currEventClass,
                                                         EventState currState,
                                                         EventState nextState
    ) {
        if (judging.getCurrEvent().state.equals(currState)
                && currEventClass.equals(judging.getCurrEvent().getClass())) {
            setState(judging, currEventClass, nextState);
            return true;
        } else return false;
    }

    synchronized public void setState(Judging judging, Class<? extends Event> eventClass, EventState nextState) {
//        if (!eventClass.equals(judging.getCurrEvent().getClass()))
//            log.warn("setState in abnormal state.," +
//                    "expect current {},but get {}. in _->{}.{}",
//                    judging.getCurrEvent().getClass(),eventClass,
//                    eventClass,nextState
//                    );
        Event currEvent = switchState(judging, eventClass, nextState);
        judging.setCurrEvent(currEvent);
        judging.getEvents().forEach(eventDAO::save);
        judgingDAO.save(judging);
    }

    synchronized public void setState(Long judgingId, Class<? extends Event> eventClass, EventState nextState) {
        var judging = judgingDAO.findById(judgingId).orElseThrow(ResourceNotFoundException::new);
        setState(judging, eventClass, nextState);
    }

    synchronized public void setState(Long judgingId, EventState nextState) {
        var judging = judgingDAO.findById(judgingId).orElseThrow(ResourceNotFoundException::new);
        setState(judging, judging.getCurrEvent().getClass(), nextState);
    }

    private Event switchState(Judging judging, Class eventClass, EventState nextState) {
        var now = new Date().getTime();
        var event = getEvent(judging.getEvents(), eventClass);
        event.time = now;
        event.state = nextState;
        Event currEvent = fsm.get(Map.entry(eventClass, nextState)).apply(judging, event);
        return currEvent;
    }

    public boolean isSameEventTypeAndState(
            Event eventA,
            Event eventB
    ) {
        return eventA.getState().equals(eventB.getState())
                && eventA.getClass().equals(eventB.getClass());
    }

    public boolean isSameEventTypeAndState(
            Event eventA,
            Class<? extends Event> eventClassB,
            EventState eventStateB
    ) {
        return eventA.getState().equals(eventStateB)
                && eventA.getClass().equals(eventClassB);
    }

}












