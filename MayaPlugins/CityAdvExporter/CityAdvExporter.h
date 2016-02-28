#ifndef CITY_ADV_EXPORTER_H
#define CITY_ADV_EXPORTER_H

#include <maya/MPxFileTranslator.h>
#include <maya/MObjectArray.h>
#include <maya/MStringArray.h>
#include <maya/MIntArray.h>
#include <maya/MDagPath.h>
#include <maya/MFnMesh.h>
#include <maya/MString.h>

#include <vector>
#include <map>
#include <set>
#include <string>

#include <CityAdvMaps/Geometry.h>
#include <CityAdvMaps/CityAdvMap.h>
#include <CityAdvMaps/EventPoint.h>
#include "MaterialParser.h"

//
// Edge info structure, from obj exporter, without modification
//
typedef struct EdgeInfo {
	int                 polyIds[2]; // Id's of polygons that reference edge
	int                 vertId;     // The second vertex of this edge
	struct EdgeInfo *   next;       // Pointer to next edge
	bool                smooth;     // Is this edge smooth
} * EdgeInfoPtr;

//from obj expoter, without modification
#define NO_SMOOTHING_GROUP      -1
#define INITIALIZE_SMOOTHING    -2
#define INVALID_ID              -1

class FaceIndexWithMaterial {
private:
	TriangleIndex faceIndex;
	int materialId;

public:
	FaceIndexWithMaterial(TriangleIndex& faceIndex, int& materialId) : faceIndex(faceIndex), materialId(materialId) { }
	inline const TriangleIndex& getIndex() const {return faceIndex; }
	inline int getMaterialId() const {return materialId; }
	inline boolean operator< (const FaceIndexWithMaterial& obj)
	{
		return materialId < obj.materialId;
	}
};

class CityAdvExporter : public MPxFileTranslator
{
private:
	
	MString fileName;	//the filename of the map, WITHOUT extension
	std::vector<Point3D> vertexArray;
	std::vector<Point3D> normalArray;
	std::vector<Point2D> textureArray;
	
	//std::map<std::string, std::vector<TriangleIndex> > caFaceMap;
	//std::map<std::string, std::vector<int>> caFaceMaterialMap;
	std::map<std::string, std::vector<FaceIndexWithMaterial> > caFaceMap;


	//TODO: uvFaceMap
	std::set<std::string> caObjectSet;	//transform node with caType
	std::set<std::string> materialToProcess;	//save all visited material to check baking textures

	std::vector<Building> caBuildingArray;
	std::vector<EventPoint> caEventPointArray;

	CityAdvMap cityAdvMap;

	MString lastCaObj;
	MString lastMaterialName;

	MaterialParser materialParser;

	//from obj exporter
	// counters
	int v,vt,vn;
	// offsets
	int voff,vtoff,vnoff;
	//options
	bool groups, ptgroups, materials, smoothing, normals;

	// Keeps track of all sets.
	//
	int numSets;
	MObjectArray *sets;

	// Keeps track of all objects and components.
	// The Tables are used to mark which sets each 
	// component belongs to.
	//
	MStringArray *objectNames;

	bool **polygonTablePtr;
	bool **vertexTablePtr;
	bool * polygonTable;
	bool * vertexTable;
	bool **objectGroupsTablePtr;

	// List of names of the mesh shapes that we export from maya
	MStringArray	objectNodeNamesArray;

	// We have to do 2 dag iterations so keep track of the
	// objects found in the first iteration by this index.
	//
	int objectId;
	int objectCount;

	// Used to keep track of Maya groups (transform DAG nodes) that
	// contain objects being exported
	MStringArray	transformNodeNameArray;

	// Used to determine if the last set(s) written out are the same
	// as the current sets to be written. We don't need to write out
	// sets unless they change between components. Same goes for
	// materials.
	// TODO: maybe useful for check groups
	MIntArray *lastSets;
	MIntArray *lastMaterials;

	// Edge lookup table (by vertex id) and smoothing group info
	//
	EdgeInfoPtr *   edgeTable;
	int *           polySmoothingGroups;
	int             edgeTableSize;
	int             nextSmoothingGroup;
	int             currSmoothingGroup;
	bool            newSmoothingGroup;

	//export obj file for debug
	FILE *fp;

	MStatus exportAll();
	void initializeSetsAndLookupTables(bool exportAll);
	void recFindTransformDAGNodes( MString&, MIntArray& );
	void freeLookupTables();
	bool lookup( MDagPath&, int, int, bool );

	//edge lookup methods
	void buildEdgeTable( MDagPath& );
	void addEdgeInfo( int, int, bool );
	EdgeInfoPtr findEdgeInfo( int, int );
	void destroyEdgeTable();
	bool smoothingAlgorithm( int, MFnMesh& );

	MStatus OutputPolygons( MDagPath&, MObject& );
	void outputSetsAndGroups ( MDagPath&, int, bool, int );
	void exportCaTypeElements();
	void exportCaBuilding(const MString& name);
	void exportCaModels();
	void exportCaEvents();

public:
	CityAdvExporter() {};
	virtual ~CityAdvExporter () {};
	static void* creator();
	MStatus writer ( const MFileObject& file, const MString& optionsString, FileAccessMode mode );
	bool haveReadMethod () const;
	bool haveWriteMethod () const;
	MString defaultExtension () const;
	MFileKind identifyFile ( const MFileObject& fileName, const char* buffer, short size) const;
};

#endif