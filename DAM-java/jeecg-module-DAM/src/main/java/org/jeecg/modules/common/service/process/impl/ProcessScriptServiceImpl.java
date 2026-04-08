package org.jeecg.modules.common.service.process.impl;

import org.jeecg.modules.common.exception.ProcessScriptExecuteException;
import org.jeecg.modules.common.service.process.IProcessContainer;
import org.jeecg.modules.common.service.process.IProcessScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 *  调用脚本服务类
 *  author:lhx
 *  date:2023.11.20
 */
@Service
public class ProcessScriptServiceImpl implements IProcessScriptService {

    @Autowired
    private IProcessContainer processContainer;
    @Override
    public Process execute(String command) throws ProcessScriptExecuteException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd","/c",command);
        processBuilder.redirectErrorStream(true);
        Process process = null;
        try {
            process = processBuilder.start();
            redirectSubProcessStream(process.getInputStream());

           return process;
        } catch (IOException e) {
            if(process != null){
                process.destroy();
            }
            e.printStackTrace();
            throw new ProcessScriptExecuteException(e.getMessage());
        }

    }
    @Override
    public Process execute(String command,String formID) throws ProcessScriptExecuteException {
        Process process = execute(command);
        processContainer.add(process,formID);
        return process;
    }
    private void redirectSubProcessStream(InputStream inputStream) throws IOException {
        Executors.newCachedThreadPool().submit(() -> {
            BufferedReader outReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while(true){
                try {
                    if ((line = outReader.readLine()) == null) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(line);
            }
            try {
                outReader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
