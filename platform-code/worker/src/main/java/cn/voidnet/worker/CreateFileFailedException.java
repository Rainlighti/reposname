package cn.voidnet.worker;

import lombok.Data;

import java.nio.file.Path;

@Data
public class CreateFileFailedException extends Exception {
    private Path filePath;
    public CreateFileFailedException(Path filePath) {
        super();
        setFilePath(filePath);
    }
}
