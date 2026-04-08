package org.jeecg.modules.common.service.process.impl;
import org.jeecg.modules.common.service.process.IProcessContainer;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class ProcessContainer implements IProcessContainer {
    private static volatile Map<String,Process> processMap = new ConcurrentHashMap<String,Process>();

    public void add(Process process, String formID){
        processMap.put(formID, process);
    }

    public void remove(String formID){
        Optional.of(processMap.get(formID)).ifPresent(process -> {
            process.destroy();
            processMap.remove(formID);
        });

    }
}
