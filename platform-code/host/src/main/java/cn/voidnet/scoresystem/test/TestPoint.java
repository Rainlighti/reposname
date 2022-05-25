package cn.voidnet.scoresystem.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestPoint {
    @Id
    @GeneratedValue
    @Null
    Long id;

    @NotBlank
    String name;

    @NotBlank
    Integer score;
}