package cn.voidnet.scoresystem.share.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    @Autowired
    UploadedFileDAO uploadedFileDao;

    @Value("${score.file-root-path}")
    String fileRootPath;

    public UploadedFile upload(MultipartFile file, Class businessType) {
        return this.upload(file, businessType, UUID.randomUUID().toString());
    }

    public UploadedFile upload(
            MultipartFile file,
            Class businessType,
            String name
    ) {
        try {
            var uploadFile = (UploadedFile) businessType.getConstructor().newInstance();
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.indexOf('.') + 1);
            fileName = fileName.substring(0, fileName.indexOf('.'));
            uploadFile.setExtension(extension);
            uploadFile.setName(name);
            uploadFile.setExtension(extension);
            this.uploadedFileDao.save(uploadFile);
            var folderPath = Path.of(fileRootPath, uploadFile.getBusinessType());
            if (!folderPath.toFile().exists())
                if (!folderPath.toFile().mkdir())
                    throw new FileAccessException("failed to create folder,please check permission");
            var uploadFilePath = folderPath.resolve(uploadFile.getFilename());
            if (uploadFilePath.toFile().createNewFile()) {
                var newfile = new File(uploadFilePath.toRealPath(LinkOption.NOFOLLOW_LINKS).toString());
                file.transferTo(newfile);
                log.info("Uploaded file" + uploadFile.toString() + " to " + uploadFilePath.toString());
                return uploadFile;
            } else throw new FileAccessException("failed to create file,please check permission");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new UploadedFailedException();
        }
    }

    public Resource download(UploadedFile uploadFile) {
        try {
            var filePath = Path.of(this.fileRootPath, uploadFile.getBusinessType(), uploadFile.getFilename());
            var resource = new UrlResource(filePath.toUri());
            if (resource.exists())
                return resource;
            else throw new UploadedFileNotFoundException();
        } catch (Exception e) {
            throw new UploadedFileNotFoundException();
        }
    }
    public String getHash(Long fileId)
    {
        var uploadedFile = uploadedFileDao
                .findById(fileId)
                .orElseThrow(UploadedFileNotFoundException::new);
        try {
            var filePath = Path.of(this.fileRootPath, uploadedFile.getBusinessType(), uploadedFile.getFilename());
            var file = filePath
                    .toFile();
            if (file.exists()) {
                return DigestUtils.md5DigestAsHex(new FileInputStream(file));

            } else throw new UploadedFileNotFoundException();
        } catch (Exception e) {
            throw new UploadedFileNotFoundException();
        }

    }

    //TODO:need to test
    public void deleteIfExist(Long fileId) {
        try {
            delete(fileId);
        } catch (UploadedFileNotFoundException e) {

        }
    }

    public void delete(Long fileId) throws UploadedFileNotFoundException {
        var uploadedFile = uploadedFileDao
                .findById(fileId)
                .orElseThrow(UploadedFileNotFoundException::new);
        try {
            var filePath = Path.of(this.fileRootPath, uploadedFile.getBusinessType(), uploadedFile.getFilename());
            var file = filePath
                    .toFile();
            if (file.exists()) {
                {
                    file.delete();
                    uploadedFileDao.delete(uploadedFile);
                }
            } else throw new UploadedFileNotFoundException();
        } catch (Exception e) {
            throw new UploadedFileNotFoundException();
        }
    }

    public Resource download(Long fileId) {
        var file = uploadedFileDao.findById(fileId);
        return this.download(file.orElseThrow(UploadedFileNotFoundException::new));
    }
}
