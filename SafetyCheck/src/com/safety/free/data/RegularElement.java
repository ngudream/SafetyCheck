package com.safety.free.data;

public class RegularElement extends Element {

    private String source;// 元素的标题
    private String punishableScore = "0-1-5-7";// 可判分数
    private String category;

    public RegularElement() {
    }
    
    public RegularElement(String name, boolean isShow) {
        super(name, isShow);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPunishableScore() {
        return punishableScore;
    }

    public void setPunishableScore(String punishableScore) {
        this.punishableScore = punishableScore;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
