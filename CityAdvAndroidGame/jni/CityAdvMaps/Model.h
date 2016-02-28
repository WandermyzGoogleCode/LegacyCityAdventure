/*
 * Model.h
 *
 *  Created on: 2010-12-8
 *      Author: Wander
 */

#ifndef MODEL_H_
#define MODEL_H_

#include "IDrawable.h"

typedef unsigned char BYTE;

class Model
{
private:
	int vertexNum, faceNum, materialGroupNum;
	Point3D* vertexArray;
	Point3D* normalArray;
	//TODO Point2D* uvArray;
	TriangleIndexShort* vertexIndex;
	//TODO TriangleIndex* uvIndex;
	MaterialGroupDesc* materialGroups;	//indicate the first element index of each group. Assuming that all faces has material
	bool loaded;
	
public:
	Model() : vertexNum(0), faceNum(0), materialGroupNum(0), vertexArray(NULL), normalArray(NULL), vertexIndex(NULL), materialGroups(NULL), loaded(false) { }
	Model(const Model& obj);
	~Model();
	//TODO: operator=

	//create a new model with specified vertexNum and indexNum.
	void create(int vertexNum, int indexNum, int materialGroupNum);

	//load a model from file
	bool loadFromFile(const char* filePath);

	inline bool isLoaded() const
	{
		return loaded;
	}

	inline Point3D* getVertexArray() const
	{
		return vertexArray;
	}

	inline Point3D* getNormalArray() const
	{
		return normalArray;
	}

	inline TriangleIndexShort* getVertexIndex() const
	{
		return vertexIndex;
	}

	inline MaterialGroupDesc* getMaterialGroups() const
	{
		return materialGroups;
	}

	inline int getVertexNum() const
	{
		return vertexNum;
	}

	inline int getFaceNum() const
	{
		return faceNum;
	}

	inline int getMaterialGroupNum() const
	{
		return materialGroupNum;
	}

#ifndef MAYA_PLUGIN
	void draw(GameEngine& gameEngine) const;
#endif //MAYA_PLUGIN
};

#endif /* MODEL_H_ */
