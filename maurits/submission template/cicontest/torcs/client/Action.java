package cicontest.torcs.client;


public class Action {
    public double accelerate = 0.0D;
    public double brake = 0.0D;
    public double clutch = 0.0D;
    public int gear = 0;
    public double steering = 0.0D;
    public boolean abandonRace = false;
    public int focus = 360;

    public String toString() {
        limitValues();

        return "(accel " + this.accelerate + ") " + "(brake " + this.brake + ") " + "(clutch " + this.clutch + ") " + "(gear " + this.gear + ") " + "(steer " + -1.0D * this.steering + ") " + "(meta " + (this.abandonRace ? 1 : 0) + ") " + "(focus " + this.focus + ")";
    }


    public void limitValues() {
        this.accelerate = Math.max(0.0D, Math.min(1.0D, this.accelerate));
        this.brake = Math.max(0.0D, Math.min(1.0D, this.brake));
        this.clutch = Math.max(0.0D, Math.min(1.0D, this.clutch));
        this.steering = Math.max(-1.0D, Math.min(1.0D, this.steering));
        this.gear = Math.max(-1, Math.min(6, this.gear));

        if (Double.isNaN(this.accelerate)) this.accelerate = 0.0D;
        if (Double.isNaN(this.brake)) this.brake = 0.0D;
        if (Double.isNaN(this.clutch)) this.clutch = 0.0D;
        if (Double.isNaN(this.steering)) this.steering = 0.0D;
    }
}