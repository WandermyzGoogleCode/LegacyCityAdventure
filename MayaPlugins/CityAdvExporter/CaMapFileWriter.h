#ifndef CA_MAP_FILE_WRITER_H
#define CA_MAP_FILE_WRITER_H

#include <maya\MStatus.h>
#include <maya\MString.h>
#include <CityAdvMaps/CityAdvMap.h>
#include <CityAdvMaps/CaMapFileStructures.h>

class CaMapFileWriter
{
private:
	MStatus writeMapFile(CityAdvMap& cityAdvMap, MString& fileName);
	MStatus writeModelFile(Model& model, MString& fileName);
	MStatus writeKdTree(CityAdvMap& cityAdvMap, MString& fileName);

public:
	CaMapFileWriter() { }
	MStatus write(CityAdvMap& cityAdvMap, MString& fileName);
};


#endif