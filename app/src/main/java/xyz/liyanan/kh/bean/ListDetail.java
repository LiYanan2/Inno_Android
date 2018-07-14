package xyz.liyanan.kh.bean;

public class ListDetail {
    //另一个用户的用户名、订单时间以及用户类型
    private String username1;
    private String username2;
    private String datetime;

    public ListDetail(String username1, String username2, String datetime) {
        this.username1 = username1;
        this.username2 = username2;
        this.datetime = datetime;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
