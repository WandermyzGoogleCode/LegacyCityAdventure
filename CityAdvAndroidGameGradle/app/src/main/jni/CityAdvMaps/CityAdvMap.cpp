#include "CityAdvMap.h"
#include <stdio.h>

#include <log.h>
#include <string.h>

#ifndef MAYA_PLUGIN
#include <GLES/gl.h>
#endif

CityAdvMap::~CityAdvMap()
{
	if(buildings != NULL)
	{
		delete [] buildings;
		buildings = NULL;
	}

	if(eventPoints != NULL)
	{
		delete [] eventPoints;
		eventPoints = NULL;
	}

	if(filePathNoExt != NULL)
	{
		delete [] filePathNoExt;
		filePathNoExt = NULL;
	}

	if(materials != NULL)
	{
		delete [] materials;
		materials = NULL;
	}
}

void CityAdvMap::createBuildings(int num)
{
	if(buildings != NULL)
	{
		delete [] buildings;
	}
	buildings = new Building[num];
	buildingNum = num;
}

void CityAdvMap::createEventPoints(int num)
{
	if(eventPoints != NULL)
	{
		delete [] eventPoints;
	}
	eventPoints = new EventPoint[num];
	eventPointNum = num;
}

void CityAdvMap::createMaterials(int num)
{
	if(materials != NULL)
	{
		delete [] materials;
	}

	materials = new MaterialDesc[num];
	materialNum = num;
}

void CityAdvMap::expandBoundingBox(const Point3D& objMinVertex, const Point3D& objMaxVertex)
{
	if(minVertex.isZero() && maxVertex.isZero())
	{
		//first building
		minVertex = objMinVertex;
		maxVertex = objMaxVertex;
	}
	else
	{
		if(objMinVertex.x < minVertex.x)
			minVertex.x = objMinVertex.x;
		if(objMinVertex.y < minVertex.y)
			minVertex.y = objMinVertex.y;
		if(objMinVertex.z < minVertex.z)
			minVertex.z = objMinVertex.z;
		if(objMaxVertex.x > maxVertex.x)
			maxVertex.x = objMaxVertex.x;
		if(objMaxVertex.y > maxVertex.y)
			maxVertex.y = objMaxVertex.y;
		if(objMaxVertex.z > maxVertex.z)
			maxVertex.z = objMaxVertex.z;
	}
}

const IDrawable* CityAdvMap::getDrawbleObject(int index) const
{
	if(index < getBuildingNum())
	{
		return buildings + index;
	}
	else if(index < getBuildingNum() + getEventPointNum())
	{
		return eventPoints + (index - getBuildingNum());
	}
	else 
	{
		return NULL;
	}
}

int CityAdvMap::getDrawbleObjectNum() const
{
	return getBuildingNum() + getEventPointNum();
}

bool CityAdvMap::loadFromFile(const char* filePath)
{
	FILE* fp = fopen(filePath, "rb");
	if(fp == NULL)
	{
		LOGE("Failed to load map");
		return false;
	}

	//check header
	char header[] = FILE_HEADER_MAP;
	char* headerInFile = new char[strlen(header) + 1];

	fread(headerInFile, sizeof(char), strlen(header) + 1, fp);

	if(strcmp(header, headerInFile) != 0)
	{
		delete headerInFile;
		return false;	//error file header
	}

	delete headerInFile;
	headerInFile = NULL;

	//read total bounding box
	fread(&minVertex, sizeof(Point3D), 1, fp);
	fread(&maxVertex, sizeof(Point3D), 1, fp);

	//buildings
	fread(&buildingNum, sizeof(int), 1, fp);
	LOGI("Buildings: %d", buildingNum);
	createBuildings(buildingNum);

	for(int i = 0; i < buildingNum; i++)
	{
		fread(&(buildings[i].getDesc()), sizeof(BuildingDesc), 1, fp);
	}
	//events
	fread(&eventPointNum, sizeof(int), 1, fp);
	createEventPoints(eventPointNum);
	for(int i = 0; i < eventPointNum; i++)
	{
		fread(&(eventPoints[i].getDesc()), sizeof(EventDesc), 1, fp);
	}
	LOGI("Events: %d", eventPointNum);

	//materials
	fread(&materialNum, sizeof(int), 1, fp);
	createMaterials(materialNum);
	fread(materials, sizeof(MaterialDesc), materialNum, fp);
	LOGI("Materials: %d", materialNum);
	for(int i = 0; i < materialNum; i++)
	{
		LOGI("Material %d: %f %f %f", i, materials[i].diffuse.r, materials[i].diffuse.g, materials[i].diffuse.b);
	}
	fclose(fp);

	//store file path
	const char* pos = strrchr(filePath, '.');
	if(pos == NULL)
	{
		LOGE("Illegal map file name");
		return false;
	}

	int len = pos - filePath;
	if(filePathNoExt != NULL)
	{
		delete [] filePathNoExt;
	}

	filePathNoExt = new char[len + 1];
	strncpy(filePathNoExt, filePath, len);
	filePathNoExt[len] = '\0';
	isLoaded = true;

	LOGI("File Path: %s", filePathNoExt);
	LOGI("Map loaded.");

	//load all model file. TEMPERARILY. TODO: dynamic load
	char* modelFilePath = new char[strlen(filePathNoExt) + OBJECT_NAME_MAX_LENGTH + strlen(FILE_EXT_MODEL) + 3];	//3 = _ . \0
	for(int i = 0; i < buildingNum; i++)
	{
		sprintf(modelFilePath, "%s_%s.%s", filePathNoExt, buildings[i].getDesc().name, FILE_EXT_MODEL);
		bool result = buildings[i].getModel().loadFromFile(modelFilePath);
		if(!result)
		{
			return false;
		}
	}
	delete modelFilePath;

	return true;
}


#ifndef MAYA_PLUGIN
void CityAdvMap::draw(GameEngine& gameEngine) const
{
	for(int i = 0; i < buildingNum; i++)
	{
		buildings[i].draw(gameEngine);
	}
}
#endif
