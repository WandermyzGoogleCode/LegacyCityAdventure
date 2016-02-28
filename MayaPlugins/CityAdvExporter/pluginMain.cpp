//
// Copyright (C)  
// 
// File: pluginMain.cpp
//
// Author: Maya Plug-in Wizard 2.0
//

#include <maya/MFnPlugin.h>
#include "CityAdvExporter.h"

#define VENDOR "City Adventure"

MStatus initializePlugin( MObject obj )
{ 
	MFnPlugin plugin( obj, VENDOR, "0.1", "Any");

	// Register the translator with the system
	return plugin.registerFileTranslator( "CityAdv Exporter", "none",
		CityAdvExporter::creator);  
}

MStatus uninitializePlugin( MObject obj )
//
//	Description:
//		this method is called when the plug-in is unloaded from Maya. It 
//		deregisters all of the services that it was providing.
//
//	Arguments:
//		obj - a handle to the plug-in object (use MFnPlugin to access it)
//
{
	MFnPlugin plugin( obj );
	return plugin.deregisterFileTranslator( "CityAdv Exporter" );
}
