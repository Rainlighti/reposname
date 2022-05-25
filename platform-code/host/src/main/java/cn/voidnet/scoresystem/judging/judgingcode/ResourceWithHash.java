package cn.voidnet.scoresystem.judging.judgingcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceWithHash {
    Resource resource;
    String hash;
}
