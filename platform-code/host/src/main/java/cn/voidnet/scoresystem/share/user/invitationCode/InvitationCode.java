package cn.voidnet.scoresystem.share.user.invitationCode;

import cn.voidnet.scoresystem.share.user.User;
import cn.voidnet.scoresystem.share.user.UserType;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class InvitationCode {
     @Id
     @GeneratedValue
     Long id;


     String keyContent;




    UserType userType;


    Integer RemainingTimes;


    @OneToOne
    User inviter;
}


