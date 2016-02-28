#ifndef KD_TREE_H_
#define KD_TREE_H_

#include "Region.h"
#include "ivector.h"
#include "stdlib.h"
#include "stdio.h"

#include "CityAdvMaps/CityAdvMap.h"

struct KD_Node
{
	Region m_regOverlap;
	ivector m_vecIn;
	bool IsSplitX;
	float m_nMedian;

	bool IsSearched;

	KD_Node *m_parent;
	KD_Node *m_left;
	KD_Node *m_right;

	KD_Node():m_parent(NULL), m_left(NULL), m_right(NULL), IsSearched(false), IsSplitX(false), m_nMedian(0){}
};
class CKDTree
{
private:
	const CityAdvMap *m_pMap;
	Region *m_regionArray;
	int m_size;
	float m_fHeight;
	float m_fWidth;

	KD_Node *m_root;
	int m_nNode;
	bool m_isBuilt;
	KD_Node *m_pRecentRoot;

	bool SplitX(KD_Node *parent, float x_median);
	bool SplitY(KD_Node *parent, float y_median);
	void BuildTree(KD_Node *parent);
	void DestroyTree(KD_Node *parent);

	void SearchTree(KD_Node *parent, Point &p, float radius, ivector &vec_r);
	void ResetTree(KD_Node *parent);

	int WriteNode(FILE *f, KD_Node *node, int &n);
	//only for mfc
	//void PaintTree(KD_Node *parent, CDC *pDC);
public:
	CKDTree(float height, float width);
	virtual ~CKDTree();

	void ReadFromFile(const char* fileName);
	void WriteToFile(const char* fileName);

	void BuildTreeFromMap(const CityAdvMap *map);

	bool IsEmpty() {return m_regionArray==NULL;}
	int Size() {return m_size;}
	Region* Array() {return m_regionArray;}

	bool IsBuilt() {return m_isBuilt;}
	void SearchTree(Point &p, float radius, ivector &vec_r);

	//only for mfc
	//void PaintTree(CDC *pDC);
	//void PaintResult(CDC *pDC, ivector &vec_r);
};

#endif
