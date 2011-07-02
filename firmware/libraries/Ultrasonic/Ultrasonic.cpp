/*
  Ultrasonic.cpp - Library for HR-SC04 sensor
  
  Created by 4ntoine. July 2, 2011
*/

#include "Ultrasonic.h"

#define NO_PIN -1

// 2 ms LOW before triggering
#define TRIGGER_LOW 2

// 10 ms HIGH triggering
#define TRIGGER_HIGH 10

// 3 times measurement to get accurate result
#define ACCURATE_MEASUREMENTS 3

void Ultrasonic::init(int vccPin, int trigPin, int echoPin)
{
    _vccPin = vccPin;
    if (_vccPin != NO_PIN)
		pinMode(_vccPin, OUTPUT);
   
 	_trigPin = trigPin;
    pinMode(_trigPin, OUTPUT);
   
    _echoPin = echoPin;
    pinMode(_echoPin, INPUT);
}

Ultrasonic::Ultrasonic(int trigPin, int echoPin) 
{
	init(NO_PIN, trigPin, echoPin);
}

Ultrasonic::Ultrasonic(int vccPin, int trigPin, int echoPin)
{
	init(vccPin, trigPin, echoPin);	
}

void Ultrasonic::trigger()
{
	// ensure low
	digitalWrite(_trigPin, LOW);
	delayMicroseconds(TRIGGER_LOW);
	
	// triggering
	digitalWrite(_trigPin, HIGH);
	delayMicroseconds(TRIGGER_HIGH);
	
	// low
	digitalWrite(_trigPin, LOW);
}

// switch on
// (if vccPin is specified)
void Ultrasonic::begin()
{
	if (_vccPin != NO_PIN)
		digitalWrite(_vccPin, HIGH);
}
	
// switch off
void Ultrasonic::end()
{
	if (_vccPin != NO_PIN)
		digitalWrite(_vccPin, LOW);
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
	
// mm (high accurancy)
int Ultrasonic::getDistanceAccurate()
{	
	return getDistanceAccurate(ACCURATE_MEASUREMENTS);
}

// returns average value
int averageStrategy(int *distance_mm, byte count)
{
	long total_distance_mm = 0;
	byte tmp_count = count;
	while (tmp_count-- > 0) {
		total_distance_mm += distance_mm[tmp_count];
	}
	return (long)(total_distance_mm / count);
}
	
// mm (high accurancy with measurement count)
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
	}
	
	int result = (*avgStrategy)(distance_mm, measurements);
	free(distance_mm);
	return result;
}

int Ultrasonic::durationToMm(long duration)
{
  return (int)((double)duration / 2.9 / 2);
}