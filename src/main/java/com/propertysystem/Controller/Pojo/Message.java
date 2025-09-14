package com.propertysystem.Controller.Pojo;

import com.propertysystem.Constant.MessageStatus;
import com.propertysystem.Constant.MessageType;
import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    public String event;
    public String sender;
    public String receiver;
    public Long houseId;
    public String content;
    public MessageType type;
    public String time;
    public MessageStatus status;


}
