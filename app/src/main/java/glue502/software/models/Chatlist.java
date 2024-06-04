package glue502.software.models;

public class Chatlist {
    private String SpeakerName;//说话者的名字
    private String SpeakContent;//聊天的内容
    private String SpeakTime;//聊天时间，这个先放着不用
    public Chatlist(String SpeakerName,String SpeakContent){
        this.SpeakerName=SpeakerName;
        this.SpeakContent=SpeakContent;
    }
    public String getSpeakerName(){
        return this.SpeakerName;
    }
    public String getSpeakContent(){
        return this.SpeakContent;
    }
    public String getSpeakTime(){
        return this.SpeakTime;
    }
}
