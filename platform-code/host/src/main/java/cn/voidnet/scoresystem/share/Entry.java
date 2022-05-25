package cn.voidnet.scoresystem.share;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@AllArgsConstructor
@MappedSuperclass
public class Entry {
    @Id
    @GeneratedValue
    private Long id;

    private String name;


    public Entry() {
    }

}
