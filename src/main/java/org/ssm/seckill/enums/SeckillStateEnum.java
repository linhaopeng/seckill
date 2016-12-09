package org.ssm.seckill.enums;

/**
 * 使用枚举表述常量数据字典
 * Created by baojulin on 2016/12/7.
 */
public enum SeckillStateEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重发成功"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3, "数据篡改");

    private int state;

    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    /**
     * 根据状态获取实例
     *
     * @param index
     * @return
     */
    public static SeckillStateEnum stateOf(int index) {
        SeckillStateEnum[] values = values();
        for (SeckillStateEnum state : values) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
