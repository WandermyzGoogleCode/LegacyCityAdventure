/*
 * Geometry.cpp
 *
 *  Created on: 2010-12-8
 *      Author: Wander
 */

#include <CityAdvMaps/Geometry.h>

Point2DStruct::Point2DStruct(const Point2DStruct& obj)
{
	x = obj.x;
	y = obj.y;
}

Point2DStruct& Point2DStruct::operator =(const Point2DStruct& obj)
{
	if(&obj != this)
	{
		x = obj.x;
		y = obj.y;
	}

	return *this;
}

Point3DStruct::Point3DStruct(const Point3DStruct& obj)
{
	x = obj.x;
	y = obj.y;
	z = obj.z;
}

Point3DStruct& Point3DStruct::operator =(const Point3DStruct& obj)
{
	if(&obj != this)
	{
		x = obj.x;
		y = obj.y;
		z = obj.z;
	}

	return *this;
}

BoundingBoxStruct::BoundingBoxStruct(const BoundingBoxStruct& obj)
{
	minVertex = obj.minVertex;
	maxVertex = obj.maxVertex;
}

BoundingBoxStruct& BoundingBoxStruct::operator =(const BoundingBoxStruct& obj)
{
	if(&obj != this)
	{
		minVertex = obj.minVertex;
		maxVertex = obj.maxVertex;
	}

	return *this;
}

