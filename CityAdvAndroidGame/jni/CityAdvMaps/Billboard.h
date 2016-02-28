/*
 * BillBoard.h
 *
 *  Created on: 2011-1-1
 *      Author: Wander
 */

#ifndef BILLBOARD_H_
#define BILLBOARD_H_

#include "Texture.h"
#include "IDrawable.h"

/*
$r = `polyPlane -ch on -o on -w 3 -h 4 -sw 1 -sh 1 -cuv 2`;
$obj = $r[0];
setAttr ($obj + ".rotateZ") 90;
setAttr ($obj + ".rotateY") -90;
setAttr ($obj + ".translateY") 2;
addAttr -ln "caType"  -at "enum" -en "CaNone:CaBuilding:CaBillboard:CaEvent:CaWall" $obj;
setAttr ($obj + ".caType", 2);
setAttr -l true ($obj + ".caType");
rename $obj ("bill_"+$obj);

$mat = `shadingNode -asShader phong`;
sets -renderable true -noSurfaceShader true -empty -name ($mat + "SG");
connectAttr -f ($mat + ".outColor") ($mat + "SG.surfaceShader");
rename $mat ("bill_" + $obj + "_mat");


//how to read file path
$billTrans = "bill_pPlane1";
$billShapes = `listRelatives -c -typ mesh $billTrans`;
$billShape = $billShapes[0];
$billShadingGroups = `listConnections -t shadingEngine $billShape`;
$billShadingGroup = $billShadingGroups[0];
$billMaterials = `listConnections -t phong $billShadingGroup`;
$billMat = $billMaterials[0];
$billFiles = `listConnections -d false -s true -t file $billMat`;
$billFile = $billFiles[0];
$billPath = `getAttr file4.fileTextureName`;

 */


class Billboard : public IDrawable
{
private:
	BillboardDesc desc;
	Texture texture;

public:
	inline Texture& getModel() {return texture; }
	inline BillboardDesc& getDesc() {return desc; }

	CaType getType() const
	{
		return CaBillboard;
	}

	void getBoundingBox(BoundingBox& result) const
	{
		//TODO
		return BoundingBox();
	}

	int isHitTestEnabled() const
	{
		return 0;
	}

#ifndef MAYA_PLUGIN
	void draw(GameEngine& gameEngine) const;
#endif //MAYA_PLUGIN
};

#endif /* BILLBOARD_H_ */
