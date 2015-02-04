package hu.kifor.tamas.vonatinfo.model;

/**
 * Created by tamas on 15. 01. 19..
 */
public class Train {
    private String lineId;
    private String trainId;
    //TODO carriage info from vonatinfo.hu

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }
}
