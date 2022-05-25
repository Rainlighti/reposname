package cn.voidnet.worker;


import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class SyncCodeService {
    public static final String SHARED_LIB_FOLDER_NAME = "shared-lib";
    public static final String USER_CODE_FOLDER_NAME = "user-code";
    public static final String JUDGING_CODE_FOLDER_NAME = "judging-code";
    public static final String USER_CODE_BACKUP_FOLDER_NAME = "backup";
    @Value("${score.worker.web-root}")
    String webRoot;
    @Value("${score.worker-key}")
    String workerKey;
    @Value("${score.file-root-path}")
    String fileRoot;
    //expId->judgingCodeHash
    Hashtable<Long, String> expIdJudgingCodeHashMap = new Hashtable<>();
    //expId->lock
    Hashtable<Long, Object> expIdLockMap = new Hashtable<>();


    public void syncUserCodeToLocal(Long judgingId) throws Exception {
        try {
            var http = HttpClients.createDefault();
            var uri = webRoot
                    + "user-code/" + judgingId.toString()
                    + "?key=" + workerKey;
            var req = new HttpGet(uri);

            CloseableHttpResponse res = null;
            for (int i = 0; i < 5; i++) {
                http = HttpClients.createDefault();
                req = new HttpGet(uri);
                res = http.execute(req);
                if (res.getStatusLine().getStatusCode() == 404) {
                    log.info("judging not found,retrying {},judgingId = {}",i, judgingId);
                    Thread.sleep(400+i*400);
                }
                else {
                    break;
                }
            }

            if (res.getStatusLine().getStatusCode() == 404) {
                log.warn("judging not found,judgingId = {}", judgingId);
                throw new RemoteResourceNotFound();
            }

            var fileStream = res.getEntity().getContent();

            createRootFileFolder();

            var userCodeFolderPath = Path.of(fileRoot,
                    USER_CODE_FOLDER_NAME,
                    judgingId.toString()
            );
            ifNotExistThenCreate(userCodeFolderPath);
            var zipFilePath = Path
                    .of(fileRoot, USER_CODE_FOLDER_NAME)
                    .resolve(judgingId.toString() + ".zip");
            if (!zipFilePath.toFile().createNewFile())
                throw new CreateFileFailedException(zipFilePath);
            fileStream.transferTo(new FileOutputStream(zipFilePath.toFile()));

            ZipFile file = new ZipFile(zipFilePath.toFile());
            file.extractAll(userCodeFolderPath.toString());
//            if (!file.getFile().delete())
//                log.error("delete user-code zip file error,judgingId = {}",
//                        judgingId
//                );


        } catch (CreateFileFailedException pe) {
            log.error("failed to create file or folder," +
                    "please check permission," +
                    "path = {}", pe.getFilePath());
            log.error(pe.getMessage(), pe);
            throw pe;
        } catch (RemoteResourceNotFound re) {
            throw re;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

    }

    public void syncJudgingCodeToLocal(Long expId) throws Exception {
        try {
            synchronized (SyncCodeService.class) {
                if (!expIdLockMap.containsKey(expId))
                    expIdLockMap.put(expId, new Object());
            }
            synchronized (expIdLockMap.get(expId)) {
                var hash = "";
                if (expIdJudgingCodeHashMap.containsKey(expId))
                    hash = expIdJudgingCodeHashMap.get(expId);
                var http = HttpClients.createDefault();
                var uri = webRoot
                        + "judging-code/" + expId.toString()
                        + "?lastHash=" + hash
                        + "&key=" + workerKey;
                var req = new HttpGet(uri);
                var res = http.execute(req);
                if (res.getStatusLine().getStatusCode() == 204) {
                    log.info("use judging code cache,expId = {}", expId);
                    return;
                }
                if (res.getStatusLine().getStatusCode() == 404) {
                    log.info("exp not found,expId = {}", expId);
                    throw new RemoteResourceNotFound();
                }
                expIdJudgingCodeHashMap.put(expId, res
                        .getFirstHeader("hash")
                        .getValue());
                var fileStream = res.getEntity().getContent();

                createRootFileFolder();

                var codeFilePath = Path
                        .of(fileRoot, JUDGING_CODE_FOLDER_NAME)
                        .resolve(expId + ".py");
                deleteIfExist(codeFilePath);
                if (!codeFilePath.toFile().createNewFile())
                    throw new CreateFileFailedException(codeFilePath);
                fileStream.transferTo(new FileOutputStream(codeFilePath.toFile()));

            }


        } catch (CreateFileFailedException pe) {
            log.error("failed to create file or folder," +
                    "please check permission," +
                    "path = {}", pe.getFilePath());
            log.error(pe.getMessage(), pe);
            throw pe;

        } catch (RemoteResourceNotFound re) {
            throw re;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

    }

    public Path moveAllJudgingDependencyToJudgingWorkDir(Long judgingId, Long expId) {
        var fileRootPath = Path.of(fileRoot);
        var sharedLibPath = fileRootPath.resolve(SHARED_LIB_FOLDER_NAME);
        var userCodePath = fileRootPath.resolve(USER_CODE_FOLDER_NAME);
        var judgingCodePath = fileRootPath.resolve(JUDGING_CODE_FOLDER_NAME);
        var judgingWorkDirPath = userCodePath.resolve(judgingId.toString());

        if (sharedLibPath.toFile().exists()) {
            for (File file : Objects.requireNonNull(sharedLibPath.toFile().listFiles())) {

                try {
                    var newFilePath = judgingWorkDirPath.resolve(file.getName());
                    Files.copy(file.toPath(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error("Can not move shared lib to work dir,expId = {} " +
                            "judgingId = {}", expId, judgingId);
                    log.error(e.getMessage(), e);
                }

            }
        }

        judgingCodePath = judgingCodePath.resolve(expId.toString() + ".py");
        try {
            var newFilePath = judgingWorkDirPath.resolve("judge.py");
            Files.copy(judgingCodePath, newFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Can not move shared lib to work dir,expId = {} " +
                    "judgingId = {}", expId, judgingId);
            log.error(e.getMessage(), e);
        }
        log.info("moved all dependency to work dir,expId = {} " +
                "judgingId = {}", expId, judgingId);
        return judgingWorkDirPath;


    }


    public void cleanUserCodeFolder(Long judgingId) {
        if (!deleteFolder(Path.of(fileRoot, USER_CODE_FOLDER_NAME, judgingId.toString())
                .toFile())
        )
            log.error("error in clean user code folder ,judgingId = {} ",
                    judgingId.toString()
            );
    }
    public void backupUserCode(Long judgingId)
    {
        var path= Path
                .of(fileRoot, USER_CODE_FOLDER_NAME, judgingId.toString()+".zip");
        var file = path.toFile();
        try {
            var fileRootPath = Path.of(fileRoot);
            var backupPath= fileRootPath.resolve(USER_CODE_BACKUP_FOLDER_NAME);
            var newFilePath =backupPath.resolve(file.getName());
            Files.move(file.toPath(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.warn("Can not backup user code,judgingId= {} ",judgingId);
        }


    }

    private void createRootFileFolder() throws CreateFileFailedException {
        ifNotExistThenCreate(Path.of(fileRoot));
        ifNotExistThenCreate(Path.of(fileRoot, USER_CODE_FOLDER_NAME));
        ifNotExistThenCreate(Path.of(fileRoot, JUDGING_CODE_FOLDER_NAME));
        ifNotExistThenCreate(Path.of(fileRoot, SHARED_LIB_FOLDER_NAME));
        ifNotExistThenCreate(Path.of(fileRoot, USER_CODE_BACKUP_FOLDER_NAME));
    }


    private void ifNotExistThenCreate(Path path) throws CreateFileFailedException {
        if (!path.toFile().exists())
            if (!path.toFile().mkdir()) {
                throw new CreateFileFailedException(path);
            }
    }

    private void deleteIfExist(Path path) {
        if (path.toFile().exists())
            path.toFile().delete();
    }

    boolean deleteFolder(File folder) {
        File[] allContents = folder.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteFolder(file);
            }
        }
        return folder.delete();
    }


}
