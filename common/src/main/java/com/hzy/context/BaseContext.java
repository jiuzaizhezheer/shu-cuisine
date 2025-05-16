package com.hzy.context;
//TODO threadLocal上下文
public class BaseContext {
    /**
     * 设置与获取当前线程绑定的id
     */
    private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Integer id) {
        threadLocal.set(id);
    }

    public static Integer getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
