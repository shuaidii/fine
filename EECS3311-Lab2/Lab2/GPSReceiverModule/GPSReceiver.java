package GPSReceiverModule;

// TODO: decide whether this is a class or interface,
// then complete your implementation

import autopilotModule.Coordinates;

import java.util.Random;

public abstract class GPSReceiver {
    private static Random RANDOM = new Random();

   public Coordinates read(){
       showReceiver();
       int lat = RANDOM.nextInt(2);
       int lon = RANDOM.nextInt(2);
       System.out.printf("GPS DATA LAT DATA IS %s GPS DATA LON IS  %s%n", lat, lon);
       return new Coordinates(lat, lon);
   }

    protected abstract void showReceiver();
 }
