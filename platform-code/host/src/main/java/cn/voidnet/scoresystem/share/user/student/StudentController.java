package cn.voidnet.scoresystem.share.user.student;

import antlr.Utils;
import cn.voidnet.scoresystem.share.auth.AccessControl;
import cn.voidnet.scoresystem.share.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("student")
@Slf4j
public class StudentController {
    @Resource
    StudentService studentService;

    //TODO:only admin
    @GetMapping
    @AccessControl
    public Page<StudentInfoVO> getAllStudent(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String studentId,
            Pageable pageRequest

    ) {
        if (pageRequest == null) {
            pageRequest = PageRequest.of(0, 10);
        }
        if (name != null) {
            return studentService.getAllStudentsByName(name, pageRequest);
        }
        if (studentId != null) {
            return studentService.getAllStudentsByStudentId(studentId, pageRequest);
        }
        return studentService.getAllStudents(pageRequest);
    }

    //    @AccessControl
    @DeleteMapping("{userId}")
    public void deleteStudent(@PathVariable Long userId) {
        studentService.deleteStudent(userId);
    }

    @PatchMapping("{userId}")
//    @AccessControl
    public void editStudent(@PathVariable Long userId,
                            @RequestBody User student
    ) {
        studentService.editStudent(userId, student);
    }

    @PutMapping()
//    @AccessControl
    public void addStudent(
            @RequestBody User student
    ) {
        studentService.addStudent(student);


    }

    @PutMapping("import")
    public Integer importStudentFromExcelFile(
            MultipartFile excel
    ) throws IOException {
        List<User> students =
                new LinkedList<>();
        try {

            XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            var rowIter = sheet
                    .rowIterator();
            rowIter.next();//skip header
            rowIter.forEachRemaining(
                    row -> {
                        var name = row.getCell(0).getStringCellValue();
                        var clazz = row.getCell(1).getStringCellValue();
                        var studentId = row.getCell(2).getStringCellValue();
                        if (
                                !StringUtils.hasText(name)
                                        || !StringUtils.hasText(clazz)
                                        || !StringUtils.hasText(studentId)
                        )
                            return;
                        var password =
                                Optional.ofNullable(row.
                                        getCell(3)
                                        .getStringCellValue());
                        var user = User
                                .builder()
                                .studentId(studentId)
                                .password(password.orElse("123456"))
                                .name(name)
                                .clazz(clazz)
                                .build();
                        students.add(user);
                    }
            );
        } catch (Exception e) {
            log.error("table format invalid:" + e.getMessage());

            throw new TableFormatInvalidException();
        }
        studentService.addStudents(students);


        return students.size();
    }


    //    @AccessControl
    @GetMapping(value = "export/info",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public @ResponseBody
    byte[] exportAllStudentInfoToCSV(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        @Data
        @JsonPropertyOrder({"studentId", "name", "clazz", "scoreSum"})
        class StudentInfoCSVFormat {
            @JsonIgnore
            Long id;
            @JsonProperty("姓名")
            String name;
            @JsonProperty("学号")
            String studentId;
            @JsonProperty("班级")
            String clazz;
            @JsonProperty("总分")
            Integer scoreSum;
            @JsonIgnore
            Integer triedExpCount;
            @JsonIgnore
            Long lastSubmitTime;

        }
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        var studentsVO = studentService
                .getAllStudents(pageable)
                .getContent();
        CsvMapper mapper = new CsvMapper();
        mapper.addMixIn(StudentInfoVO.class, StudentInfoCSVFormat.class);
        CsvSchema schema = mapper
                .schemaFor(StudentInfoVO.class)
                .withHeader()
                .withLineSeparator("\n");
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        return mapper.writer(schema)
                .writeValueAsBytes(studentsVO);


    }


}







