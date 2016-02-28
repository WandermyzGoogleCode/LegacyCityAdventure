#ifndef MATERIAL_PARSER_H
#define MATERIAL_PARSER_H

#include <map>
#include <vector>
#include <string>
#include <maya/MString.h>
#include <maya/MGlobal.h>

#include <CityAdvMaps/CaMapFileStructures.h>

class MaterialParser
{
private:
	std::map<std::string, int> nameToIdMap;
	std::vector<MaterialDesc> materials;

public:
	MaterialParser() { }

	bool parse(MString filePath);

	inline const std::vector<MaterialDesc>& getMaterials() const {return materials; }
	int getMaterialIdByName(MString name) const;
};


#endif //MATERIAL_PARSER_H