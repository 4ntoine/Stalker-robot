#include <Ultrasonic.h>

#define TRIG_PIN 12
#define ECHO_PIN 13

// 50 ms between measurements 
#define BETWEEN_TIME 50

Ultrasonic ultrasonic(TRIG_PIN, ECHO_PIN);

void setup()
{
  Serial.begin(9600);
  Serial.println("Enter count of measurements:");
}

int count, tmp_count, distance_mm, distance_acc_mm;

void loop() {  
  // wait for data in Serial
  if (Serial.available() > 0)
  {
    count = Serial.read() - 48;
    
    // measurements series
    Serial.print("Starting measurement ... count="); Serial.println(count);
    tmp_count = count;
    while (tmp_count-- > 0) {
       distance_mm = ultrasonic.getDistance();
       delay(BETWEEN_TIME); // recommended value
       Serial.print(" mm="); Serial.println(distance_mm);
    }    

    delay(BETWEEN_TIME); // recommended value
    
    // accurate measurements series
    distance_acc_mm = ultrasonic.getDistanceAccurate((byte)count);  
          
    Serial.print("~mm="); Serial.println(distance_acc_mm);
    Serial.println("Done");  
  }
}

