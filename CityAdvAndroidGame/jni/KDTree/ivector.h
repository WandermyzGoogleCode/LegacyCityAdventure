#ifndef IVECTOR_H_
#define IVECTOR_H_

class ivector
{
private:
	int *pArray;
	int length;
	int _size;
public:
	ivector():length(100),_size(0) { pArray=new int[100]; }
	~ivector() { delete [] pArray; }
	
	void push_back(int ival);
	int size() { return _size; }
	int& operator[](int index) { return pArray[index]; }
};
#endif