#include <DcMotor.h>

#define DIR_PIN 12
#define PWM_PIN 11

char c;

byte speeds[] = {40, 80, 120, 160, 200, 255}; // 4, 5, 6, 7, 8, 9
byte speed = 5;

DcMotor motor(PWM_PIN, DIR_PIN);

void setup()
{
  Serial.begin(9600);
  
  Serial.println("1 - start/stop");
  Serial.println("2 - change direction");
  Serial.println("3 - print status");
  Serial.println("4-9 - set speed");
  
  printStatus();
}

void loop()
{
  if (Serial.available())
  {
    c = Serial.read();
    
    Serial.print(">>");
    Serial.println(c);
    
    // start/stop
    if (c == '1') {
      if (motor.isStarted())
	motor.stop();
      else
	motor.start();
		
      printOnOff();
    } else
    // forward/backward
    if (c == '2') {
      motor.setDirection(!motor.getDirection());
      printDirection();
    } else
    //print current status
    if (c == '3')
    {
      printStatus();
    }
      else
    // change speed
    {
      speed = speeds[c - 48 - 4]; // command starting from "4", so "4" -> [0]
      boolean prevStarted = motor.isStarted();
      
      // off and wait
      motor.stop();
      delay(100);
      
      motor.setSpeed(speed);
	  
      //restore on/off and
      if (prevStarted)
	motor.start();
      else
	motor.stop();

      printOnOff();
    }
  }
}

void printOnOff()
{
  Serial.print(motor.isStarted() ? "ON," : "OFF,");
  Serial.print("SPEED=");
  Serial.println(motor.getSpeed(), DEC);
}

void printDirection()
{
  Serial.println(motor.getDirection() ? "forward" : "backward");  
}

void printStatus()
{
  printOnOff();
  printDirection();      
}
