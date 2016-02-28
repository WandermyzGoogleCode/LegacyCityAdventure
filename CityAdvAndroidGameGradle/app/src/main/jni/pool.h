#ifndef POOL_H_
#define POOL_H_

template<typename T>
class pool
{
	T *_stor;
	int _pointer;
	int _size;
	bool _is_full;

public:
	pool();
	pool(int size);
	~pool() { delete [] _stor;}

	void push_back(T val);
	T& operator[](int index) { return _stor[(_pointer+index)%_size]; }
	bool is_full() { return _is_full; }
	int size() { return _size; }
};

template<typename T>
pool<T>::pool():_size(57),_pointer(0),_is_full(false)
{
  _stor=new T[_size];
}

template<typename T>
pool<T>::pool(int size):_size(size),_pointer(0),_is_full(false)
{
  _stor=new T[_size];
}

template<typename T>
void pool<T>::push_back(T val)
{
	_stor[_pointer++]=val;
	if(_pointer==_size)
	{
		_is_full=true;
		_pointer=0;
	}
}

#endif
