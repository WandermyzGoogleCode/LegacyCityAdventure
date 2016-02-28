#ifndef REGION_H_
#define REGION_H_
// Region command target

struct Point
{
	float m_x;
	float m_y;
	Point() : m_x(0), m_y(0) {}
	Point(float x, float y) : m_x(x), m_y(y) {}
};

class Region
{
private:
	Point m_leftTop;
	Point m_rightDown;
public:
	Region();
	Region(float top_x, float top_y, float bottom_x, float bottom_y, bool isPoint);
	virtual ~Region();
	//access methods
	float top_x() {return m_leftTop.m_x;}
	float top_y() {return m_leftTop.m_y;}
	float bottom_x() {return m_rightDown.m_x;}
	float bottom_y() {return m_rightDown.m_y;}
	void InitRegion(float top_x, float top_y, float bottom_x, float bottom_y, bool isPoint);
	float width() {return m_rightDown.m_x-m_leftTop.m_x;}
	float height() {return m_rightDown.m_y-m_leftTop.m_y;}
	//calculate methods
	bool IsIn(Point p)
	{
		return (p.m_x>=m_leftTop.m_x) && (p.m_x<=m_rightDown.m_x)
			&& (p.m_y>=m_leftTop.m_y) && (p.m_y<=m_rightDown.m_y);
	}
	bool IsOverlap(const Region &r);
	bool IsSeen(Point p, float expand)
	{
		return Region(m_leftTop.m_x-expand, m_leftTop.m_y-expand, m_rightDown.m_x+expand, m_rightDown.m_y+expand, true).IsIn(p);
	}
	Point Center()
	{
		return Point((float)((m_leftTop.m_x+m_rightDown.m_x)/2.0), (float)((m_leftTop.m_y+m_rightDown.m_y)/2.0));
	}
};
#endif