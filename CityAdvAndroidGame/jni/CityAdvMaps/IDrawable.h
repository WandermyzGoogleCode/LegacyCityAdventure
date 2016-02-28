/*
 * IDrawable.h
 *
 *  Created on: 2010-12-8
 *      Author: Wander
 */

#ifndef IDRAWABLE_H_
#define IDRAWABLE_H_

#include "CaMapFileStructures.h"

class GameEngine;

class IDrawable
{
public:
	virtual CaType getType() const = 0;
	virtual void getBoundingBox(BoundingBox& result) const = 0;
	virtual int isHitTestEnabled() const = 0;

#ifndef MAYA_PLUGIN
	virtual void draw(GameEngine& gameEngine) const = 0;
#endif //MAYA_PLUGIN
};

#endif /* IDRAWABLE_H_ */
