package com.github.kr328.clash;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ProxySetup {
    private String dataDir;
    
    ProxySetup(String dataDir) {
        this.dataDir = dataDir;
    }
    
    void execOnStarted(StarterConfigure starterConfigure, ClashConfigure clashConfigure) {
        try {
            HashMap<String, String> env = new HashMap<>();
            
            if ( clashConfigure.portHttp != null )
                env.put("CLASH_HTTP_PORT", clashConfigure.portHttp);
            if ( clashConfigure.portSocks != null )
                env.put("CLASH_SOCKS_PORT", clashConfigure.portSocks);
            if ( clashConfigure.portRedir != null )
                env.put("CLASH_REDIR_PORT", clashConfigure.portRedir);
            if ( clashConfigure.portDns != null )
                env.put("CLASH_DNS_PORT", clashConfigure.portDns);
            
            exec("sh " + dataDir + "/mode.d/" + starterConfigure.mode + "/on-start.sh", env);
        } catch (IOException e) {
            Log.e(Constants.TAG, "proxy-setup: failure", e);
        }
    }
    
    void exceOnStop(StarterConfigure starterConfigure, ClashConfigure clashConfigure) {
        try {
            HashMap<String, String> env = new HashMap<>();

            if ( clashConfigure.portHttp != null )
                env.put("CLASH_HTTP_PORT", clashConfigure.portHttp);
            if ( clashConfigure.portSocks != null )
                env.put("CLASH_SOCKS_PORT", clashConfigure.portSocks);
            if ( clashConfigure.portRedir != null )
                env.put("CLASH_REDIR_PORT", clashConfigure.portRedir);
            if ( clashConfigure.portDns != null )
                env.put("CLASH_DNS_PORT", clashConfigure.portDns);
            
            exec("sh " + dataDir + "/mode.d/" + starterConfigure.mode + "/on-stop.sh", env);
        } catch (IOException e) {
            Log.e(Constants.TAG, "proxy-setup: failure", e);
        }
    }
    
    private void exec(String command, HashMap<String, String> env) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        
        builder.command("/system/bin/sh");
        builder.environment().putAll(env);
        
        Process process = builder.start();
        
        process.getOutputStream().write(("exec " + command + " 2>&1\n").getBytes());
        process.getOutputStream().flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        
        while (( line = reader.readLine()) != null ) {
            Log.i(Constants.TAG, "proxy-setup: " + line);
        }

        try {
            process.waitFor();
        } catch (InterruptedException ignored) {}
        
        process.destroy();
        process.destroyForcibly();
    }
}
