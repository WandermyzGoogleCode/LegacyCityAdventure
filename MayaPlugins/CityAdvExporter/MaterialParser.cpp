#include "MaterialParser.h"
#include <fstream>
#include "stdio.h"
using namespace std;

#define MATERIAL_PARSER_HEADER_BUFFER_SIZE 64
#define MATERIAL_PARSER_LINE_BUFFER_SIZE 1024

bool MaterialParser::parse(MString filePath)
{
	nameToIdMap.clear();
	materials.clear();

	//execute obj exporter
	MString cmd = "file -force -options \"groups=1;ptgroups=1;materials=1;smoothing=1;normals=1\" -type \"OBJexport\" -pr -ea \"" + filePath + ".mattemp.obj" + "\"";
	MStatus cmdResult = MGlobal::executeCommand(cmd, true);
	
	if(cmdResult == MStatus::kFailure)
	{
		return false;
	}
	
	
	FILE* fp = fopen((filePath + ".mattemp.mtl").asChar(), "r");
	if(fp == NULL)
	{
		MGlobal::displayError(MString("No material library found: ") + (filePath + ".mattemp.mtl").asChar());
		return false;
	}

	MaterialDesc* matBuffer = NULL;
	string currentName;
	char headerBuffer[MATERIAL_PARSER_HEADER_BUFFER_SIZE];
	char lineBuffer[MATERIAL_PARSER_LINE_BUFFER_SIZE];

	while(fscanf(fp, "%s", headerBuffer) != EOF)
	{
		if(strcmp(headerBuffer, "newmtl") == 0)
		{
			if(matBuffer != NULL)
			{
				materials.push_back(*matBuffer);		//copy data into vector
				nameToIdMap[currentName] = materials.size() - 1;	
				delete matBuffer;
			}

			matBuffer = new MaterialDesc();
			//initialize
			matBuffer->ambient.r = matBuffer->ambient.g = matBuffer->ambient.b = 0.0f;
			matBuffer->ambient.a = 1.0f;
			matBuffer->diffuse.r = matBuffer->diffuse.g = matBuffer->diffuse.b = 0.0f;
			matBuffer->diffuse.a = 1.0f;
			matBuffer->specular.r = matBuffer->specular.g = matBuffer->specular.b = 0.0f;
			matBuffer->specular.a = 1.0f;
			matBuffer->shininess = 0.0f;

			fgets(lineBuffer, MATERIAL_PARSER_LINE_BUFFER_SIZE, fp);
			currentName = lineBuffer;
			while(currentName.length() > 0 && (currentName[0] == ' ' || currentName[0] == '\n' || currentName[0] == '\r'))
			{
				currentName.erase(0, 1);
			}
			while(currentName.length() > 0 && (currentName[currentName.size() - 1] == '\n' || currentName[currentName.size() - 1] == '\r'))
			{
				currentName.erase(currentName.size() - 1);
			}

			MGlobal::displayInfo(MString("Parsing Material: ") + currentName.c_str());
		}
		else if(strcmp(headerBuffer, "Kd") == 0)
		{
			fscanf(fp, "%f %f %f", &(matBuffer->diffuse.r), &(matBuffer->diffuse.g), &(matBuffer->diffuse.b));
			fgets(lineBuffer, MATERIAL_PARSER_LINE_BUFFER_SIZE, fp);
		}
		else if(strcmp(headerBuffer, "Ka") == 0)
		{
			fscanf(fp, "%f %f %f", &(matBuffer->ambient.r), &(matBuffer->ambient.g), &(matBuffer->ambient.b));
			fgets(lineBuffer, MATERIAL_PARSER_LINE_BUFFER_SIZE, fp);
		}
		else if(strcmp(headerBuffer, "Tf") == 0)
		{
			float tr, tg, tb;
			fscanf(fp, "%f %f %f", &tr, &tg, &tb);
			float alpha = (tr + tg + tb) / 3.0f;
			matBuffer->ambient.a = matBuffer->diffuse.a = matBuffer->specular.a = alpha;

			fgets(lineBuffer, MATERIAL_PARSER_LINE_BUFFER_SIZE, fp);
		}
		else if(strcmp(headerBuffer, "Ns") == 0)
		{
			fscanf(fp, "%f", &(matBuffer->shininess));
			fgets(lineBuffer, MATERIAL_PARSER_LINE_BUFFER_SIZE, fp);
		}
		else
		{
			//skip this line
			fgets(lineBuffer, MATERIAL_PARSER_LINE_BUFFER_SIZE, fp);
		}
	}

	if(matBuffer != NULL)
	{
		materials.push_back(*matBuffer);
		nameToIdMap[currentName] = materials.size() - 1;	//copy data into vector
		delete matBuffer;
	}

	/*
	for(int i = 0; i < materials.size(); i++)
	{
		MGlobal::displayInfo(MString("Material ") + i);
		MGlobal::displayInfo(MString("\tDiffuse: ") + materials[i].diffuse.r + ", " + materials[i].diffuse.g + ", " + materials[i].diffuse.b + ", " + materials[i].diffuse.a);
		MGlobal::displayInfo(MString("\tAmbient: ") + materials[i].ambient.r + ", " + materials[i].ambient.g + ", " + materials[i].ambient.b + ", " + materials[i].ambient.a);
		MGlobal::displayInfo(MString("\tSpecular: ") + materials[i].specular.r + ", " + materials[i].specular.g + ", " + materials[i].specular.b + ", " + materials[i].specular.a);
		MGlobal::displayInfo(MString("\tShininess: ") + materials[i].shininess);
	}*/

	//delete temp file
	remove((filePath + ".mattemp.obj").asChar());
	//TODO: remove(filePath + ".mattemp.mtl");

	return true;
}

int MaterialParser::getMaterialIdByName(MString name) const
{
	string nameStr(name.asChar());
	
	map<string, int>::const_iterator it = nameToIdMap.find(nameStr);
	
	if(it != nameToIdMap.end())
	{
		return it->second;
	}
	else
	{
		return -1;
	}
}