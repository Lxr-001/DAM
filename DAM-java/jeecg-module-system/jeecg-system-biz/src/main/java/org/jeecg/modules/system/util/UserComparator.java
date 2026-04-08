package org.jeecg.modules.system.util;

import org.jeecg.modules.system.entity.SysUser;

import java.lang.reflect.Field;
import java.util.Objects;

public class UserComparator {
    public static String compareUsers(SysUser user1, SysUser user2) {
        StringBuilder diff = new StringBuilder();
        Field[] fields = SysUser.class.getDeclaredFields();

        for (Field field : fields) {
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);

                Object value1 = field.get(user1);
                Object value2 = field.get(user2);

                if (!Objects.equals(value1, value2)) {
                    String fieldName = field.getName();
                    diff.append(fieldName)
                            .append(": ")
                            .append(value1 == null ? "null" : value1.toString())
                            .append(" -> ")
                            .append(value2 == null ? "null" : value2.toString())
                            .append("\n");
                }

                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return diff.toString().trim();
    }
}
