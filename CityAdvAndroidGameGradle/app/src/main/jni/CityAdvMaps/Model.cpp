/*
 * Model.cpp
 *
 *  Created on: 2010-12-8
 *      Author: Wander
 */

#include "Model.h"
#include <log.h>
#include <stdio.h>

#ifndef MAYA_PLUGIN
#include <GLES/gl.h>
#include <glUtils.h>
#include <GameEngine.h>
#endif

Model::Model(const Model& obj) : vertexNum(obj.vertexNum), faceNum(obj.faceNum), materialGroupNum(obj.materialGroupNum), loaded(obj.loaded), vertexArray(NULL), vertexIndex(NULL), normalArray(NULL), materialGroups(NULL)
{
	//vertexNum = obj.vertexNum;
	//faceNum = obj.faceNum;
	//loaded = obj.loaded;

	if(obj.loaded)
	{
		create(vertexNum, faceNum, materialGroupNum);
		memcpy(vertexArray, obj.vertexArray, obj.vertexNum * sizeof(Point3D));
		memcpy(normalArray, obj.normalArray, obj.vertexNum * sizeof(Point3D));
		memcpy(vertexIndex, obj.vertexIndex, obj.faceNum * sizeof(TriangleIndex));
		memcpy(materialGroups, obj.materialGroups, obj.materialGroupNum * sizeof(MaterialGroupDesc));
	}
}

Model::~Model()
{
	if(vertexArray != NULL)
	{
		delete [] vertexArray;
		vertexArray = NULL;
	}

	if(normalArray != NULL)
	{
		delete [] normalArray;
		normalArray = NULL;
	}

	if(vertexIndex != NULL)
	{
		delete [] vertexIndex;
		vertexIndex = NULL;
	}

	if(materialGroups != NULL)
	{
		delete[] materialGroups;
		materialGroups = NULL;
	}
}

void Model::create(int vertexNum, int faceNum, int materialGroupNum)
{
	if(vertexArray != NULL)
	{
		delete [] vertexArray;
	}

	if(normalArray != NULL)
	{
		delete [] normalArray;
	}

	if(vertexIndex != NULL)
	{
		delete [] vertexIndex;
	}

	if(materialGroups != NULL)
	{
		delete[] materialGroups;
		materialGroups = NULL;
	}

	vertexArray = new Point3D[vertexNum];
	normalArray = new Point3D[vertexNum];
	vertexIndex = new TriangleIndexShort[faceNum];
	materialGroups = new MaterialGroupDesc[materialGroupNum];

	this->vertexNum = vertexNum;
	this->faceNum = faceNum;
	this->materialGroupNum = materialGroupNum;

	loaded = true;
}

bool Model::loadFromFile(const char* filePath)
{
	LOGI("Loading model: %s", filePath);
	FILE* fp = fopen(filePath, "rb");
	if(fp == NULL)
	{
		return false;
	}

	//check header
	char header[] = FILE_HEADER_MODEL;
	char* headerInFile = new char[strlen(header) + 1];

	fread(headerInFile, sizeof(char), strlen(header) + 1, fp);

	if(strcmp(header, headerInFile) != 0)
	{
		delete headerInFile;
		return false;	//error file header
	}

	delete headerInFile;
	headerInFile = NULL;

	//read number
	fread(&vertexNum, sizeof(int), 1, fp);
	fread(&faceNum, sizeof(int), 1, fp);
	fread(&materialGroupNum, sizeof(int), 1, fp);

	//vertex array
	vertexArray = new Point3D[vertexNum];
	fread(vertexArray, sizeof(Point3D), vertexNum, fp);

	//faces
	vertexIndex = new TriangleIndexShort[faceNum];
	fread(vertexIndex, sizeof(TriangleIndexShort), faceNum, fp);

	//normals
	normalArray = new Point3D[vertexNum];
	fread(normalArray, sizeof(Point3D), vertexNum, fp);

	//materials
	materialGroups = new MaterialGroupDesc[materialGroupNum];
	fread(materialGroups, sizeof(MaterialGroupDesc), materialGroupNum, fp);
	LOGI("Material Groups: %d", materialGroupNum);

	fclose(fp);

	loaded = true;

	return true;
}

#ifndef MAYA_PLUGIN
void Model::draw(GameEngine& gameEngine) const
{
	glFrontFace(GL_CCW);
	glCullFace(GL_BACK);

	glEnable(GL_CULL_FACE);

	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_NORMAL_ARRAY);


    glVertexPointer(3, GL_FLOAT, 0, getVertexArray());
    glNormalPointer(GL_FLOAT, 0, getNormalArray());
    checkGlError("pointer");
    //glDrawElements(GL_TRIANGLES, getFaceNum() * 3, GL_UNSIGNED_SHORT, getVertexIndex());

	for(int i = 0; i < materialGroupNum; i++)
	{

		int matId = materialGroups[i].materialId;
		MaterialDesc& desc = gameEngine.getCityAdvMapConst().getMaterials()[matId];

		glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, (GLfloat*)(&desc.ambient));
		glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, (GLfloat*)(&desc.diffuse));
		//LOGI("%d: %f, %f, %f", matId, desc.diffuse.r, desc.diffuse.g, desc.diffuse.b);
		glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, (GLfloat*)(&desc.specular));
		glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, desc.shininess);
		checkGlError("material");
		//LOGI("i = %d", i);
		int startIndex = materialGroups[i].beginIndex;
		int endIndex = (i == materialGroupNum - 1) ? faceNum : materialGroups[i + 1].beginIndex;
		/*
		if(endIndex - startIndex < 0) {
			LOGE("Draw %d - %d", startIndex, endIndex);
		}*/
		glDrawElements(GL_TRIANGLES, (endIndex - startIndex) * 3, GL_UNSIGNED_SHORT, vertexIndex + startIndex);
		checkGlError("draw");
	}

	glDisableClientState(GL_NORMAL_ARRAY);
	glDisableClientState(GL_VERTEX_ARRAY);
    glDisable(GL_CULL_FACE);
}
#endif
