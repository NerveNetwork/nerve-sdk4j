package network.nerve.core.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import network.nerve.core.io.IoUtils;
import network.nerve.core.parse.JSONUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoulijun
 * @Time: 2019-03-13 17:51
 * @Description: 功能描述
 */
public class JsonModuleConfigParser implements ModuleConfigParser {
    @Override
    public String fileSuffix() {
        return "json";
    }

    @Override
    public Map<String,Map<String, ConfigurationLoader.ConfigItem>> parse(String configFile, InputStream inputStream) {
        try {
            String configJson = IoUtils.readRealPath(inputStream);
            Map<String,Object> data = JSONUtils.json2map(configJson);
            Map<String,ConfigurationLoader.ConfigItem> res = new HashMap<>(data.size());
            data.forEach((key, value) -> {
                if (ConfigSetting.isPrimitive(value.getClass())) {
                    res.put(key, new ConfigurationLoader.ConfigItem(configFile, String.valueOf(value)));
                } else {
                    try {
                        res.put(key, new ConfigurationLoader.ConfigItem(configFile, JSONUtils.obj2json(value)));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            });
            Map<String, Map<String, ConfigurationLoader.ConfigItem>> map = new HashMap<>();
            map.put(ConfigurationLoader.GLOBAL_DOMAIN,res);
            return map;
        } catch (Exception e) {
            throw new RuntimeException("json配置文件解析错误");
        }
    }
}
