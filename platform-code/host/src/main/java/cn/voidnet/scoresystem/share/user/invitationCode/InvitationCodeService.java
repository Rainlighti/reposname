package cn.voidnet.scoresystem.share.user.invitationCode;

import cn.voidnet.scoresystem.share.user.User;
import cn.voidnet.scoresystem.share.user.UserType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Random;

@Service
public class InvitationCodeService {
    @Resource
    InvitationCodeDAO invitationCodeDAO;


    @Transactional
    public InvitationCode useKey(String key) {
        var findKey = invitationCodeDAO.findByKeyContent(key)
                .filter(it -> it.getRemainingTimes() > 0)
                .orElseThrow(InvitationCodeNotAvaliableException::new);
        findKey.setRemainingTimes(findKey.getRemainingTimes() - 1);
        invitationCodeDAO.save(findKey);
        return findKey;
    }

    public InvitationCode newKey(
                                 UserType userType,
                                 User inviter,
                                 int availableTimes //可用次数
    ) {
        var newKey = new InvitationCode();
        newKey.keyContent = new Random()
                .ints(0, 36)
                .boxed()
                .map(it ->
                        it < 10
                                ? it.toString()
                                : String.valueOf((char) (it.intValue() - 10 + 'A'))
                )
                .dropWhile(it -> it == "0" || it == "O" || it == "I" || it == "1")
                .limit(8)
                .reduce("", (prev, next) -> prev + next);
        newKey.setRemainingTimes(availableTimes);
        newKey.setUserType(userType);
        newKey.setInviter(inviter);
        return invitationCodeDAO.save(newKey);
    }

}
