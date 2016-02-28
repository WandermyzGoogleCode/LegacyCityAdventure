// Region.cpp : implementation file

#include "Region.h"

// Region
Region::Region()
{
	m_leftTop.m_x=m_leftTop.m_y=m_rightDown.m_x=m_rightDown.m_y=0;
}
Region::Region(float top_x, float top_y, float bottom_x, float bottom_y, bool isPoint)
{
	InitRegion(top_x, top_y, bottom_x, bottom_y, isPoint);
}
Region::~Region()
{
}
// Region member functions
void Region::InitRegion(float top_x, float top_y, float bottom_x, float bottom_y, bool isPoint)
{
	m_leftTop.m_x=top_x;
	m_leftTop.m_y=top_y;
	if (isPoint)
	{
		m_rightDown.m_x=bottom_x;
		m_rightDown.m_y=bottom_y;
	} 
	else
	{
		m_rightDown.m_x=top_x+bottom_x;
		m_rightDown.m_y=top_y+bottom_y;
	}
}
bool Region::IsOverlap(const Region &r)
{
	Point p1,p2;
	p1.m_x=r.m_rightDown.m_x; p1.m_y=r.m_leftTop.m_y;	//right top
	p2.m_x=r.m_leftTop.m_x; p2.m_y=r.m_rightDown.m_y;	//left down

	if(IsIn(r.m_leftTop) || IsIn(p1) || IsIn(p2) || IsIn(r.m_rightDown))
	{
		return true;
	}
	else
	{
		return (m_leftTop.m_x>=r.m_leftTop.m_x) && (m_rightDown.m_x<=r.m_rightDown.m_x)
			&& (r.m_leftTop.m_y>=m_leftTop.m_y) && (r.m_rightDown.m_y<=m_rightDown.m_y);
	}
}
