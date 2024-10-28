package mizdooni.controllers;

import java.lang.reflect.Field;

public class ControllersTestUtils {

    static public Object getField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}
