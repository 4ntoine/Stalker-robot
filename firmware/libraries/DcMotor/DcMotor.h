/*
  DcMotor.h - Library for DC Motor
  
  Features:
	1. start/stop
	2. set speed 
	
  Created by 4ntoine. November 16, 2011
*/

#ifndef DcMotor_h
#define DcMotor_h

#include "WProgram.h"

// default speed (max available)
#define DEFAULT_SPEED 255

// default direction (forward)
#define DEFAULT_DIRECTION true

class DcMotor
{
  public:
    // pwmPin regulates rotation speed
	// dirPin regulates rotation direction
	DcMotor(int pwmPin, int dirPin);
	
	DcMotor(int pwmPin, int dirPin, byte speed, boolean forward);
	
	// start rotation
	void start();
	
	// stop rotation
	void stop();
	
	bool isStarted(); // is started?
	
	// direction
	void setDirection(bool forward);
	
	bool getDirection(); // is forward?
	
	// rotation speed	
	void setSpeed(byte speed);
	
	byte getSpeed();
	
  protected:
    int _pwmPin, _dirPin;
	byte _speed;
	bool _forward;
	bool _started;
	
	void init(int pwmPin, int dirPin);
	void applySpeed();
	void applySpeed(byte speed);
	void applyDirection();
};

#endif