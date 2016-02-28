// KDTree.cpp : implementation file
//

#include "KDTree.h"

#include "time.h"

typedef unsigned int uint;

// CKDTree

CKDTree::CKDTree(float height, float width) : m_regionArray(NULL), m_size(0),
		m_root(NULL), m_pRecentRoot(NULL), m_pMap(0),
		m_nNode(0), m_isBuilt(false),
		m_fHeight(height),m_fWidth(width)
{
	srand((unsigned int)time(0));
}
CKDTree::~CKDTree()
{
	if (m_root!=NULL)
	{
		DestroyTree(m_root);
		delete m_root;
		m_root=NULL;
	}
	if (m_regionArray!=NULL)
	{
		delete [] m_regionArray;
		m_regionArray=NULL;
	}
	m_size=0;
	m_pMap=0;
}
// CKDTree member functions
// read KDTree from text file
// file struction
/*
 * number of blocks
 * top_x, top_y, bottom_x, bottom_y of one block
 * number of KD-Tree's node
 * sequence number of one node
 * top_x, top_y, bottom_x, bottom_y of the region overlapped by this node
 * number of region it contains and number of these regions
 * whether it is splited by x, and median value
 * left child right child
 *
 */
void CKDTree::ReadFromFile(const char* fileName)
{
	FILE *f=fopen(fileName,"r+");
	//for blocks
	fscanf(f,"%d",&m_size);
	if (m_regionArray!=NULL)
	{
		delete [] m_regionArray;
		m_regionArray=NULL;
	}
	m_regionArray=new Region[m_size];
	float top_x, top_y, bottom_x, bottom_y;
	for (int i=0; i<m_size; ++i)
	{
		fscanf(f,"%f%f%f%f",&top_x,&top_y,&bottom_x,&bottom_y);
		m_regionArray[i].InitRegion(top_x, top_y, bottom_x, bottom_y, true);
	}
	//for KD-tree
	if (m_root!=NULL)
	{
		DestroyTree(m_root);
		m_root=NULL;
		m_nNode=0;
	}
	fscanf(f,"%d",&m_nNode);
	KD_Node **n_pool=new KD_Node*[m_nNode];
	int *left_child=new int[m_nNode];
	int *right_child=new int[m_nNode];
	for(int i=0; i<m_nNode; ++i)
	{
		int num=0;
		fscanf(f,"%d",&num);
		n_pool[num]=new KD_Node();
		float tx, ty, bx, by;
		fscanf(f,"%f%f%f%f",&tx,&ty,&bx,&by);
		n_pool[num]->m_regOverlap.InitRegion(tx, ty, bx, by, true);
		int cnt=0, tmp=0;
		fscanf(f,"%d",&cnt);
		for(int i=0; i<cnt; ++i)
		{
			fscanf(f,"%d",&tmp);
			n_pool[num]->m_vecIn.push_back(tmp);
		}
		fscanf(f,"%d%f",&tmp,&(n_pool[num]->m_nMedian));
		n_pool[num]->IsSplitX=(tmp==1);
		fscanf(f,"%d%d",&left_child[num],&right_child[num]);
	}
	for(int i=0; i<m_nNode; ++i)
	{
		if(left_child[i]!=-1)
		{
			n_pool[i]->m_left=n_pool[left_child[i]];
			n_pool[left_child[i]]->m_parent=n_pool[i];
		}
		if(right_child[i]!=-1)
		{
			n_pool[i]->m_right=n_pool[right_child[i]];
			n_pool[right_child[i]]->m_parent=n_pool[i];
		}
	}
	for (int i=0; i<m_nNode; ++i)
		if (n_pool[i]->m_parent==NULL)
		{
			m_root=n_pool[i];
			break;
		}
		m_isBuilt=true;

		fclose(f);
		delete [] n_pool;
		delete [] left_child;
		delete [] right_child;
}

int CKDTree::WriteNode(FILE* f, KD_Node *node, int &n)
{
	if (node==NULL)
	{
		return -1;
	}

	int left_child=WriteNode(f, node->m_left, n);
	int right_child=WriteNode(f, node->m_right, n);
	fprintf(f,"%d\n",n);
	fprintf(f,"%f %f %f %f\n",node->m_regOverlap.top_x(),node->m_regOverlap.top_y(),
			node->m_regOverlap.bottom_x(),node->m_regOverlap.bottom_y());
	fprintf(f,"%d ",node->m_vecIn.size());
	for(int i=0; i<node->m_vecIn.size(); ++i)
		fprintf(f,"%d ",node->m_vecIn[i]);
	fprintf(f,"\n");
	fprintf(f,"%d %f\n",(node->IsSplitX ? 1 : 0),node->m_nMedian);
	fprintf(f,"%d %d\n",left_child,right_child);

	return n++;
}

void CKDTree::WriteToFile(const char* fileName)
{
	if (m_regionArray==NULL)
	{
		return;
	}
	FILE *f=fopen(fileName,"w+");
	//about blocks
	fprintf(f,"%d\n",m_size);
	for (int i=0; i<m_size; ++i)
	{
		fprintf(f,"%f %f %f %f\n",m_regionArray[i].top_x(),m_regionArray[i].top_y()
				,m_regionArray[i].bottom_x(),m_regionArray[i].bottom_y());
	}
	//about KD-tree
	if(m_root==NULL)
	{
		fclose(f);
		return;
	}
	fprintf(f,"%d\n",m_nNode);
	int n=0;
	WriteNode(f, m_root, n);
	fclose(f);
}

//construct a k-d tree dynamicly
void CKDTree::BuildTreeFromMap(const CityAdvMap *map)
{
	//clear old tree and resource
	if(m_regionArray!=NULL)
	{
		delete [] m_regionArray;
		m_regionArray=NULL;
	}
	if (m_root!=NULL)
	{
		DestroyTree(m_root);
		delete m_root;
		m_root=NULL;
	}
	//fill region array
	/*
	m_regionArray=new Region[map->getBuildingNum()];
	for(int i=0; i<map->getBuildingNum(); ++i)
	{
		Point3D vMax=map->getBuildings()[i].getDesc().maxVertex;
		Point3D vMin=map->getBuildings()[i].getDesc().minVertex;
		m_regionArray[i].InitRegion(vMin.x, vMin.z, vMax.x, vMax.z, true);
	}*/

	//new interface, by Wander
	m_regionArray = new Region[map->getDrawbleObjectNum()];
	for(int i = 0; i < map->getDrawbleObjectNum(); ++i)
	{
		BoundingBox boundingBox;
		map->getDrawbleObject(i)->getBoundingBox(boundingBox);
		m_regionArray[i].InitRegion(boundingBox.minVertex.x, boundingBox.minVertex.z, boundingBox.maxVertex.x, boundingBox.maxVertex.z, true);
	}

	//build the tree
	m_root=new KD_Node();
	m_root->m_regOverlap.InitRegion(0.0f, 0.0f, (float)m_fWidth, (float)m_fHeight, true);
	for(int i=0; i<m_size; ++i)
		m_root->m_vecIn.push_back(i);
	++m_nNode;
	BuildTree(m_root);
	//only for mfc
	//TRACE("CKDTree::BuildTree : total node=%d\n",m_nNode);
	m_isBuilt=true;
	m_pRecentRoot=NULL;
	m_pMap=map;
}

void CKDTree::BuildTree(KD_Node *parent)
{
	int nSize=parent->m_vecIn.size();
	//terminate conditions : size<=3
	if (nSize<=3)
		return;
	//calculate median x, y;
	float x_median=0;
	float y_median=0;
	for(int i=0; i<nSize; ++i)
	{
		x_median+=m_regionArray[parent->m_vecIn[i]].Center().m_x;
		y_median+=m_regionArray[parent->m_vecIn[i]].Center().m_y;
	}
	x_median/=nSize;
	y_median/=nSize;
	//calculate variance along x, y;
	float x_sigma=0; 
	float y_sigma=0;
	float x_tmp=0, y_tmp=0;
	for (int i=0; i<nSize; ++i)
	{
		x_tmp=x_median-m_regionArray[parent->m_vecIn[i]].Center().m_x;
		y_tmp=y_median-m_regionArray[parent->m_vecIn[i]].Center().m_y;
		x_sigma+=x_tmp*x_tmp;
		y_sigma+=y_tmp*y_tmp;
	}
	//split x
	if (x_sigma>=y_sigma)
	{
		if(!SplitX(parent, x_median))
			SplitY(parent, y_median);
	}
	//split y
	else
	{
		if (!SplitY(parent, y_median))
			SplitX(parent,x_median);
	}
	//recursive call this function
	if(parent->m_left!=NULL && parent->m_right!=NULL)
	{
		BuildTree(parent->m_left);
		BuildTree(parent->m_right);
	}
}

bool CKDTree::SplitX(KD_Node *parent, float x_median)
{
	float x_left=parent->m_regOverlap.top_x();
	float x_right=parent->m_regOverlap.bottom_x();
	float y_low=parent->m_regOverlap.top_y();
	float y_high=parent->m_regOverlap.bottom_y();
	int nSize=parent->m_vecIn.size();
	bool isCollide=true;

	float x_delta=x_right-x_median;
	float x_delta_t=x_median-x_left;
	if(x_delta<x_delta_t)
		x_delta=x_delta_t;
	float x_tmp=0;
	//collision detection
	for(int i=0; i<=x_delta+1; ++i)
	{
		//add delta
		x_tmp=x_median+i;
		isCollide=false;
		for(float j=y_low; j<=y_high; ++j)
		{
			if (x_tmp>x_right)
				break;
			for(int k=0; k<nSize; ++k)
				if (m_pMap->getDrawbleObject(parent->m_vecIn[k])->isHitTestEnabled() && 
						m_regionArray[parent->m_vecIn[k]].IsIn(Point(x_tmp,j)))
				{
					isCollide=true;
					break;
				}
				if (isCollide)
					break;
		}
		if(!isCollide)
			break;
		//minus delta
		x_tmp=x_median-i;
		isCollide=false;
		for(float j=y_low; j<=y_high; ++j)
		{
			if (x_tmp<x_left)
				break;
			for(int k=0; k<nSize; ++k)
				if (m_pMap->getDrawbleObject(parent->m_vecIn[k])->isHitTestEnabled() && 
						m_regionArray[parent->m_vecIn[k]].IsIn(Point(x_tmp,j)))
				{
					isCollide=true;
					break;
				}
				if (isCollide)
					break;
		}
		if(!isCollide)
			break;
	}
	//actually split the node
	if(!isCollide)
	{
		parent->m_left=new KD_Node();
		parent->m_left->m_parent=parent;
		parent->m_left->m_regOverlap.InitRegion(x_left, y_low, x_tmp, y_high, true);
		for(int i=0; i<nSize;++i)
			if(m_regionArray[parent->m_vecIn[i]].Center().m_x<x_tmp)
				parent->m_left->m_vecIn.push_back(parent->m_vecIn[i]);
		if(parent->m_left->m_vecIn.size()!=0 && parent->m_left->m_vecIn.size()!=parent->m_vecIn.size())
		{
			parent->m_right=new KD_Node();
			parent->m_right->m_parent=parent;
			parent->m_right->m_regOverlap.InitRegion(x_tmp, y_low, x_right, y_high, true);
			for(int i=0; i<nSize;++i)
				if (m_regionArray[parent->m_vecIn[i]].Center().m_x>x_tmp)
					parent->m_right->m_vecIn.push_back(parent->m_vecIn[i]);

			parent->m_nMedian=x_tmp;
			parent->IsSplitX=true;
			m_nNode+=2;

			return true;
		}
		else
		{
			delete parent->m_left;
			parent->m_left=NULL;
		}
	}
	return false;
}
bool CKDTree::SplitY(KD_Node *parent, float y_median)
{
	float x_left=parent->m_regOverlap.top_x();
	float x_right=parent->m_regOverlap.bottom_x();
	float y_low=parent->m_regOverlap.top_y();
	float y_high=parent->m_regOverlap.bottom_y();
	int nSize=parent->m_vecIn.size();
	bool isCollide=true;

	float y_delta=y_high-y_median;
	float y_delta_t=y_median-y_low;
	if(y_delta<y_delta_t)
		y_delta=y_delta_t;
	float y_tmp=0;
	//collision detection
	for(float i=0; i<=y_delta+1; ++i)
	{
		//add delta
		y_tmp=y_median+i;
		isCollide=false;
		for(float j=x_left; j<=x_right; ++j)
		{
			if (y_tmp>y_high)
				break;
			for(int k=0; k<nSize; ++k)
				if (m_pMap->getDrawbleObject(parent->m_vecIn[k])->isHitTestEnabled() && 
					m_regionArray[parent->m_vecIn[k]].IsIn(Point(j,y_tmp)))
				{
					isCollide=true;
					break;
				}
				if (isCollide)
					break;
		}
		if(!isCollide)
			break;
		//minus delta
		y_tmp=y_median-i;
		isCollide=false;
		for(float j=x_left; j<=x_right; ++j)
		{
			if (y_tmp<y_low)
				break;
			for(int k=0; k<nSize; ++k)
				if (m_pMap->getDrawbleObject(parent->m_vecIn[k])->isHitTestEnabled() && 
					m_regionArray[parent->m_vecIn[k]].IsIn(Point(j,y_tmp)))
				{
					isCollide=true;
					break;
				}
				if (isCollide)
					break;
		}
		if(!isCollide)
			break;
	}
	//actually split the node
	if (!isCollide)
	{
		parent->m_left=new KD_Node();
		parent->m_left->m_parent=parent;
		parent->m_left->m_regOverlap.InitRegion(x_left, y_low, x_right, y_tmp, true);
		for(int i=0; i<nSize;++i)
			if(m_regionArray[parent->m_vecIn[i]].Center().m_y<y_tmp)
				parent->m_left->m_vecIn.push_back(parent->m_vecIn[i]);
		if(parent->m_left->m_vecIn.size()!=0 && parent->m_left->m_vecIn.size()!=parent->m_vecIn.size())
		{
			parent->m_right=new KD_Node();
			parent->m_right->m_parent=parent;
			parent->m_right->m_regOverlap.InitRegion(x_left, y_tmp, x_right, y_high, true);
			for(int i=0; i<nSize;++i)
				if (m_regionArray[parent->m_vecIn[i]].Center().m_y>y_tmp)
					parent->m_right->m_vecIn.push_back(parent->m_vecIn[i]);

			parent->m_nMedian=y_tmp;
			parent->IsSplitX=false;
			m_nNode+=2;

			return true;
		}
		else
		{
			delete parent->m_left;
			parent->m_left=NULL;
		}
	}
	return false;
}

//destroy the tree
void CKDTree::DestroyTree(KD_Node *parent)
{
	if (parent==NULL)
		return;
	DestroyTree(parent->m_left);
	if(parent->m_left!=NULL)
	{
		delete parent->m_left;
		parent->m_left=NULL;
	}
	DestroyTree(parent->m_right);
	if(parent->m_right!=NULL)
	{
		delete parent->m_right;
		parent->m_right=NULL;
	}
	m_nNode=0;
	parent->m_parent=NULL;
}

//void CKDTree::PaintTree(KD_Node *parent, CDC *pDC)
//{
//	if (parent==NULL)
//		return;
//	PaintTree(parent->m_left, pDC);
//	PaintTree(parent->m_right, pDC);
//	pDC->Rectangle(parent->m_regOverlap.top_x(), parent->m_regOverlap.top_y(),
//		parent->m_regOverlap.bottom_x(), parent->m_regOverlap.bottom_y());
//}
//void CKDTree::PaintTree(CDC *pDC)
//{
//	PaintTree(m_root, pDC);
//}

//search the tree
void CKDTree::SearchTree(Point &p, float radius, ivector &vec_r)
{
	ResetTree(m_root);
	KD_Node *n=m_pRecentRoot;
	if (n==NULL)
	{
		n=m_root;
	}
	else
	{
		n=m_pRecentRoot;
		while(n!=m_root && !n->m_regOverlap.IsIn(p))
			n=n->m_parent;
	}
	while(n!=NULL && (n->m_left!=NULL || n->m_right!=NULL))
	{
		if (n->IsSplitX)
		{
			if(p.m_x<n->m_nMedian)
				n=n->m_left;
			else
				n=n->m_right;
		}
		else
		{
			if(p.m_y<n->m_nMedian)
				n=n->m_left;
			else
				n=n->m_right;
		}
	}
	m_pRecentRoot=n;
	SearchTree(n, p, radius, vec_r);
}

void CKDTree::SearchTree(KD_Node *parent, Point &p, float radius, ivector &vec_r)
{
	if (parent==NULL || parent->IsSearched)
		return;
	parent->IsSearched=true;
	//is a child node
	if(parent->m_left==NULL && parent->m_right==NULL)
	{
		int nSize=parent->m_vecIn.size();
		for (int i=0; i<nSize; ++i)
			if(m_regionArray[parent->m_vecIn[i]].IsSeen(p, radius))
				vec_r.push_back(parent->m_vecIn[i]);
	}
	//not a child node
	else
	{
		if(parent->m_left!=NULL && !parent->m_left->IsSearched && 
			(parent->m_left->m_regOverlap.IsSeen(p, radius)))
			SearchTree(parent->m_left, p, radius, vec_r);
		if (parent->m_right!=NULL && !parent->m_right->IsSearched &&
			(parent->m_right->m_regOverlap.IsSeen(p, radius)))
			SearchTree(parent->m_right, p, radius, vec_r);
	}
	//back trace : search parent
	SearchTree(parent->m_parent, p, radius, vec_r);
}

void CKDTree::ResetTree(KD_Node *parent)
{
	if(parent==NULL)
		return;
	parent->IsSearched=false;
	ResetTree(parent->m_left);
	ResetTree(parent->m_right);
}
//void CKDTree::PaintResult(CDC *pDC, std::vector<int> &vec_r)
//{
//	for(unsigned i=0; i<vec_r.size(); ++i)
//	{
//		pDC->Rectangle(m_regionArray[i].top_x(), m_regionArray[i].top_y(), m_regionArray[i].bottom_x(), m_regionArray[i].bottom_y());
//	}
//	int nSize=vec_r.size();
//	CPen newPen(PS_SOLID, 3, RGB(0, 0, 255));
//	CPen *oldPen=(CPen*)pDC->SelectObject(&newPen);
//	for (int i=0; i<nSize; ++i)
//	{
//		pDC->Rectangle(m_regionArray[vec_r[i]].top_x(), m_regionArray[vec_r[i]].top_y(), 
//			m_regionArray[vec_r[i]].bottom_x(), m_regionArray[vec_r[i]].bottom_y());
//	}
//	pDC->SelectObject(oldPen);
//}
