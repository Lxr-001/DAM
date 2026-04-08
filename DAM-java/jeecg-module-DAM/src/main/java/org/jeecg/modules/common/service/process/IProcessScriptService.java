package org.jeecg.modules.common.service.process;


import org.jeecg.modules.common.exception.ProcessScriptExecuteException;

import java.io.IOException;

public interface IProcessScriptService {
    Process execute(String command) throws IOException, ProcessScriptExecuteException;
    Process execute(String command,String formID) throws ProcessScriptExecuteException;
}
