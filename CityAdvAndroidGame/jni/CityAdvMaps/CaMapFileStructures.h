#ifndef CA_MAP_FILE_STRUCTURES_H
#define CA_MAP_FILE_STRUCTURES_H

#define OBJECT_NAME_MAX_LENGTH 64	//
#define EVENT_CHECKSUM_LENGTH 40	//SHA1 160 bits, 40 bytes in ASCII
#define FILE_HEADER_MAP "CITYADV_MAP"
#define FILE_HEADER_MODEL "CITYADV_MOD" 

//file extensions
#define FILE_EXT_MAP "map"
#define FILE_EXT_OBJ "obj"
#define FILE_EXT_MODEL "mod"
#define FILE_EXT_KDTREE "kdt"
#define FILE_EXT_EVENT_HINT "evt"

#include <string.h>
#include "Geometry.h"

//Corresponding to Maya MEL
typedef enum CaTypeEnum
{
	None = 0,
	CaBuilding = 1,
	CaBillboard = 2,
	CaEvent = 3
} CaType;

typedef struct BuildingDescStruct
{
	char name[OBJECT_NAME_MAX_LENGTH + 1];
	Point3D minVertex;
	Point3D maxVertex;
	int hitTestEnabled;

	BuildingDescStruct()
	{
		name[0] = '\0';
	}
	BuildingDescStruct(const struct BuildingDescStruct& obj) : minVertex(obj.minVertex), maxVertex(obj.maxVertex), hitTestEnabled(obj.hitTestEnabled)
	{
		strncpy(name, obj.name, OBJECT_NAME_MAX_LENGTH);
	}
	//TODO: operator=

	/*
	CaType getType() const
	{
		return CaType::CaBuilding;
	}
	
	void getBoundingBox(BoundingBox& result) const
	{
		result.minVertex = minVertex;
		result.maxVertex = maxVertex;
	}*/
} BuildingDesc;

typedef struct BillboardDescStruct
{
	char name[OBJECT_NAME_MAX_LENGTH + 1];
	Point3D pivot;
	float width, height; 
	
	BillboardDescStruct()
	{
		name[0] = '\0';
	}
	BillboardDescStruct(const struct BillboardDescStruct& obj) : pivot(obj.pivot), width(obj.width), height(obj.height)
	{
		strncpy(name, obj.name, OBJECT_NAME_MAX_LENGTH);
	}
	
	/*
	CaType getType() const
	{
		return CaType::CaBillboard;
	}

	void getBoundingBox(BoundingBox& result) const
	{
		result.minVertex = minVertex;
		result.maxVertex = maxVertex;
	}*/
	//TODO: operator=
} BillboardDesc;

//Use a locator and its radius to represent an event
typedef struct EventDescStruct
{
	int eventId;
	char name[OBJECT_NAME_MAX_LENGTH + 1];
	
	//(Currently ignored) The target object corresponding to the event. e.g., a NPC billboard
	char target[OBJECT_NAME_MAX_LENGTH + 1];

	//The player position when trigger this event. e.g., in front of a NPC.
	Point3D playerPosition;

	//The radius of the event indicator shown in the scenes
	float radius;

	//char checksum[EVENT_CHECKSUM_LENGTH + 1]; //checksum is always the last field of the structure

	EventDescStruct()
	{
		name[0] = '\0';
		target[0] = '\0';
		//checksum[0] = '\0';
	}
	EventDescStruct(const struct EventDescStruct& obj) : playerPosition(obj.playerPosition), radius(obj.radius), eventId(obj.eventId)
	{
		strncpy(name, obj.name, OBJECT_NAME_MAX_LENGTH);
		strncpy(target, obj.target, OBJECT_NAME_MAX_LENGTH);
		//strncpy(checksum, obj.checksum, EVENT_CHECKSUM_LENGTH);
	}
	
	/*
	void getBoundingBox(BoundingBox& result) const
	{
		result.minVertex.x = playerPosition.x - radius;
		result.minVertex.y = playerPosition.y - radius;
		result.minVertex.z = playerPosition.z - radius;
		result.maxVertex.x = playerPosition.x + radius;
		result.maxVertex.y = playerPosition.y + radius;
		result.maxVertex.z = playerPosition.z + radius;
	}*/
	//TODO: operator=
} EventDesc;

typedef struct MaterialDescStruct
{
	Color ambient;
	Color diffuse;
	Color specular;
	float shininess; 
	//TODO: texture
} MaterialDesc;

typedef struct MaterialGroupDescStruct
{
	int beginIndex;	//the index (in a model) of the first face of this group
	int materialId;	
} MaterialGroupDesc;


#endif
