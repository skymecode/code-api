package com.skyme.apiclientsdk.model.card;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class Root {
    private Config config;
    private Header header;
    private Map<String, List<I18nElement>> i18n_elements;

    // 添加 getter 和 setter 方法
}
