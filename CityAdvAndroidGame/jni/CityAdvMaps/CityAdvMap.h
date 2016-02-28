#ifndef CITY_ADV_MAP_H
#define CITY_ADV_MAP_H

#include "CaMapFileStructures.h"
#include "Building.h"
#include "EventPoint.h"
#include <stdio.h>
#include <log.h>

//The class for storing all map data, including bounding box, models, materials, etc. Dynamic loading supported. Used for both Maya and Android
class CityAdvMap
{
private:
	//CaLinkedList<Building*> buildingList;
	int buildingNum;
	Building* buildings;

	int eventPointNum;
	EventPoint* eventPoints;

	int materialNum;
	MaterialDesc* materials;

	int billboardNum;
	BillboardDesc* billboards;

	Point3D minVertex;
	Point3D maxVertex;

	bool isLoaded;
	char* filePathNoExt;
public:
	CityAdvMap() : buildingNum(0), buildings(NULL), eventPointNum(0), eventPoints(NULL), materialNum(0), materials(NULL), minVertex(0, 0, 0), maxVertex(0, 0, 0), isLoaded(false), filePathNoExt(NULL)
	{
		LOGI("Constructor: filePathNoExt Addr = %d", filePathNoExt);
	}
	~CityAdvMap();

	inline Building* getBuildings() const {return buildings; }
	inline int getBuildingNum() const {return buildingNum; }

	inline EventPoint* getEventPoints() const {return eventPoints;}
	inline int getEventPointNum() const {return eventPointNum;}

	inline MaterialDesc* getMaterials() const {return materials; }
	inline int getMaterialNum() const {return materialNum; }

	inline BillboardDesc* getBillboards() const {return billboards; }
	inline int getBillboardNum() const {return billboardNum; }

	//get a drawable object of specified index, in order buildings, billboards, events. return NULL if index out of bound
	const IDrawable* getDrawbleObject(int index) const;
	int getDrawbleObjectNum() const;

	//allocate space for buildings/events
	void createBuildings(int num);
	void createEventPoints(int num);
	void createMaterials(int num);
	void createBillboards(int num);

	bool loadFromFile(const char* filePath);

	//expand the current bounding box to include the new object
	void expandBoundingBox(const Point3D& objMinVertex, const Point3D& objMaxVertex);

	inline const Point3D& getBoundingBoxMinVertex() const {return minVertex; }
	inline const Point3D& getBoundingBoxMaxVertex() const {return maxVertex; }

#ifndef MAYA_PLUGIN
	void draw(GameEngine& gameEngine) const;
#endif //MAYA_PLUGIN
};

#endif
