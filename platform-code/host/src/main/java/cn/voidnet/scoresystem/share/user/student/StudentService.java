package cn.voidnet.scoresystem.share.user.student;

import cn.voidnet.scoresystem.judging.JudgingDAO;
import cn.voidnet.scoresystem.share.auth.AuthService;
import cn.voidnet.scoresystem.share.auth.TokenDAO;
import cn.voidnet.scoresystem.share.exception.ResourceNotFoundException;
import cn.voidnet.scoresystem.share.upload.FileService;
import cn.voidnet.scoresystem.share.upload.UploadedFile;
import cn.voidnet.scoresystem.share.user.User;
import cn.voidnet.scoresystem.share.user.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Resource
    StudentDAO studentDAO;

    @Resource
    JudgingDAO judgingDAO;

    @Resource
    FileService fileService;

    @Resource
    TokenDAO tokenDAO;
    
    @Resource
    AuthService authService;

    private StudentInfoVO mapToStudentInfo(User user)
    {
        return StudentInfoVO
                .builder()
                .name(user.getName())
                .clazz(user.getClazz())
                .studentId(user.getStudentId())
                .scoreSum(user.getScoreSum())
                .id(user.getId())
                .lastSubmitTime(
                        judgingDAO
                        .findTop1ByUserOrderByLastSubmitTimeDesc(user)
                        .map(it->it.getLastSubmitTime())
                        .orElse(0l)
                )
                .triedExpCount(
                        judgingDAO
                        .countByLastSubmitCodeNotNullAndUser(user)
                )
                .build();

    }
    public Page<StudentInfoVO> getAllStudents(Pageable pageable){
        return studentDAO
                .findUserByType(UserType.STUDENT,pageable)
                .map(this::mapToStudentInfo)
                ;
    }
    public Page<StudentInfoVO> getAllStudentsByName(String name,Pageable pageable){
        return studentDAO
                .findUserByTypeAndNameContains(UserType.STUDENT,name,pageable)
                .map(this::mapToStudentInfo)
                ;
    }
    public Page<StudentInfoVO> getAllStudentsByStudentId(String studentId, Pageable pageable){
        return studentDAO
                .findUserByTypeAndStudentIdContains(UserType.STUDENT,studentId,pageable)
                .map(this::mapToStudentInfo)
                ;
    }

    @Transactional
    public void deleteStudent(Long userId) {
        var student = studentDAO.findById(userId)
                .orElseThrow(ResourceNotFoundException::new);
        var judgings = judgingDAO.findAllByUser(student);
        judgings.forEach(judging->{
            Optional.ofNullable(judging.getLastSubmitCode())
                    .map(UploadedFile::getId)
                    .ifPresent(fileService::deleteIfExist);
            judgingDAO.delete(judging);
        });
        authService.invaildTokens(student);
        studentDAO.delete(student);
    }

    @Transactional
    public void editStudent(Long userId, User student) {
        var origin = studentDAO.findById(userId)
                .orElseThrow(ResourceNotFoundException::new);
        origin.setName(student.getName());
        origin.setStudentId(student.getStudentId());
        origin.setClazz(student.getClazz());
        if(StringUtils.hasText(student.getPassword()))
        {
            origin.setPassword(student.getPassword());
            authService.invaildTokens(origin);
        }
        studentDAO.save(origin);
    }

    @Transactional
    public void addStudent(User student) {
        var newStudent =
                User.builder()
                .studentId(student.getStudentId())
                .clazz(student.getClazz())
                .name(student.getName())
                .type(UserType.STUDENT)
                .password(student.getPassword())
                .build();
        studentDAO.save(newStudent);
    }
    @Transactional
    public void addStudents(
            List<User> students) {
        var users = students
                .stream()
                .map(student-> User.builder()
                        .studentId(student.getStudentId())
                        .clazz(student.getClazz())
                        .name(student.getName())
                        .type(UserType.STUDENT)
                        .password(student.getPassword())
                        .build()
                )
        .collect(Collectors.toList())
        ;
        studentDAO.saveAll(users);


    }
}
