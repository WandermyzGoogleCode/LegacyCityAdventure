#include "ivector.h"

void ivector::push_back(int ival)
{
	if(_size>=length)
	{
		int *pTemp=new int[2*length];
		for(int i=0; i<length; ++i)
			pTemp[i]=pArray[i];
		delete [] pArray;
		pArray=pTemp;
		length=2*length;
	}
	pArray[_size++]=ival;
}