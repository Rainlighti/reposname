package cn.voidnet.scoresystem.share.util;

import cn.voidnet.scoresystem.share.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ResponseUtil {
    public static ResponseEntity<Resource> getDownloadEntity(Resource resource, HttpServletRequest request) {
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public static Set<Entry> mapToEntry(Set<? extends Entry> obj) {
        return obj
                .stream()
                .map(it -> new Entry(it.getId(), it.getName()))
                .collect(Collectors.toSet());
    }

    public static List<Entry> mapToEntry(List<? extends Entry> obj) {
        return obj
                .stream()
                .map(it -> new Entry(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public static Entry mapToEntry(Entry obj) {
        return new Entry(obj.getId(), obj.getName());
    }

    //显然的是 基本数据类型不能设null
    public static <T> T allSetNull(T entity, Set<String> exceptPropertiesNames) {
        setAllPropertiesToNull(entity, exceptPropertiesNames, true);
        return entity;
    }

    public static <T> Collection<T> allSetNull(Collection<T> entities, Set<String> exceptPropertiesNames) {
        for (T entity : entities) {
            setAllPropertiesToNull(entity, exceptPropertiesNames, true);
        }
        return entities;
    }

    //显然的是 基本数据类型不能设null
    public static <T> T setNull(T entity, Set<String> propertiesNames) {
        setAllPropertiesToNull(entity, propertiesNames);
        return entity;
    }

    public static <T> Collection<T> setNull(Collection<T> entities, Set<String> exceptPropertiesNames) {
        for (T entity : entities) {
            setAllPropertiesToNull(entity, exceptPropertiesNames);
        }
        return entities;
    }

    private static <T> void setAllPropertiesToNull(T obj, Set<String> propertiesNames) {
        setAllPropertiesToNull(obj, propertiesNames, false);
    }

    private static <T> void setAllPropertiesToNull(T obj, Set<String> propertiesNames, boolean isComplementarySet) {
        var methods =
                obj.getClass()
                        .getMethods();
        Arrays.stream(methods)
                .filter(it -> it.getName().startsWith("set"))
                .filter(it -> it.getParameterTypes()[0].getSimpleName().matches("^[A-Z].*?"))
                .filter(it -> {
                    var propertiesName = it.getName().substring(3);
                    propertiesName = propertiesName.substring(0, 1).toLowerCase() + propertiesName.substring(1);
                    return isComplementarySet != propertiesNames.contains(propertiesName);
                })
                .forEach(it -> {
                    try {
                        it.invoke(obj, new Object[]{null});
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                });
    }

}
