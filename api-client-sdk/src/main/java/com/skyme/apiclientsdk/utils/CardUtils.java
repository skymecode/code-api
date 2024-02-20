package com.skyme.apiclientsdk.utils;

import cn.hutool.Hutool;
import cn.hutool.core.date.DateUtil;
import com.skyme.apiclientsdk.model.card.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardUtils {
    public static Root getCard(String level,String context){
        Root root = new Root();

        // 设置 Config 对象
        Config config = new Config();
        config.setWide_screen_mode(true);
        root.setConfig(config);

        // 设置 Header 对象
        Header header = new Header();
        if(level.equals("INFO")){
            header.setTemplate("blue");
        }else  if (level.equals("ERROR")){
            header.setTemplate("red");
        }
        Title title = new Title();
        title.setTag("plain_text");
        I18n i18n = new I18n();
        i18n.setZh_cn("日志");
        title.setI18n(i18n);
        header.setTitle(title);
        root.setHeader(header);

        // 设置 i18n_elements 对象
        //时间元素
        List<I18nElement> i18nElements = new ArrayList<>();
        I18nElement time = new I18nElement();
        time.setTag("markdown");
        time.setContent("**时间:**"+ DateUtil.date());
        i18nElements.add(time);
        // 第一个元素
        I18nElement i18nElement1 = new I18nElement();
        i18nElement1.setTag("markdown");
        i18nElement1.setContent("**日志级别:**");
        i18nElements.add(i18nElement1);

        // 第二个元素
        I18nElement i18nElement2 = new I18nElement();
        i18nElement2.setTag("div");
        Text text2 = new Text();
        text2.setContent(level);
        text2.setTag("plain_text");
        i18nElement2.setText(text2);
        i18nElements.add(i18nElement2);

        // 第三个元素
        I18nElement i18nElement3 = new I18nElement();
        i18nElement3.setTag("markdown");
        i18nElement3.setContent("**详细内容:**\n");
        i18nElements.add(i18nElement3);

        // 第四个元素
        I18nElement i18nElement4 = new I18nElement();
        i18nElement4.setTag("div");
        Text text4 = new Text();
        text4.setContent(context);
        text4.setTag("plain_text");
        i18nElement4.setText(text4);
        i18nElements.add(i18nElement4);

        // 设置 i18n_elements 对象的值
        Map<String, List<I18nElement>> i18nElementsMap = new HashMap<>();
        i18nElementsMap.put("zh_cn", i18nElements);
        root.setI18n_elements(i18nElementsMap);
        return root;
    }
}
