package org.jeecg.modules.quartz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogParam {
    public boolean autoDel;    //备份后自动删除
    public String path;        //备份路径
}
