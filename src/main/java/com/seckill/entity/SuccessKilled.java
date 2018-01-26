package com.seckill.entity;

import java.util.Date;

public class SuccessKilled {

    private long seckilld;
    private long userPhone;
    private short state;
    private Date createTime;


    //变通   多对一
    private Seckill seckill;

    public long getSeckilld() {
        return seckilld;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public short getState() {
        return state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setSeckilld(long seckilld) {
        this.seckilld = seckilld;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public void setState(short state) {
        this.state = state;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckilld=" + seckilld +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}
