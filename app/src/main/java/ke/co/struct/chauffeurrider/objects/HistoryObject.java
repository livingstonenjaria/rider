package ke.co.struct.chauffeurrider.objects;

/**
 * Created by STRUCT on 2/5/2018.
 */

public class HistoryObject {
    private String from;
    private String to;
    private String ridedate;
    private String ridetime;
    private String historyid;
    private String status;

    public HistoryObject(String from, String to, String ridedate, String ridetime, String historyid, String status) {
        this.from = from;
        this.to = to;
        this.ridedate = ridedate;
        this.ridetime = ridetime;
        this.historyid = historyid;
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getRidedate() {
        return ridedate;
    }

    public String getRidetime() {
        return ridetime;
    }



    public String getStatus() {
        return status;
    }

    public String getHistoryid() {
        return historyid;
    }
}