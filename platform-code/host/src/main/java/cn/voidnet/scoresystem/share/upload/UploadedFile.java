package cn.voidnet.scoresystem.share.upload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.DigestUtils;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class UploadedFile {
    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @JsonIgnore
    String name;

    @JsonIgnore
    String extension;//文件扩展名

    @JsonIgnore
    @Transient
    private String businessType;//业务类型

    @Transient
    private String url;


    public UploadedFile() {
        this.getBusinessType();
    }

    public String getExtension() {
        return extension;
    }

    public UploadedFile setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getFilename() {
        return id.toString() + '.' + extension;
    }

    public UploadedFile setId(Long id) {
        this.id = id;
        return this;
    }


    public String getBusinessType() {
        businessType = this.getClass().getSimpleName();
        return businessType;
    }

    public UploadedFile setBusinessType(String businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getName() {
        return name;
    }

    public UploadedFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        var sb = new StringBuilder();
        var slat = "exusiai";
        sb.append(id);
        sb.append(DigestUtils.md5DigestAsHex(name.getBytes()));
        sb.append(businessType);
        sb.append(extension);
        sb.append(slat);
        url = DigestUtils.md5DigestAsHex(sb.toString().getBytes());
        if (extension != null)
            return url + '.' + extension;
        else
            return url;
    }

    public UploadedFile setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return "UploadedFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", businessType='" + businessType + '\'' +
                ", extension='" + extension + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
