import java.util.Random;

class Message{

    //incorporate message types here

    public static final int 
    TYPE_MESSAGE = 0,
    TYPE_TELEMETRY = 1,
    TYPE_DATA = 2,
    TYPE_ABORT = 3,
    TYPE_INSTRUCTION = 4,
    TYPE_RESPONSE_NEEDED = 5;

    public static final Message 
    ABORT_COMMAND = new Message(TYPE_ABORT, 8, "Abort Mission"),            //send by controller to mission
    ABORTED_MESSAGE = new Message(TYPE_TELEMETRY, 8, "Mission Aborted"),    //sent by mission as a status message

    FAILURE_MESSAGE = new Message(TYPE_TELEMETRY, 8, "Unknown Failure"),
    
    SOFTWARE_UPGRADES_COMMAND = new Message(TYPE_INSTRUCTION, "Attempting software upgrade"),
    SOFTWARE_UPGRADE_FAILED = new Message(TYPE_TELEMETRY, "Software upgrade failed"),
    SOFTWARE_UPGRADE_SUCCESS = new Message(TYPE_DATA, "Software upgrade success"),
    
    STAGE_REQUEST_MESSAGE = new Message(TYPE_DATA, 8, "Requesting stage"),
    STAGE_COMMAND = new Message(TYPE_INSTRUCTION, 8, "Staging"),
    STAGE_SUCCESS = new Message(TYPE_DATA, 16, "Stage Success"),
    
    MISSION_SUCCESSFUL = new Message(TYPE_DATA, 16, "Mission Successful"),

    COMMAND_RESPONSE = new Message(TYPE_DATA, 16, "Command Response Sent"),
    COMMAND_RESPONSE_ACK = new Message(TYPE_DATA, "Command Response Acknowledged");
    
    int type;
    int bits;

    String info;

    Failure failure;
    
    Message(String info){
        this(TYPE_MESSAGE, info.length() * 8, info);
    }

    /*
    used to send data about the mission
    info is just what is displayed in console
    random number of MB
    */

    Message(int type, Object... os){
        this(type, Main.compileString(os));
    }

    Message(int type, String info){
        this(type, (new Random().nextInt(10))*1000000, info);
    }
    
    Message(int type, int bits, String info){
        this.type = type;
        this.bits = bits;
        this.info = info;
    }

    Message(Failure failure){
        this(TYPE_TELEMETRY, failure.name);
        this.failure = failure;
    }

    public String getString(){
        return this.info;
    }

}