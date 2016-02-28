/*
 * Texture.h
 *
 *  Created on: 2011-1-1
 *      Author: Wander
 */

#ifndef TEXTURE_H_
#define TEXTURE_H_

class Texture
{
private:

public:
	bool loadFromFile(const char* filePath);

#ifndef MAYA_PLUGIN
	void texBegin();
	void texEnd();
#endif
};

#endif /* TEXTURE_H_ */
