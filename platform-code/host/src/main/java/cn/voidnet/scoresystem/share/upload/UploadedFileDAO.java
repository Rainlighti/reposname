package cn.voidnet.scoresystem.share.upload;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileDAO extends JpaRepository<UploadedFile, Long> {
}
