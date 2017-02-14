package com.safety.free.data;

public class Element {

    /**id的计算方法：一切从0开始；一书本第1位，第一级目录为第2位，第二级为第3位，依此类推。如：一级目录第一个，二级目录第一个，三级目录第一个标题为00000 */
    private String id;
    private String name;// 元素的标题
    private boolean show;//是否在界面显示
    private boolean evaluation;//是否已经评测
    /**某项的总分: 判给分数/可判分数 * 100% */
    private double score;
    private int scoreZero;//0分
    private int scoreOne;//1分
    private int scoreFive;//5分
    private int scoreSeven;//7分
    private String time;//评估时间

    public Element() {

    }
    
    public Element(String name, boolean isShow){
        this.name = name;
        this.show = isShow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isEvaluation() {
        return evaluation;
    }

    public void setEvaluation(boolean evaluation) {
        this.evaluation = evaluation;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getScoreZero() {
        return scoreZero;
    }

    public void setScoreZero(int scroeZero) {
        this.scoreZero = scroeZero;
    }

    public int getScoreOne() {
        return scoreOne;
    }

    public void setScoreOne(int scroeOne) {
        this.scoreOne = scroeOne;
    }

    public int getScoreFive() {
        return scoreFive;
    }

    public void setScoreFive(int scroeFive) {
        this.scoreFive = scroeFive;
    }

    public int getScoreSeven() {
        return scoreSeven;
    }

    public void setScoreSeven(int scroeSeven) {
        this.scoreSeven = scroeSeven;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
