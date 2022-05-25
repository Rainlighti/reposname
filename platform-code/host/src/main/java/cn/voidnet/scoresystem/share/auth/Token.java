package cn.voidnet.scoresystem.share.auth;

import cn.voidnet.scoresystem.share.user.User;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Data
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne()
    private User user;

    public Token() {
    }

    public Token(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
    }

}
