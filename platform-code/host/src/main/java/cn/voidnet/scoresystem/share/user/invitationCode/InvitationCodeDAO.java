package cn.voidnet.scoresystem.share.user.invitationCode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationCodeDAO extends JpaRepository<InvitationCode, Long> {
    Optional<InvitationCode> findByKeyContent(String content);
}
