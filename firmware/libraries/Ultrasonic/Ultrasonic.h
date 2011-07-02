/*
  Ultrasonic.h - Library for HR-SC04 sensor  
  Created by 4ntoine. July 2, 2011
*/

#ifndef Ultrasonic_h
#define Ultrasonic_h

#include "WProgram.h"

typedef int (*AVG_STRATEGY)(int*, byte);

class Ultrasonic
{
  public:
	Ultrasonic(int trigPin, int echoPin);
	Ultrasonic(int trigPin, int echoPin, int vccPin);
	
	// switch on
    // (if vccPin is specified)
	void begin();
	
	// switch off
	// (if vccPin is specified)
	void end();
	
    // mm
	int getDistance();
	
	// mm (high accuracy)
	int getDistanceAccurate();
	
	// mm (high accuracy with measurement count)
	int getDistanceAccurate(byte measurements);
	
	// mm (high accuracy with average value strategy)
	int getDistanceAccurate(byte measurements, AVG_STRATEGY avgStrategy);

  protected:
    int _trigPin, _echoPin, _vccPin;

	void init(int vccPin, int trigPin, int echoPin);
	void trigger();	
	long getDuration();
	int durationToMm(long duration);
};

#endif