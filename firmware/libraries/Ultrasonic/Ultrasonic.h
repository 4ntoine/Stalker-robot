/*
  Ultrasonic.h - Library for HC-SR04 sensor
  
  Features:
	1. measurement series for accurate values
	2. filters bad measurement values 
	
  Created by 4ntoine. July 10, 2011
*/

#ifndef Ultrasonic_h
#define Ultrasonic_h

#include "WProgram.h"

typedef int (*AVG_STRATEGY)(int*, byte);

class Ultrasonic
{
  public:
	Ultrasonic(int trigPin, int echoPin);
	
    // mm
	int getDistance();
	
	// mm (high accuracy)
	int getDistanceAccurate();
	
	// mm (high accuracy with measurement count)
	int getDistanceAccurate(byte measurements);
	
	// mm (high accuracy with average value strategy)
	int getDistanceAccurate(byte measurements, AVG_STRATEGY avgStrategy);

  protected:
    int _trigPin, _echoPin;

	void trigger();	
	long getDuration();
	int durationToMm(long duration);
};

#endif