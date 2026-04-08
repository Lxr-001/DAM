package org.jeecg.modules.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogParamVO {
    public boolean autoBackup;  //自动备份
    public boolean autoDelete;  //备份后自动删除
    public int interval;        //备份间隔(天)
    public String path;         //备份路径
}
