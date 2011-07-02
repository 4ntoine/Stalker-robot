#include <Ultrasonic.h>

const int VCC_PIN = 11;
const int TRIG_PIN = 12;
const int ECHO_PIN = 13;

Ultrasonic ultrasonic(VCC_PIN, TRIG_PIN, ECHO_PIN);

void setup()
{
  Serial.begin(9600);
  Serial.println("Enter count of measurements:");
  
  ultrasonic.begin();
}

int count, tmp_count, distance_mm, distance_acc_mm;

void loop() {  
  // wait for char in serial
  if (Serial.available() > 0)
  {
    count = Serial.read() - 48;
    
    Serial.print("Starting measurement ... count="); Serial.println(count);
    tmp_count = count;
    while (tmp_count-- > 0) {
       distance_mm = ultrasonic.getDistance();
       delayMicroseconds(100);
       Serial.print(" mm="); Serial.println(distance_mm);
    }    
    
    distance_acc_mm = ultrasonic.getDistanceAccurate((byte)count);  

    Serial.println("Done");        
    Serial.print("~mm="); Serial.println(distance_acc_mm);
  }
}
