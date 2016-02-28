/*
 * EventPoint.h
 *
 *  Created on: 2010-12-16
 *      Author: Wander
 */

#ifndef EVENTPOINT_H_
#define EVENTPOINT_H_

#include <CityAdvMaps/IDrawable.h>

class EventPoint : public IDrawable
{
private:
	EventDesc desc;

public:
	inline EventDesc& getDesc() {return desc; }

	CaType getType() const {return CaEvent; }
	void getBoundingBox(BoundingBox& result) const
	{
		result.minVertex.x = desc.playerPosition.x - desc.radius;
		result.minVertex.y = desc.playerPosition.y - desc.radius;
		result.minVertex.z = desc.playerPosition.z - desc.radius;
		result.maxVertex.x = desc.playerPosition.x + desc.radius;
		result.maxVertex.y = desc.playerPosition.y + desc.radius;
		result.maxVertex.z = desc.playerPosition.z + desc.radius;
	}

	int isHitTestEnabled() const
	{
		return 0;
	}

#ifndef MAYA_PLUGIN
	void draw(GameEngine& gameEngine) const;
#endif //MAYA_PLUGIN
};

#endif /* EVENTPOINT_H_ */
