/*
  Ultrasonic.cpp - Library for HC-SR04 sensor
  
  Features:
	1. measurement series for accurate values
	2. filters bad measurement values
	
  Created by 4ntoine. July 10, 2011
*/

#include "Ultrasonic.h"

// 4 meters - max sensor distance
#define SENSOR_MAX_DISTANCE 4000

// 2 us LOW before triggering
#define TRIGGER_LOW_TIME 2

// 10 us HIGH triggering
#define TRIGGER_HIGH_TIME 10

// 50 ms between measurements 
#define BETWEEN_TIME 50

// 3 times measurement to get accurate result
#define MEASUREMENTS_COUNT 3

Ultrasonic::Ultrasonic(int trigPin, int echoPin)
{
	_trigPin = trigPin;
    pinMode(_trigPin, OUTPUT);
   
    _echoPin = echoPin;
    pinMode(_echoPin, INPUT);
}

void Ultrasonic::trigger()
{
	// ensure low
	digitalWrite(_trigPin, LOW);
	delayMicroseconds(TRIGGER_LOW_TIME);
	
	// triggering
	digitalWrite(_trigPin, HIGH);
	delayMicroseconds(TRIGGER_HIGH_TIME);
	
	// low
	digitalWrite(_trigPin, LOW);
}

long Ultrasonic::getDuration()
{
	// start measurement
	trigger();
	
	// waiting for reflection
	return pulseIn(_echoPin,HIGH);
}

// mm
int Ultrasonic::getDistance()
{
	return durationToMm(getDuration());
}
	
// mm (high accuracy)
int Ultrasonic::getDistanceAccurate()
{	
	return getDistanceAccurate(MEASUREMENTS_COUNT);
}

bool isGoodValue(int value)
{
	return (value > 0 && value < SENSOR_MAX_DISTANCE);
}

// returns average value
int averageStrategy(int *distance_mm, byte count)
{
	long total_distance_mm = 0;
	byte tmp_count = count;
	int cur_distance_mm;
	int final_count = count;
	while (tmp_count-- > 0) {
		cur_distance_mm = distance_mm[tmp_count];
		
		// filter values
		if (isGoodValue(cur_distance_mm))
		{
			// good value
			total_distance_mm += cur_distance_mm;
		}
		else
		{
			// bad value
			final_count--;
		}
	}
	return (long)(total_distance_mm / final_count);
}
	
// mm (high accuracy with measurement count)
int Ultrasonic::getDistanceAccurate(byte measurements)
{	
	return getDistanceAccurate(measurements, averageStrategy);
}

// mm (high accuracy with average value strategy)
int Ultrasonic::getDistanceAccurate(const byte measurements, AVG_STRATEGY avgStrategy)
{
	int* distance_mm = (int*)malloc(measurements * sizeof(byte));
	
	for (int i=0; i<measurements; i++) {
		distance_mm[i] = durationToMm(getDuration());
		
		// delay between measurements recommended
		if (i>0)
			delay(BETWEEN_TIME);
	}
	
	int result = (*avgStrategy)(distance_mm, measurements);
	free(distance_mm);
	return result;
}

int Ultrasonic::durationToMm(long duration)
{
  return (int)((double)duration / 2.9 / 2);
}