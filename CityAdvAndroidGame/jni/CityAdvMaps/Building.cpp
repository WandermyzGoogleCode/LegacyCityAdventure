/*
 * Building.cpp
 *
 *  Created on: 2010-12-8
 *      Author: Wander
 */

#include "Building.h"

#ifndef MAYA_PLUGIN
#include <log.h>
#endif

#ifndef MAYA_PLUGIN
void Building::draw(GameEngine& gameEngine) const
{
	if(!model.isLoaded())
	{
		LOGE("Model not loaded! %s", desc.name);
		return;
	}
	model.draw(gameEngine);
}
#endif
