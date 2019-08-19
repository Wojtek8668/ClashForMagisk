package com.github.kr328.clash;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class ClashConfigure {
    String portHttp;
    String portSocks;
    String portRedir;
    String portDns;

    public static ClashConfigure loadFromFile(File file) throws IOException {
        Map root = new Yaml(new SafeConstructor()).loadAs(new FileReader(file), Map.class);
        ClashConfigure result = new ClashConfigure();

        result.portHttp = valueOfOrNull(root.get("port"));
        result.portSocks = valueOfOrNull(root.get("socks-port"));
        result.portRedir = valueOfOrNull(root.get("redir-port"));

        Map dns = (Map) root.get("dns");
        if ( dns != null && (boolean) dns.get("enable") && dns.containsKey("listen") ) {
            result.portDns = String.valueOf(dns.get("listen")).split(":")[1];
        }

        return result;
    }

    private static String valueOfOrNull(Object object) {
        if ( object == null )
            return null;
        return String.valueOf(object);
    }
}
