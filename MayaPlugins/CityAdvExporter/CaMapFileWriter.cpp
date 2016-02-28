#include "CaMapFileWriter.h"
#include <CityAdvMaps/CaMapFileStructures.h>
#include <stdio.h>
#include <KDTree/KDTree.h>

MStatus CaMapFileWriter::write(CityAdvMap& cityAdvMap, MString& fileName)
{
	//write map file
	MStatus status;
	status = writeMapFile(cityAdvMap, fileName + "." + FILE_EXT_MAP);
	
	if(status == MStatus::kFailure)
	{
		return MStatus::kFailure;
	}

	//write models for buildings
	for(int i = 0; i < cityAdvMap.getBuildingNum(); i++)
	{
		MString modelFileName = fileName + "_" + MString(cityAdvMap.getBuildings()[i].getDesc().name) + "." + FILE_EXT_MODEL;
		MStatus modelStatus = writeModelFile(cityAdvMap.getBuildings()[i].getModel(), modelFileName);
		if(modelStatus == MStatus::kFailure)
		{
			return MStatus::kFailure;
		}
	}

	//write kdtree
	writeKdTree(cityAdvMap, fileName + "." + FILE_EXT_KDTREE);

	return MStatus::kSuccess;
}

MStatus CaMapFileWriter::writeMapFile(CityAdvMap& cityAdvMap, MString& fileName)
{
	FILE* fp = fopen(fileName.asChar(), "wb");
	
	if(fp == NULL)
	{
		return MStatus::kFailure;
	}


	//write a file about event, to instruct how a java-based qr-code generator read events from the map file
	FILE* fpHint = fopen((fileName + "." + FILE_EXT_EVENT_HINT).asChar(), "w");
	if(fpHint == NULL)
	{
		return MStatus::kFailure;
	}

	//header in hint
	fprintf(fpHint, "%s\n", FILE_HEADER_MAP);
	fprintf(fpHint, "%d\n", sizeof(EventDesc));

	//file header
	char header[] = FILE_HEADER_MAP;
	fwrite(header, sizeof(char), strlen(header) + 1, fp);

	//bounding box
	Point3D minVertex = cityAdvMap.getBoundingBoxMinVertex();
	Point3D maxVertex = cityAdvMap.getBoundingBoxMaxVertex();
	fwrite(&minVertex, sizeof(Point3D), 1, fp);
	fwrite(&maxVertex, sizeof(Point3D), 1, fp);

	//TODO: map name, description, ...
	
	//building num
	int buildingNum = cityAdvMap.getBuildingNum();
	fwrite(&buildingNum, sizeof(int), 1, fp);

	//building content
	for(int i = 0; i < buildingNum; i++)
	{
		fwrite(&(cityAdvMap.getBuildings()[i].getDesc()), sizeof(BuildingDesc), 1, fp);
	}

	//event num
	int eventNum = cityAdvMap.getEventPointNum();
	fwrite(&eventNum, sizeof(int), 1, fp);

	//event points
	for(int i = 0; i < eventNum; i++)
	{
		EventDesc desc = cityAdvMap.getEventPoints()[i].getDesc();
		long int offset = ftell(fp);	
		//long int offsetChecksum = offset + ((unsigned char*)desc.checksum - (unsigned char*)(&desc));	//checksum is always at the end of the structure
		int id = desc.eventId;
		fprintf(fpHint, "%d %ld %s\n", id, offset, desc.name);

		fwrite(&(desc), sizeof(EventDesc), 1, fp);
	}

	//material num
	int materialNum = cityAdvMap.getMaterialNum();
	fwrite(&materialNum, sizeof(int), 1, fp);

	//materials
	/*for(int i = 0; i < materialNum, i++)
	{
		fwrite(&(cityAdvMap.getMaterials()[i]), sizeof(MaterialDesc), 1, fp);
	}*/
	fwrite(cityAdvMap.getMaterials(), sizeof(MaterialDesc), materialNum, fp);

	fclose(fp);
	fclose(fpHint);

	return MStatus::kSuccess;
}

MStatus CaMapFileWriter::writeModelFile(Model& model, MString& fileName)
{
	FILE* fp = fopen(fileName.asChar(), "wb");

	if(fp == NULL)
	{
		return MStatus::kFailure;
	}

	//file header
	char header[] = FILE_HEADER_MODEL;
	fwrite(header, sizeof(char), strlen(header) + 1, fp);

	//vertex num, face num, material groups num
	int vertexNum = model.getVertexNum();
	int faceNum = model.getFaceNum();
	int materialGroupNum = model.getMaterialGroupNum();
	fwrite(&vertexNum, sizeof(int), 1, fp);
	fwrite(&faceNum, sizeof(int), 1, fp);
	fwrite(&materialGroupNum, sizeof(int), 1, fp);

	//vertex array
	fwrite(model.getVertexArray(), sizeof(Point3D), vertexNum, fp);

	//faces
	fwrite(model.getVertexIndex(), sizeof(TriangleIndexShort), faceNum, fp);

	//normals
	fwrite(model.getNormalArray(), sizeof(Point3D), vertexNum, fp);

	//material groups
	fwrite(model.getMaterialGroups(), sizeof(MaterialGroupDesc), materialGroupNum, fp);

	fclose(fp);
}

MStatus CaMapFileWriter::writeKdTree(CityAdvMap& cityAdvMap, MString& fileName)
{
	float width = cityAdvMap.getBoundingBoxMaxVertex().x - cityAdvMap.getBoundingBoxMinVertex().x;
	float height = cityAdvMap.getBoundingBoxMaxVertex().z - cityAdvMap.getBoundingBoxMinVertex().z;

	CKDTree kdtree(width, height);
	kdtree.BuildTreeFromMap(&cityAdvMap);
	kdtree.WriteToFile(fileName.asChar());

	return MStatus::kSuccess;
}