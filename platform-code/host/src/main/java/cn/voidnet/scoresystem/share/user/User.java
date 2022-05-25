package cn.voidnet.scoresystem.share.user;

import cn.voidnet.scoresystem.share.Entry;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
public class User extends Entry {
    public interface OnlyBasicUserInfo {
    }


    @NotBlank(message = "studentId can not be blank")
    @Column(unique = true)
    @JsonView(OnlyBasicUserInfo.class)
    private String studentId;

    @JsonView(OnlyBasicUserInfo.class)
    private String clazz;

    private int scoreSum;



    @Id
    @GeneratedValue
    @JsonView(OnlyBasicUserInfo.class)
    private Long id;


    @JsonView({OnlyBasicUserInfo.class })
    @NotBlank(message = "name can not be black")
    private String name;


    @Column(length = 32)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

//    @OneToOne
//    private Clazz clazz;


    @JsonView(OnlyBasicUserInfo.class)
    @Enumerated(value = EnumType.STRING)
    private UserType type = UserType.UNKNOWN;




    public User() {
    }

    public User(Long id) {
        this.setId(id);
    }

    public User(String name, String password, String studentId) {
        this.name = name;
        this.password = password;
        this.studentId = studentId;
    }

}
