/*
 * Building.h
 *
 *  Created on: 2010-12-8
 *      Author: Wander
 */

#ifndef BUILDING_H_
#define BUILDING_H_

#include "Model.h"
#include "IDrawable.h"

class Building : public IDrawable
{
private:
	BuildingDesc desc;
	Model model;

public:
	Building() { }
	Building(const Building& obj) : desc(obj.desc), model(obj.model)
	{
		//use default copy constructor
	}

	inline Model& getModel() {return model; }
	inline BuildingDesc& getDesc() {return desc; }

	CaType getType() const
	{
		return CaBuilding;
	}

	void getBoundingBox(BoundingBox& result) const
	{
		result.minVertex = desc.minVertex;
		result.maxVertex = desc.maxVertex;
	}

	int isHitTestEnabled() const
	{
		return desc.hitTestEnabled;
	}

#ifndef MAYA_PLUGIN
	void draw(GameEngine& gameEngine) const;
#endif //MAYA_PLUGIN
};

#endif /* BUILDING_H_ */
