#ifndef GEOMETRY_H
#define GEOMETRY_H

typedef unsigned short USHORT;

typedef struct Point2DStruct
{
	float x;
	float y;
	Point2DStruct() { }
	Point2DStruct(float x, float y) : x(x), y(y) { }
	Point2DStruct(const Point2DStruct& obj);
	Point2DStruct& operator=(const Point2DStruct& obj);
	inline int isZero() const {return x == 0 && y == 0;}
} Point2D;

typedef struct Point3DStruct
{
	float x;
	float y;
	float z;
	Point3DStruct() { }
	Point3DStruct(float x, float y, float z) : x(x), y(y), z(z) { }
	Point3DStruct(const Point3DStruct& obj);
	Point3DStruct& operator=(const Point3DStruct& obj);
	inline int isZero() const {return x == 0 && y == 0 && z == 0;}
} Point3D;

typedef struct BoundingBoxStruct
{
	Point3D minVertex;
	Point3D maxVertex;
	BoundingBoxStruct() { }
	BoundingBoxStruct(const Point3D& minVertex, const Point3D& maxVertex) : minVertex(minVertex), maxVertex(maxVertex) { }
	BoundingBoxStruct(const BoundingBoxStruct& obj);
	BoundingBoxStruct& operator=(const BoundingBoxStruct& obj);

	inline float getWidth() const {return maxVertex.x - minVertex.x; }
	inline float getLength() const {return maxVertex.z - minVertex.z; }
	inline float getHeight() const {return maxVertex.y - minVertex.y; }
} BoundingBox;

typedef struct TriangleIndexStruct
{
	int v1;
	int v2;
	int v3;
	TriangleIndexStruct() { }
	TriangleIndexStruct(int v1, int v2, int v3) : v1(v1), v2(v2), v3(v3) { }
} TriangleIndex;

typedef struct ColorStruct
{
	float r;
	float g;
	float b;
	float a;
	ColorStruct() { }
	ColorStruct(float r, float g, float b, float a) : r(r), g(g), b(b), a(a) { }
} Color;

//for OpenGL ES
typedef struct TriangleIndexShortStruct
{
	USHORT v1;
	USHORT v2;
	USHORT v3;
	TriangleIndexShortStruct() { }
	TriangleIndexShortStruct(USHORT v1, USHORT v2, USHORT v3) : v1(v1), v2(v2), v3(v3) { }

} TriangleIndexShort;
#endif
