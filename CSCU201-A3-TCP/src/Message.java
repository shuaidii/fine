import java.io.Serializable;

public class Message implements Serializable {
    private MessageType messageType;
    private Location center;
    private Object value;

    public Message(MessageType messageType, Location center) {
        this.messageType = messageType;
        this.center = center;
    }

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
