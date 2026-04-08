package org.jeecg.modules.common.service.process;
/**
 *  进程容器
 *  author:lhx
 *  date:2023.11.20
 */
public interface IProcessContainer {
    /**
     * 加入进程
     * @param process 进程
     * @param formID 表单id
     */
    void add(Process process, String formID);

    /**
     * 移除进程
     * @param formID 表单id
     */
    void remove(String formID);
}
