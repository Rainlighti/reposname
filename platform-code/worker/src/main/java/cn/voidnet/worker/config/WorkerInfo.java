package cn.voidnet.worker.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerInfo {
    String name;
    String ip;

    @Override
    public String toString() {
        return String.format("%s:%s",ip,name);
    }
}
