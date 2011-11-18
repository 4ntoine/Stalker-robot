/*
  DcMotor.cpp - Library for DC Motor
  
  Features:
	1. start/stop
	2. set speed 
	
  Created by 4ntoine. November 16, 2011
*/

#include "DcMotor.h"

void DcMotor::init(int pwmPin, int dirPin)
{
	_speed = DEFAULT_SPEED;
	_forward = DEFAULT_DIRECTION;

	_pwmPin = pwmPin;
    pinMode(_pwmPin, OUTPUT);
   
    _dirPin = dirPin;
    pinMode(_dirPin, OUTPUT);
}

DcMotor::DcMotor(int pwmPin, int dirPin)
{
	init(pwmPin, dirPin);
}

DcMotor::DcMotor(int pwmPin, int dirPin, byte speed, boolean forward)
{
	init(pwmPin, dirPin);
	
	_speed = speed;
	_forward = _forward;
}
	
// rotation speed
byte DcMotor::getSpeed()
{
	return _speed;
}	

void DcMotor::setSpeed(byte speed)
{
	_speed = speed;
	if (_started)
		applySpeed();		
}

void DcMotor::applySpeed(byte speed)
{
	analogWrite(_pwmPin, speed);
}

void DcMotor::applySpeed()
{
	applySpeed(_speed);
}
	
// 
bool DcMotor::isStarted()
{
	return _started;
}
	
// start rotation
void DcMotor::start()
{
	_started = true;
	applySpeed();
}
	
// stop rotation
void DcMotor::stop()
{
	_started = false;
	applySpeed(0);
}

// direction
void DcMotor::setDirection(bool forward)
{
	_forward = forward;
	applyDirection();
}

void DcMotor::applyDirection()
{
	digitalWrite(_dirPin, (_forward ? HIGH : LOW));
}
	
// is forward?
bool DcMotor::getDirection()
{
	return _forward;
}