package org.nacosdemo.tlmalluserconfigdemo.feign.dto;

/**
 * @author: Fox
 * @Desc:
 **/

public class OrderDTO {

    private String userId;

    private String commodityCode;

    private Integer count;

    private Integer money;

    public OrderDTO() {
    }

    public OrderDTO(String userId, String commodityCode) {
        this.userId = userId;
        this.commodityCode = commodityCode;
    }

    public OrderDTO(String userId, String commodityCode, Integer count, Integer money) {
        this.userId = userId;
        this.commodityCode = commodityCode;
        this.count = count;
        this.money = money;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public void setCommodityCode(String commodityCode) {
        this.commodityCode = commodityCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}
