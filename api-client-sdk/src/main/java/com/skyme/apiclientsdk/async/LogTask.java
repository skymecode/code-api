package com.skyme.apiclientsdk.async;

import com.skyme.apiclientsdk.utils.FeishuUtils;
import lombok.Data;

@Data
public class LogTask implements Runnable {
    public LogTask() {
    }
    private String level;
    private String content;

    public LogTask(String level, String content) {
        this.level = level;
        this.content = content;
    }

    @Override
    public void run() {
        try {
            FeishuUtils.SendLogInfo(this.level,this.content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
