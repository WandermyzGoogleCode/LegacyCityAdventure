#ifndef GAME_ENGINE_H
#define GAME_ENGINE_H

#include "Camera.h"
#include <CityAdvMaps/CityAdvMap.h>

#define PERSON_HEIGHT 1.40f

class GameEngine
{
private:
	Camera* camera;	//the camera currenly used
	Camera defaultCamera;	//the unique camera in the engine. in future maybe more
	CityAdvMap map;
	double initTime;
	double lastFrameTime;

	double getTimeNow() const;

public:
	GameEngine();
	~GameEngine();
	bool loadMap(const char* fileName);

	void onSurfaceCreated();
	void onSurfaceChanged(int width, int height);
	void drawFrame();

	double getGameTime() const;
	double getElapsedTime() const;

	void getEventRawData(int eventId, signed char* buffer);	//for jni to do sha-1 check in Java
	void gotoEventPoint(int eventId);

	inline const CityAdvMap& getCityAdvMapConst() const {return map;}

	inline Camera* getCamera() {return camera; }
};

#endif
