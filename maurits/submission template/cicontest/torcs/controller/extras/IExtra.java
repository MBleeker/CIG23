package cicontest.torcs.controller.extras;

import cicontest.torcs.client.Action;
import cicontest.torcs.client.SensorModel;
import java.io.Serializable;

public interface IExtra
  extends Serializable
{
  public abstract void process(Action paramAction, SensorModel paramSensorModel);
  
  public abstract void reset();
}