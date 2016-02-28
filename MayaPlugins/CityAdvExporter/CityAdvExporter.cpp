#include "CityAdvExporter.h"
#include <maya\MGlobal.h>
#include <maya\MItDag.h>
#include <maya\MDagPath.h>
#include <maya\MFnDagNode.h>
#include <maya\MItMeshPolygon.h>
#include <maya\MItMeshVertex.h>
#include <maya\MDistance.h>
#include <maya\MFnSet.h>
#include <maya\MSelectionList.h>
#include <maya\MItSelectionList.h>
#include <maya\MItMeshEdge.h>
#include <maya\MCommandResult.h>
#include <maya\MString.h>
#include <maya\MDoubleArray.h>
#include "CaMapFileWriter.h"
#include "MaterialParser.h"

#include <algorithm>

using namespace std;

void* CityAdvExporter::creator()
{
	return new CityAdvExporter();
}

bool CityAdvExporter::haveReadMethod() const
{
	return false;
}

bool CityAdvExporter::haveWriteMethod() const
{
	return true;
}

MString CityAdvExporter::defaultExtension() const
{
	return FILE_EXT_MAP;
}

CityAdvExporter::MFileKind CityAdvExporter::identifyFile ( const MFileObject& fileName, const char* buffer, short size) const
{
	MString name = fileName.name();
	MString ext = name.substring(name.length() - strlen(FILE_EXT_MAP) - 1, name.length());

	if (ext == MString(".") + FILE_EXT_MAP)
		return kCouldBeMyFileType;
	else
		return kNotMyFileType;
}

MStatus CityAdvExporter::writer( const MFileObject& file, const MString& optionsString, FileAccessMode mode )
{
	if( (mode != MPxFileTranslator::kExportAccessMode ) && (mode != MPxFileTranslator::kSaveAccessMode) )
	{
		//Export selected not supported
		MGlobal::displayError("Please use \"Export All\"");
		return MS::kFailure;
	}
	
	//TODO: Unit check
	//TODO: triangulate

	//extract file name
	fileName = file.fullName().substring(0, file.fullName().length() - strlen(FILE_EXT_MAP) - 2);

	MString objFileName = fileName + "." + FILE_EXT_OBJ;
	const char *fname = objFileName.asChar();
	fp = fopen(fname,"w");

	if (fp == NULL)
	{
		MGlobal::displayError("Error: The file " + MString(fname) + " could not be opened for writing.");
		return MS::kFailure;
	}

	//added by Wander [[[
	//prepare material lib
	materialParser.parse(fileName);
	//]]]

	//From obj exporter
	// Options
	//
	groups      = true; // write out facet groups
	ptgroups    = true; // write out vertex groups
	materials   = true; // write out shading groups
	smoothing   = true; // write out facet smoothing information
	normals     = true; // write out normal table and facet normals

	exportAll();

	fclose(fp);

	//by Wander
	CaMapFileWriter caWriter;
	MStatus caWriterResult = caWriter.write(cityAdvMap, fileName);

	return caWriterResult;
}

MStatus CityAdvExporter::exportAll( )
{
	//reset
	vertexArray.clear();
	normalArray.clear();
	textureArray.clear();
	caFaceMap.clear();
	//caNormalMap.clear();
	caObjectSet.clear();
	caBuildingArray.clear();
	caEventPointArray.clear();
	lastCaObj = "";

	MStatus status = MS::kSuccess;

	initializeSetsAndLookupTables( true );

	//add by Wander output buildings from transformNodeArray [[[
	exportCaTypeElements();
	//]]]


	MItDag dagIterator( MItDag::kBreadthFirst, MFn::kInvalid, &status);

	if ( MS::kSuccess != status) {
		fprintf(stderr,"Failure in DAG iterator setup.\n");
		return MS::kFailure;
	}
	// reset counters
	v = vt = vn = 0;
	voff = vtoff = vnoff = 0;

	for ( ; !dagIterator.isDone(); dagIterator.next() )
	{
		MDagPath dagPath;
		MObject  component = MObject::kNullObj;
		status = dagIterator.getPath(dagPath);

		if (!status) {
			fprintf(stderr,"Failure getting DAG path.\n");
			freeLookupTables();
			return MS::kFailure;
		}

		// skip over intermediate objects
		//
		MFnDagNode dagNode( dagPath, &status );
		if (dagNode.isIntermediateObject()) 
		{
			continue;
		}

		if ((  dagPath.hasFn(MFn::kNurbsSurface)) &&
			(  dagPath.hasFn(MFn::kTransform)))
		{
			status = MS::kSuccess;
			fprintf(stderr,"Warning: skipping Nurbs Surface.\n");
		}
		else if ((  dagPath.hasFn(MFn::kMesh)) &&
			(  dagPath.hasFn(MFn::kTransform)))
		{
			// We want only the shape, 
			// not the transform-extended-to-shape.
			continue;
		}
		else if (  dagPath.hasFn(MFn::kMesh))
		{
			// Build a lookup table so we can determine which 
			// polygons belong to a particular edge as well as
			// smoothing information
			//
			buildEdgeTable( dagPath );

			// Now output the polygon information
			//
			status = OutputPolygons(dagPath, component);
			objectId++;
			if (status != MS::kSuccess) {
				fprintf(stderr,"Error: exporting geom failed.\n");
				freeLookupTables();                
				destroyEdgeTable(); // Free up the edge table				
				return MS::kFailure;
			}
			destroyEdgeTable(); // Free up the edge table
		}
		voff = v;
		vtoff = vt;
		vnoff = vn;
	}

	freeLookupTables();

	//added by Wander [[[
	//create elements in cityAdvMap
	cityAdvMap.createBuildings(caBuildingArray.size());
	for(int i = 0; i < caBuildingArray.size(); i++)
	{
		cityAdvMap.getBuildings()[i] = caBuildingArray[i];	//ensured that model has not been loaded
		cityAdvMap.expandBoundingBox(caBuildingArray[i].getDesc().minVertex, caBuildingArray[i].getDesc().maxVertex);	//expand bounding box
	}

	MGlobal::displayInfo("Event Points: ");
	exportCaEvents();
	cityAdvMap.createEventPoints(caEventPointArray.size());
	for(int i = 0; i < caEventPointArray.size(); i++)
	{
		cityAdvMap.getEventPoints()[i] = caEventPointArray[i];
		BoundingBox eventBoundingBox;
		caEventPointArray[i].getBoundingBox(eventBoundingBox);
		cityAdvMap.expandBoundingBox(eventBoundingBox.minVertex, eventBoundingBox.maxVertex);	//expand bounding box

		EventDesc desc = caEventPointArray[i].getDesc();
		MGlobal::displayInfo(MString("\t") + MString(desc.name) + ": (" + desc.playerPosition.x + ", " + desc.playerPosition.y + ", " + desc.playerPosition.z + ") " + desc.radius);
	}

	MGlobal::displayInfo("Materials: ");
	cityAdvMap.createMaterials(materialParser.getMaterials().size());
	for(int i = 0; i < materialParser.getMaterials().size(); i++)
	{
		cityAdvMap.getMaterials()[i] = materialParser.getMaterials()[i];
		MGlobal::displayInfo(MString("Material ") + i);
		MGlobal::displayInfo(MString("\tDiffuse: ") + materialParser.getMaterials()[i].diffuse.r + ", " + materialParser.getMaterials()[i].diffuse.g + ", " + materialParser.getMaterials()[i].diffuse.b + ", " + materialParser.getMaterials()[i].diffuse.a);
		MGlobal::displayInfo(MString("\tAmbient: ") + materialParser.getMaterials()[i].ambient.r + ", " + materialParser.getMaterials()[i].ambient.g + ", " + materialParser.getMaterials()[i].ambient.b + ", " + materialParser.getMaterials()[i].ambient.a);
		MGlobal::displayInfo(MString("\tSpecular: ") + materialParser.getMaterials()[i].specular.r + ", " + materialParser.getMaterials()[i].specular.g + ", " + materialParser.getMaterials()[i].specular.b + ", " + materialParser.getMaterials()[i].specular.a);
		MGlobal::displayInfo(MString("\tShininess: ") + materialParser.getMaterials()[i].shininess);
	}

	Point3D minV = cityAdvMap.getBoundingBoxMinVertex();
	Point3D maxV = cityAdvMap.getBoundingBoxMaxVertex();
	MGlobal::displayInfo(MString("Map Bounding Box: (") + minV.x + ", " + minV.y + ", " + minV.z + "), (" + maxV.x + ", " + maxV.y + ", " + maxV.z);

	exportCaModels();
	//]]]

	return status;
}

void CityAdvExporter::exportCaTypeElements()
{
	for(int i = 0; i < transformNodeNameArray.length(); i++) 
	{
		MCommandResult result;
		MGlobal::executeCommand(MString("getAttr ") + transformNodeNameArray[i] + ".caType", result);
		MCommandResult::Type type = result.resultType(); 
		if(type == MCommandResult::Type::kInvalid)
		{
			continue;
		}
		else if(type == MCommandResult::Type::kInt)
		{
			int caType;
			result.getResult(caType);
			
			switch(caType)
			{
			case CaType::CaBuilding:
				exportCaBuilding(transformNodeNameArray[i]);
				caObjectSet.insert(std::string(transformNodeNameArray[i].asChar()));
				break;

			case CaType::CaEvent:
				//exportCaEvent(transformNodeNameArray[i]);
				//no need to set caObjectSet because it has no model
				//enumerate by mel in exportCaEvents because it is not mesh
				break;

			default:
				continue;
			}
		}
	}	
}

void CityAdvExporter::exportCaModels()
{
	//MGlobal::displayInfo(MString("Size Test: ") + sizeof(TriangleIndexShort));

	/*
	for(std::map<std::string, std::vector<TriangleIndex> >::iterator it = caFaceMap.begin(); it != caFaceMap.end(); ++it)
	{
		MGlobal::displayInfo(MString("Ca Object: ") + it->first.c_str());
		for(std::vector<TriangleIndex>::iterator indIt = it->second.begin(); indIt != it->second.end(); ++indIt)
		{
			MGlobal::displayInfo(MString("f ") + indIt->v1 + ", " + indIt->v2 + ", " + indIt->v3);
		}
	}*/
	
	//export building models
	for(int bid = 0; bid < cityAdvMap.getBuildingNum(); bid++)
	{
		std::vector<Point3D> modelVertexArray;
		std::vector<Point3D> modelNormalArray;
		std::map<int, int> global2localMap;
		
		std::vector<FaceIndexWithMaterial>& faces = caFaceMap[std::string(cityAdvMap.getBuildings()[bid].getDesc().name)];
		//std::vector<TriangleIndex>& normals = caNormalMap[std::string(cityAdvMap.getBuildings()[bid].getDesc().name)];
		for(std::vector<FaceIndexWithMaterial>::iterator it = faces.begin(); it != faces.end(); ++it)
		{
			//check vertex
			int v[3];
			v[0] = it->getIndex().v1;
			v[1] = it->getIndex().v2;
			v[2] = it->getIndex().v3;

			for(int i = 0; i < 3; i++)
			{
				if(global2localMap.find(v[i]) == global2localMap.end())
				{
					//insert a new vertex
					modelVertexArray.push_back(vertexArray[v[i]]);
					modelNormalArray.push_back(normalArray[v[i]]);
					global2localMap[v[i]] = modelVertexArray.size() - 1;
				}
			}
		}

		//sort faces index to aggregate same materials
		std::sort(faces.begin(), faces.end());

		//build material groups
		std::vector<MaterialGroupDesc> faceMatGroups;
		int lastMatId = -1;
		//std::vector<int>& facesMat = caFaceMaterialMap[std::string(cityAdvMap.getBuildings()[bid].getDesc().name)];
		
		for(int i = 0; i < faces.size() /*facesMat.size()*/; i++)
		{
			int matId = faces[i].getMaterialId(); //facesMat[i];
			if(matId != lastMatId)
			{
				//add a new group
				MaterialGroupDesc matGroup;
				matGroup.beginIndex = i;
				matGroup.materialId = matId;
				faceMatGroups.push_back(matGroup);
				lastMatId = matId;
			}
		}

		//create model
		cityAdvMap.getBuildings()[bid].getModel().create(modelVertexArray.size(), faces.size(), faceMatGroups.size());
		
		//copy vertex and vertexNormal
		for(int i = 0; i < modelVertexArray.size(); i++)
		{
			cityAdvMap.getBuildings()[bid].getModel().getVertexArray()[i] = modelVertexArray[i];
			cityAdvMap.getBuildings()[bid].getModel().getNormalArray()[i] = modelNormalArray[i];
		}

		//copy vertex index and normals
		for(int i = 0; i < faces.size(); i++)
		{
			//TODO: more than 65536 vertexes
			cityAdvMap.getBuildings()[bid].getModel().getVertexIndex()[i] = TriangleIndexShort(
					(USHORT)global2localMap[faces[i].getIndex().v1],
					(USHORT)global2localMap[faces[i].getIndex().v2],
					(USHORT)global2localMap[faces[i].getIndex().v3]
				);

			//Point3D normal = normalArray[normals[i].v1];
			//cityAdvMap.getBuildings()[bid].getModel().getNormalArray()[i] = normal;	//TODO: v2, v3 ignored
		}	

		//copy material groups
		for(int i = 0; i < faceMatGroups.size(); i++)
		{
			cityAdvMap.getBuildings()[bid].getModel().getMaterialGroups()[i] = faceMatGroups[i];
		}
		
		MGlobal::displayInfo(MString("Model for ") + cityAdvMap.getBuildings()[bid].getDesc().name);
		MGlobal::displayInfo(MString("Vertex ") + cityAdvMap.getBuildings()[bid].getModel().getVertexNum());
		MGlobal::displayInfo(MString("Face ") + cityAdvMap.getBuildings()[bid].getModel().getFaceNum());
		/*MGlobal::displayInfo("Vertex: ");
		for(int i = 0; i < cityAdvMap.getBuildings()[bid].getModel().getVertexNum(); i++)
		{
			MGlobal::displayInfo(MString("v") + i + ": " + 
				cityAdvMap.getBuildings()[bid].getModel().getVertexArray()[i].x + ", " + 
				cityAdvMap.getBuildings()[bid].getModel().getVertexArray()[i].y + ", " + 
				cityAdvMap.getBuildings()[bid].getModel().getVertexArray()[i].z);
		}
		MGlobal::displayInfo("Index: ");
		for(int i = 0; i < cityAdvMap.getBuildings()[bid].getModel().getFaceNum(); i++)
		{
			MGlobal::displayInfo(MString("f") + i + ": " + 
				cityAdvMap.getBuildings()[bid].getModel().getVertexIndex()[i].v1 + ", " + 
				cityAdvMap.getBuildings()[bid].getModel().getVertexIndex()[i].v2 + ", " + 
				cityAdvMap.getBuildings()[bid].getModel().getVertexIndex()[i].v3);
		}
		MGlobal::displayInfo("Normals: ");
		for(int i = 0; i < cityAdvMap.getBuildings()[bid].getModel().getVertexNum(); i++)
		{
			MGlobal::displayInfo(MString("n") + i + ": " + 
				cityAdvMap.getBuildings()[bid].getModel().getNormalArray()[i].x + ", " + 
				cityAdvMap.getBuildings()[bid].getModel().getNormalArray()[i].y + ", " + 
				cityAdvMap.getBuildings()[bid].getModel().getNormalArray()[i].z);
		}*/
		MGlobal::displayInfo("Material Groups: ");
		for(int i = 0; i < faceMatGroups.size(); i++)
		{
			MGlobal::displayInfo(MString("m ") + faceMatGroups[i].materialId + " begin at face " + faceMatGroups[i].beginIndex);
		}
		
	}
}

void CityAdvExporter::exportCaBuilding(const MString& name)
{
	double x1,y1,z1,x2,y2,z2;
	MGlobal::executeCommand(MString("getAttr ") + name + ".boundingBoxMinX", x1);
	MGlobal::executeCommand(MString("getAttr ") + name + ".boundingBoxMinY", y1);
	MGlobal::executeCommand(MString("getAttr ") + name + ".boundingBoxMinZ", z1);
	MGlobal::executeCommand(MString("getAttr ") + name + ".boundingBoxMaxX", x2);
	MGlobal::executeCommand(MString("getAttr ") + name + ".boundingBoxMaxY", y2);
	MGlobal::executeCommand(MString("getAttr ") + name + ".boundingBoxMaxZ", z2);

	int hitTestEnabled = 0;
	MCommandResult result;
	MGlobal::executeCommand(MString("getAttr ") + name + ".caHitTestEnabled", result);
	MCommandResult::Type type = result.resultType(); 
	if(type == MCommandResult::Type::kInt)
	{
		result.getResult(hitTestEnabled);
	}

	MGlobal::displayInfo(MString("CaBuilding: ") + name + " (" + x1 + ", " + y1 + ", " + z1 + "), (" + x2 + ", " + y2 + ", " + z2 + ")");

	Building b;
	strncpy(b.getDesc().name, name.asChar(), OBJECT_NAME_MAX_LENGTH);
	b.getDesc().minVertex.x = x1;
	b.getDesc().minVertex.y = y1;
	b.getDesc().minVertex.z = z1;
	b.getDesc().maxVertex.x = x2;
	b.getDesc().maxVertex.y = y2;
	b.getDesc().maxVertex.z = z2;
	b.getDesc().hitTestEnabled = hitTestEnabled;

	caBuildingArray.push_back(b);
}

void CityAdvExporter::exportCaEvents()
{
	MStringArray eventLocators;
	MGlobal::executeCommand(MString("ls -et locator"), eventLocators);

	for(int i = 0; i < eventLocators.length(); i++)
	{
		MString childName = eventLocators[i];
		MStringArray nameArray;
		MGlobal::executeCommand(MString("listRelatives -p " + childName), nameArray);
		
		if(nameArray.length() != 1)
		{
			continue;
		}
		MString name = nameArray[0];

		//check type
		MString typeCheck;
		MGlobal::executeCommand(MString("nodeType " + name), typeCheck);
		if(typeCheck != "transform")
		{
			continue;			
		}

		MCommandResult caTypeResult;
		MGlobal::executeCommand(MString("getAttr ") + name + ".caType", caTypeResult);
		if(caTypeResult.resultType() == MCommandResult::Type::kInvalid)
		{
			continue;
		}
		else if(caTypeResult.resultType() == MCommandResult::Type::kInt )
		{
			int caType;
			caTypeResult.getResult(caType);
			if(caType != CaEvent)
			{
				continue;
			}
		}

		MDoubleArray position;
		MGlobal::executeCommand(MString("getAttr ") + name + ".translate", position);

		MDoubleArray scale;
		MGlobal::executeCommand(MString("getAttr ") + name + ".scale", scale);

		double maxScale = 0;
		for(int i = 0; i < 3; i++)
		{
			if(abs(scale[i]) > maxScale)
			{
				maxScale = abs(scale[i]);
			}
		}

		EventPoint p;
		strncpy(p.getDesc().name, name.asChar(), OBJECT_NAME_MAX_LENGTH);
		p.getDesc().playerPosition.x = position[0];
		p.getDesc().playerPosition.y = position[1];
		p.getDesc().playerPosition.z = position[2];
		p.getDesc().radius = maxScale;
		p.getDesc().eventId = caEventPointArray.size();

		//MGlobal::displayInfo(MString("test") + p.getDesc().radius);

		caEventPointArray.push_back(p);
	}
}

MStatus CityAdvExporter::OutputPolygons(MDagPath& mdagPath, MObject&  mComponent)
{
	MStatus stat = MS::kSuccess;
	MSpace::Space space = MSpace::kWorld;
	int i;

	MFnMesh fnMesh( mdagPath, &stat );
	if ( MS::kSuccess != stat) {
		fprintf(stderr,"Failure in MFnMesh initialization.\n");
		return MS::kFailure;
	}

	MItMeshPolygon polyIter( mdagPath, mComponent, &stat );
	if ( MS::kSuccess != stat) {
		fprintf(stderr,"Failure in MItMeshPolygon initialization.\n");
		return MS::kFailure;
	}
	MItMeshVertex vtxIter( mdagPath, mComponent, &stat );
	if ( MS::kSuccess != stat) {
		fprintf(stderr,"Failure in MItMeshVertex initialization.\n");
		return MS::kFailure;
	}

	int objectIdx = -1, length;
	MString mdagPathNodeName = fnMesh.name();
	// Find i such that objectGroupsTablePtr[i] corresponds to the
	// object node pointed to by mdagPath
	length = objectNodeNamesArray.length();
	for( i=0; i<length; i++ ) {
		if( objectNodeNamesArray[i] == mdagPathNodeName ) {
			objectIdx = i;
			break;
		}
	}

	// Write out the vertex table
	//

	for ( ; !vtxIter.isDone(); vtxIter.next() ) {
		MPoint p = vtxIter.position( space );
		if (ptgroups && groups && (objectIdx >= 0)) {
			int compIdx = vtxIter.index();
			outputSetsAndGroups( mdagPath, compIdx, true, objectIdx );
		}
		// convert from internal units to the current ui units
		p.x = MDistance::internalToUI(p.x);
		p.y = MDistance::internalToUI(p.y);
		p.z = MDistance::internalToUI(p.z);
		fprintf(fp,"v %f %f %f\n",p.x,p.y,p.z);
		v++;
		
		//add by Wander [[[
		vertexArray.push_back(Point3D(p.x, p.y, p.z));
		MVector vNorm;
		fnMesh.getVertexNormal(vtxIter.index(), true, vNorm, MSpace::kWorld);
		normalArray.push_back(Point3D(vNorm.x, vNorm.y, vNorm.z));
		//]]]
	}

	// Write out the uv table
	//
	MFloatArray uArray, vArray;
	fnMesh.getUVs( uArray, vArray );
	int uvLength = uArray.length();
	for ( int x=0; x<uvLength; x++ ) {
		fprintf(fp,"vt %f %f\n",uArray[x],vArray[x]);
		vt++;

		//add by Wander [[[
		textureArray.push_back(Point2D(uArray[x], vArray[x]));
		//]]]
	}

	// Write out the normal table
	//
	if ( normals ) {
		MFloatVectorArray norms;
		fnMesh.getNormals( norms, MSpace::kWorld );
		int normsLength = norms.length();
		for ( int t=0; t<normsLength; t++ ) {
			MFloatVector tmpf = norms[t];
			fprintf(fp,"vn %f %f %f\n",tmpf[0],tmpf[1],tmpf[2]);
			vn++;

			/*//add by Wander [[[
			normalArray.push_back(Point3D(tmpf[0], tmpf[1], tmpf[2]));
			//]]]*/
		}
	}

	// For each polygon, write out: 
	//    s  smoothing_group
	//    sets/groups the polygon belongs to 
	//    f  vertex_index/uvIndex/normalIndex
	//
	int lastSmoothingGroup = INITIALIZE_SMOOTHING;

	for ( ; !polyIter.isDone(); polyIter.next() )
	{
		// Write out the smoothing group that this polygon belongs to
		// We only write out the smoothing group if it is different
		// from the last polygon.
		//
		if ( smoothing ) {
			int compIdx = polyIter.index();
			int smoothingGroup = polySmoothingGroups[ compIdx ];

			if ( lastSmoothingGroup != smoothingGroup ) {
				if ( NO_SMOOTHING_GROUP == smoothingGroup ) {
					fprintf(fp,"s off\n");
				}
				else {
					fprintf(fp,"s %d\n", smoothingGroup );
				}
				lastSmoothingGroup = smoothingGroup;
			}
		}

		// Write out all the sets that this polygon belongs to
		//
		if ((groups || materials) && (objectIdx >= 0)) {
			int compIdx = polyIter.index();
			outputSetsAndGroups( mdagPath, compIdx, false, objectIdx );
		}

		// Write out vertex/uv/normal index information
		//
		
		//added by Wander[[[
		TriangleIndex faceIndex;
		TriangleIndex normIndex;
		bool nonTriangular = false;
		//]]]

		fprintf(fp,"f");
		int polyVertexCount = polyIter.polygonVertexCount();
		for ( int vtx=0; vtx<polyVertexCount; vtx++ ) {
			fprintf(fp," %d", polyIter.vertexIndex( vtx ) +1 +voff);

			bool noUV = true;
			if ( fnMesh.numUVs() > 0 ) {
				int uvIndex;
				// If the call to getUVIndex fails then there is no
				// mapping information for this polyon so we don't write
				// anything
				//
				if ( polyIter.getUVIndex(vtx,uvIndex) ) {
					fprintf(fp,"/%d",uvIndex+1 +vtoff);
					noUV = false;
				}
			}

			if ( (normals) && (fnMesh.numNormals() > 0) ) {
				if ( noUV ) {
					// If there are no UVs then our polygon is written
					// in the form vertex//normal
					//
					fprintf(fp,"/");
				}
				fprintf(fp,"/%d",polyIter.normalIndex( vtx ) +1 +vnoff);
			}

			//added by Wander[[[
			int vertexIndexElem = polyIter.vertexIndex( vtx ) + voff;	//do not +1 because vertexArray is 0-based.
			int normIndexElem = polyIter.normalIndex( vtx ) + vnoff; 
			switch(vtx)
			{
			case 0:
				faceIndex.v1 = vertexIndexElem;
				normIndex.v1 = normIndexElem;
				break;

			case 1:
				faceIndex.v2 = vertexIndexElem;
				normIndex.v2 = normIndexElem;
				break;

			case 2:
				faceIndex.v3 = vertexIndexElem;
				normIndex.v3 = normIndexElem;
				break;

			default:
				nonTriangular = true;
				break;
			}
			//]]]

		}
		fprintf(fp,"\n");

		//added by Wander [[[
		if(lastCaObj.length() > 0)
		{
			if(nonTriangular)
			{
				MGlobal::displayError(MString("Non-triangular face detected: ") + lastCaObj);
			}

			//materials
			int matId = materialParser.getMaterialIdByName(lastMaterialName);
			if(matId == -1)
			{
				MGlobal::displayError(MString("Cannot find material: " + lastMaterialName));
				matId = 0;	//use the first material instead
			}

			caFaceMap[std::string(lastCaObj.asChar())].push_back(FaceIndexWithMaterial(faceIndex, matId));

			//caFaceMap[std::string(lastCaObj.asChar())].push_back(faceIndex);


			//caFaceMaterialMap[std::string(lastCaObj.asChar())].push_back(matId);
		}
		//]]]


		fflush(fp);
	}
	return stat;
}

void CityAdvExporter::outputSetsAndGroups(MDagPath & mdagPath, int cid, bool isVertexIterator, int objectIdx)
{
	MStatus stat;

	int i, length;
	MIntArray * currentSets = new MIntArray;
	MIntArray * currentMaterials = new MIntArray;
	MStringArray gArray, mArray;


	if (groups || materials) {

		for ( i=0; i<numSets; i++ )
		{
			if ( lookup(mdagPath,i,cid,isVertexIterator) ) {

				MFnSet fnSet( (*sets)[i] );
				if ( MFnSet::kRenderableOnly == fnSet.restriction(&stat) ) {
					currentMaterials->append( i );
					mArray.append( fnSet.name() );
				}
				else {
					currentSets->append( i );
					gArray.append( fnSet.name() );
				}
			}
		}

		//add by Wander[[[
		MString lastCaObjTest = "";	//maybe repeated
		//]]]

		if( !isVertexIterator ) {
			// export group nodes (transform DAG nodes) in Maya that
			// the current object is a
			// child/grandchild/grandgrandchild/... of
			bool *objectGroupTable = objectGroupsTablePtr[objectIdx];
			length = transformNodeNameArray.length();
			for( i=0; i<length; i++ ) {
				if( objectGroupTable[i] ) {
					currentSets->append( numSets + i );
					gArray.append(transformNodeNameArray[i]);

					//add by Wander[[[
					if(caObjectSet.find(std::string(transformNodeNameArray[i].asChar())) != caObjectSet.end())
					{
						//ca obj
						lastCaObjTest = transformNodeNameArray[i];
						//TODO: tranform belongs to multiple ca obj?
					}
					//]]]
				}
			}
		}

		// prevent grouping incoherence, use tav default group schema.
		//
		if (0 == currentSets->length())
		{
			currentSets->append( 0 );
			gArray.append( "default" );
		}


		// Test for equivalent sets
		//
		bool setsEqual = false;
		if ( (lastSets != NULL) && 
			(lastSets->length() == currentSets->length())
			) {
				setsEqual = true;
				length = lastSets->length();
				for ( i=0; i<length; i++ )
				{
					if ( (*lastSets)[i] != (*currentSets)[i] ) {
						setsEqual = false;
						break;
					}
				}	
		}

		if ( !setsEqual ) {
			if ( lastSets != NULL )
				delete lastSets;

			lastSets = currentSets;		
			//added by Wander [[[
			lastCaObj = lastCaObjTest;
			//]]]

			if (groups) {
				int gLength = gArray.length();
				if ( gLength > 0  ) {
					fprintf(fp,"g");
					for ( i=0; i<gLength; i++ ) {
						fprintf(fp," %s",gArray[i].asChar());
					}
					fprintf(fp,"\n");  
				}
			}
		}
		else
		{
			delete currentSets;
		}




		// Test for equivalent materials
		//
		bool materialsEqual = false;
		if ( (lastMaterials != NULL) && 
			(lastMaterials->length() == currentMaterials->length())
			) {
				materialsEqual = true;
				length = lastMaterials->length();
				for ( i=0; i<length; i++ )
				{
					if ( (*lastMaterials)[i] != (*currentMaterials)[i] ) {
						materialsEqual = false;
						break;
					}
				}			
		}

		if ( !materialsEqual ) {
			if ( lastMaterials != NULL )
				delete lastMaterials;

			lastMaterials = currentMaterials;

			if (materials) {


				int mLength = mArray.length();

				if ( mLength > 0  ) {
					fprintf(fp,"usemtl");
					for ( i=0; i<mLength; i++ ) {
						fprintf(fp," %s",mArray[i].asChar());
					}
					fprintf(fp,"\n");

					//added by Wander [[[
					lastMaterialName = mArray[0];	//we only support single material on a face	

					string matNameStr(lastMaterialName.asChar());
					if(materialToProcess.find(matNameStr) == materialToProcess.end())
					{
						materialToProcess.insert(string(lastMaterialName.asChar()));
						MGlobal::displayInfo("Mat Name: " + lastMaterialName);
					}
					//]]]
				}
			}
		}
		else
		{
			delete currentMaterials;
		}
	}	
}


//////////////////////////////////////////////////////////////////////////
//from obj exporter
//////////////////////////////////////////////////////////////////////////

//from obj exporter, without modification
void CityAdvExporter::initializeSetsAndLookupTables( bool exportAll )
//
// Description :
//    Creates a list of all sets in Maya, a list of mesh objects,
//    and polygon/vertex lookup tables that will be used to
//    determine which sets are referenced by the poly components.
//
{
	int i=0,j=0, length;
	MStatus stat;

	// Initialize class data.
	// Note: we cannot do this in the constructor as it
	// only gets called upon registry of the plug-in.
	//
	numSets = 0;
	sets = NULL;
	lastSets = NULL;
	lastMaterials = NULL;
	objectId = 0;
	objectCount = 0;
	polygonTable = NULL;
	vertexTable = NULL;
	polygonTablePtr = NULL;
	vertexTablePtr = NULL;
	objectGroupsTablePtr = NULL;
	objectNodeNamesArray.clear();
	transformNodeNameArray.clear();

	//////////////////////////////////////////////////////////////////
	//
	// Find all sets in Maya and store the ones we care about in
	// the 'sets' array. Also make note of the number of sets.
	//
	//////////////////////////////////////////////////////////////////

	// Get all of the sets in maya and put them into
	// a selection list
	// 
	MStringArray result;
	MGlobal::executeCommand( "ls -sets", result );
	MSelectionList * setList = new MSelectionList;
	length = result.length();
	for ( i=0; i<length; i++ )
	{	
		setList->add( result[i] );
	}

	// Extract each set as an MObject and add them to the
	// sets array.
	// We may be excluding groups, matierials, or ptGroups
	// in which case we can ignore those sets. 
	//
	MObject mset;
	sets = new MObjectArray();
	length = setList->length();
	for ( i=0; i<length; i++ )
	{
		setList->getDependNode( i, mset );

		MFnSet fnSet( mset, &stat );
		if ( stat ) {
			if ( MFnSet::kRenderableOnly == fnSet.restriction(&stat) ) {
				if ( materials ) {
					sets->append( mset );
				}
			} 
			else {
				if ( groups ) {
					sets->append( mset );
				}
			}
		}	
	}
	delete setList;

	numSets = sets->length();

	//////////////////////////////////////////////////////////////////
	//
	// Do a dag-iteration and for every mesh found, create facet and
	// vertex look-up tables. These tables will keep track of which
	// sets each component belongs to.
	//
	// If exportAll is false then iterate over the activeSelection 
	// list instead of the entire DAG.
	//
	// These arrays have a corrisponding entry in the name
	// stringArray.
	//
	//////////////////////////////////////////////////////////////////
	MIntArray vertexCounts;
	MIntArray polygonCounts;	

	if ( exportAll ) {
		MItDag dagIterator( MItDag::kBreadthFirst, MFn::kInvalid, &stat);

		if ( MS::kSuccess != stat) {
			fprintf(stderr,"Failure in DAG iterator setup.\n");
			return;
		}

		objectNames = new MStringArray;

		for ( ; !dagIterator.isDone(); dagIterator.next() ) 
		{
			MDagPath dagPath;
			stat = dagIterator.getPath( dagPath );

			if ( stat ) 
			{
				// skip over intermediate objects
				//
				MFnDagNode dagNode( dagPath, &stat );
				if (dagNode.isIntermediateObject()) 
				{
					continue;
				}

				if (( dagPath.hasFn(MFn::kMesh)) &&
					( dagPath.hasFn(MFn::kTransform)))
				{
					// We want only the shape, 
					// not the transform-extended-to-shape.
					continue;
				}
				else if ( dagPath.hasFn(MFn::kMesh))
				{
					// We have a mesh so create a vertex and polygon table
					// for this object.
					//
					MFnMesh fnMesh( dagPath );
					int vtxCount = fnMesh.numVertices();
					int polygonCount = fnMesh.numPolygons();
					// we do not need this call anymore, we have the shape.
					// dagPath.extendToShape();
					MString name = dagPath.fullPathName();
					objectNames->append( name );
					objectNodeNamesArray.append( fnMesh.name() );

					vertexCounts.append( vtxCount );
					polygonCounts.append( polygonCount );

					objectCount++;
				}
			}
		}	
	}
	else 
	{
		MSelectionList slist;
		MGlobal::getActiveSelectionList( slist );
		MItSelectionList iter( slist );
		MStatus status;

		objectNames = new MStringArray;

		// We will need to interate over a selected node's heirarchy
		// in the case where shapes are grouped, and the group is selected.
		MItDag dagIterator( MItDag::kDepthFirst, MFn::kInvalid, &status);

		for ( ; !iter.isDone(); iter.next() ) 
		{
			MDagPath objectPath;
			stat = iter.getDagPath( objectPath );

			// reset iterator's root node to be the selected node.
			status = dagIterator.reset (objectPath.node(), 
				MItDag::kDepthFirst, MFn::kInvalid );

			// DAG iteration beginning at at selected node
			for ( ; !dagIterator.isDone(); dagIterator.next() )
			{
				MDagPath dagPath;
				MObject  component = MObject::kNullObj;
				status = dagIterator.getPath(dagPath);

				if (!status) {
					fprintf(stderr,"Failure getting DAG path.\n");
					freeLookupTables();
					return ;
				}

				// skip over intermediate objects
				//
				MFnDagNode dagNode( dagPath, &stat );
				if (dagNode.isIntermediateObject()) 
				{
					continue;
				}


				if (( dagPath.hasFn(MFn::kMesh)) &&
					( dagPath.hasFn(MFn::kTransform)))
				{
					// We want only the shape, 
					// not the transform-extended-to-shape.
					continue;
				}
				else if ( dagPath.hasFn(MFn::kMesh))
				{
					// We have a mesh so create a vertex and polygon table
					// for this object.
					//
					MFnMesh fnMesh( dagPath );
					int vtxCount = fnMesh.numVertices();
					int polygonCount = fnMesh.numPolygons();

					// we do not need this call anymore, we have the shape.
					// dagPath.extendToShape();
					MString name = dagPath.fullPathName();
					objectNames->append( name );
					objectNodeNamesArray.append( fnMesh.name() );

					vertexCounts.append( vtxCount );
					polygonCounts.append( polygonCount );

					objectCount++;	
				}
			}
		}
	}

	// Now we know how many objects we are dealing with 
	// and we have counts of the vertices/polygons for each
	// object so create the maya group look-up table.
	//
	if( objectCount > 0 ) {

		// To export Maya groups we traverse the hierarchy starting at
		// each objectNodeNamesArray[i] going towards the root collecting transform
		// nodes as we go.
		length = objectNodeNamesArray.length();
		for( i=0; i<length; i++ ) {
			MIntArray transformNodeNameIndicesArray;
			recFindTransformDAGNodes( objectNodeNamesArray[i], transformNodeNameIndicesArray );
		}

		if( transformNodeNameArray.length() > 0 ) {
			objectGroupsTablePtr = (bool**) malloc( sizeof(bool*)*objectCount );
			length = transformNodeNameArray.length();
			for ( i=0; i<objectCount; i++ )
			{
				objectGroupsTablePtr[i] =
					(bool*)calloc( length, sizeof(bool) );	

				if ( objectGroupsTablePtr[i] == NULL ) {
					cerr << "Error: calloc returned NULL (objectGroupsTablePtr)\n";
					return;
				}
			}
		}
	}

	// Create the vertex/polygon look-up tables.
	//
	if ( objectCount > 0 ) {

		vertexTablePtr = (bool**) malloc( sizeof(bool*)*objectCount );
		polygonTablePtr = (bool**) malloc( sizeof(bool*)*objectCount );

		for ( i=0; i<objectCount; i++ )
		{
			vertexTablePtr[i] =
				(bool*)calloc( vertexCounts[i]*numSets, sizeof(bool) );	

			if ( vertexTablePtr[i] == NULL ) {
				cerr << "Error: calloc returned NULL (vertexTable)\n";
				return;
			}

			polygonTablePtr[i] =
				(bool*)calloc( polygonCounts[i]*numSets, sizeof(bool) );
			if ( polygonTablePtr[i] == NULL ) {
				cerr << "Error: calloc returned NULL (polygonTable)\n";
				return;
			}
		}	
	}

	// If we found no meshes then return
	//	
	if ( objectCount == 0 ) {
		return;
	}

	//////////////////////////////////////////////////////////////////
	//
	// Go through all of the set members (flattened lists) and mark
	// in the lookup-tables, the sets that each mesh component belongs
	// to.
	//
	//
	//////////////////////////////////////////////////////////////////
	bool flattenedList = true;
	MDagPath object;
	MObject component;
	MSelectionList memberList;


	for ( i=0; i<numSets; i++ )
	{
		MFnSet fnSet( (*sets)[i] );		
		memberList.clear();
		stat = fnSet.getMembers( memberList, flattenedList );

		if (MS::kSuccess != stat) {
			fprintf(stderr,"Error in fnSet.getMembers()!\n");
		}

		int m, numMembers;
		numMembers = memberList.length();
		for ( m=0; m<numMembers; m++ )
		{
			if ( memberList.getDagPath(m,object,component) ) {

				if ( (!component.isNull()) && (object.apiType() == MFn::kMesh) )
				{
					if (component.apiType() == MFn::kMeshVertComponent) {
						MItMeshVertex viter( object, component );	
						for ( ; !viter.isDone(); viter.next() )
						{
							int compIdx = viter.index();
							MString name = object.fullPathName();

							// Figure out which object vertexTable
							// to get.
							//

							int o, numObjectNames;
							numObjectNames = objectNames->length();
							for ( o=0; o<numObjectNames; o++ ) {
								if ( (*objectNames)[o] == name ) {
									// Mark set i as true in the table
									//		
									vertexTable = vertexTablePtr[o];
									*(vertexTable + numSets*compIdx + i) = true;
									break;
								}
							}
						}
					}
					else if (component.apiType() == MFn::kMeshPolygonComponent) 
					{
						MItMeshPolygon piter( object, component );
						for ( ; !piter.isDone(); piter.next() )
						{
							int compIdx = piter.index();
							MString name = object.fullPathName();

							// Figure out which object polygonTable
							// to get.
							//							
							int o, numObjectNames;
							numObjectNames = objectNames->length();
							for ( o=0; o<numObjectNames; o++ ) {
								if ( (*objectNames)[o] == name ) {

									// Mark set i as true in the table
									//

									// Check for bad components in the set
									//									
									if ( compIdx >= polygonCounts[o] ) {
										cerr << "Error: component in set >= numPolygons, skipping!\n";
										cerr << "  Component index    = " << compIdx << endl;
										cerr << "  Number of polygons = " << polygonCounts[o] << endl;
										break;
									}

									polygonTable = polygonTablePtr[o];
									*(polygonTable + numSets*compIdx + i) = true;
									break;
								}
							}	
						}
					}										
				}
				else { 

					// There are no components, therefore we can mark
					// all polygons as members of the given set.
					//

					if (object.hasFn(MFn::kMesh)) {

						MFnMesh fnMesh( object, &stat );
						if ( MS::kSuccess != stat) {
							fprintf(stderr,"Failure in MFnMesh initialization.\n");
							return;
						}

						// We are going to iterate over all the polygons.
						//
						MItMeshPolygon piter( object, MObject::kNullObj, &stat );
						if ( MS::kSuccess != stat) {
							fprintf(stderr,
								"Failure in MItMeshPolygon initialization.\n");
							return;
						}
						for ( ; !piter.isDone(); piter.next() )
						{
							int compIdx = piter.index();
							MString name = object.fullPathName();

							// Figure out which object polygonTable to get.
							//
							int o, numObjectNames;
							numObjectNames = objectNames->length();
							for ( o=0; o<numObjectNames; o++ ) {
								if ( (*objectNames)[o] == name ) {

									// Check for bad components in the set
									//
									if ( compIdx >= polygonCounts[o] ) {
										cerr << "Error: component in set >= numPolygons, skipping!\n";
										cerr << "  Component index    = " << compIdx << endl;
										cerr << "  Number of polygons = " << polygonCounts[o] << endl;
										break;
									}
									// Mark set i as true in the table
									//
									polygonTable = polygonTablePtr[o];
									*(polygonTable + numSets*compIdx + i) = true;
									break;
								}
							}
						} // end of piter.next() loop
					} // end of condition if (object.hasFn(MFn::kMesh))
				} // end of else condifion if (!component.isNull()) 
			} // end of memberList.getDagPath(m,object,component)
		} // end of memberList loop
	} // end of for-loop for sets

	// Go through all of the group members and mark in the
	// lookup-table, the group that each shape belongs to.
	length = objectNodeNamesArray.length();
	for( i=0; i<length; i++ ) {
		MIntArray groupTableIndicesArray;
		bool *objectGroupTable = objectGroupsTablePtr[i];
		int length2;
		recFindTransformDAGNodes( objectNodeNamesArray[i], groupTableIndicesArray );
		length2 = groupTableIndicesArray.length();
		for( j=0; j<length2; j++ ) {
			int groupIdx = groupTableIndicesArray[j];
			objectGroupTable[groupIdx] = true;
		}
	}
}

//from obj exporter, without modification
void CityAdvExporter::recFindTransformDAGNodes( MString& nodeName, MIntArray& transformNodeIndicesArray )
{
	// To handle Maya groups we traverse the hierarchy starting at
	// each objectNames[i] going towards the root collecting transform
	// nodes as we go.
	MStringArray result;
	MString cmdStr = "listRelatives -ap " + nodeName;
	MGlobal::executeCommand( cmdStr, result );

	if( result.length() == 0 )
		// nodeName must be at the root of the DAG.  Stop recursing
		return;

	for( unsigned int j=0; j<result.length(); j++ ) {
		// check if the node result[i] is of type transform
		MStringArray result2;
		MGlobal::executeCommand( "nodeType " + result[j], result2 );

		if( result2.length() == 1 && result2[0] == "transform" ) {
			// check if result[j] is already in result[j]
			bool found=false;
			unsigned int i;
			for( i=0; i<transformNodeNameArray.length(); i++) {
				if( transformNodeNameArray[i] == result[j] ) {
					found = true;
					break;
				}
			}

			if( !found ) {
				transformNodeIndicesArray.append(transformNodeNameArray.length());
				transformNodeNameArray.append(result[j]);
			}
			else {
				transformNodeIndicesArray.append(i);
			}
			recFindTransformDAGNodes(result[j], transformNodeIndicesArray);
		}
	}
}

//from obj exporter, without modification
void CityAdvExporter::freeLookupTables()
//
// Frees up all tables and arrays allocated by this plug-in.
//
{
	for ( int i=0; i<objectCount; i++ ) {
		if ( vertexTablePtr[i] != NULL ) {
			free( vertexTablePtr[i] );
		}
		if ( polygonTablePtr[i] != NULL ) {
			free( polygonTablePtr[i] );
		}
	}	

	if( objectGroupsTablePtr != NULL ) {
		for ( int i=0; i<objectCount; i++ ) {
			if ( objectGroupsTablePtr[i] != NULL ) {
				free( objectGroupsTablePtr[i] );
			}
		}
		free( objectGroupsTablePtr );
		objectGroupsTablePtr = NULL;
	}

	if ( vertexTablePtr != NULL ) {
		free( vertexTablePtr );
		vertexTablePtr = NULL;
	}
	if ( polygonTablePtr != NULL ) {
		free( polygonTablePtr );
		polygonTablePtr = NULL;
	}

	if ( lastSets != NULL ) {
		delete lastSets;
		lastSets = NULL;
	}

	if ( lastMaterials != NULL ) {
		delete lastMaterials;
		lastMaterials = NULL;
	}

	if ( sets != NULL ) {
		delete sets;
		sets = NULL;
	}

	if ( objectNames != NULL ) {
		delete objectNames;
		objectNames = NULL;
	}		
}

//from obj exporter, without modification
void CityAdvExporter::buildEdgeTable( MDagPath& mesh )
{
	if ( !smoothing )
		return;

	// Create our edge lookup table and initialize all entries to NULL
	//
	MFnMesh fnMesh( mesh );
	edgeTableSize = fnMesh.numVertices();
	edgeTable = (EdgeInfoPtr*) calloc( edgeTableSize, sizeof(int) );

	// Add entries, for each edge, to the lookup table
	//
	MItMeshEdge eIt( mesh );
	for ( ; !eIt.isDone(); eIt.next() )
	{
		bool smooth = eIt.isSmooth();
		addEdgeInfo( eIt.index(0), eIt.index(1), smooth );
	}

	// Fill in referenced polygons
	//
	MItMeshPolygon pIt( mesh );
	for ( ; !pIt.isDone(); pIt.next() )
	{
		int pvc = pIt.polygonVertexCount();
		for ( int v=0; v<pvc; v++ )
		{
			int a = pIt.vertexIndex( v );
			int b = pIt.vertexIndex( v==(pvc-1) ? 0 : v+1 );

			EdgeInfoPtr elem = findEdgeInfo( a, b );
			if ( NULL != elem ) {
				int edgeId = pIt.index();

				if ( INVALID_ID == elem->polyIds[0] ) {
					elem->polyIds[0] = edgeId;
				}
				else {
					elem->polyIds[1] = edgeId;
				}                

			}
		}
	}

	// Now create a polyId->smoothingGroup table
	//   
	int numPolygons = fnMesh.numPolygons();
	polySmoothingGroups = (int*)malloc( sizeof(int) *  numPolygons );
	for ( int i=0; i< numPolygons; i++ ) {
		polySmoothingGroups[i] = NO_SMOOTHING_GROUP;
	}    

	// Now call the smoothingAlgorithm to fill in the polySmoothingGroups
	// table.
	// Note: we have to traverse ALL polygons to handle the case
	// of disjoint polygons.
	//
	nextSmoothingGroup = 1;
	currSmoothingGroup = 1;
	for ( int pid=0; pid<numPolygons; pid++ ) {
		newSmoothingGroup = true;
		// Check polygon has not already been visited
		if ( NO_SMOOTHING_GROUP == polySmoothingGroups[pid] ) {
			if ( !smoothingAlgorithm(pid,fnMesh) ) {
				// No smooth edges for this polygon so we set
				// the smoothing group to NO_SMOOTHING_GROUP (off)
				polySmoothingGroups[pid] = NO_SMOOTHING_GROUP;
			}
		}
	}
}

//from obj exporter, without modification
void CityAdvExporter::addEdgeInfo( int v1, int v2, bool smooth )
//
// Adds a new edge info element to the vertex table.
//
{
	EdgeInfoPtr element = NULL;

	if ( NULL == edgeTable[v1] ) {
		edgeTable[v1] = (EdgeInfoPtr)malloc( sizeof(struct EdgeInfo) );
		element = edgeTable[v1];
	}
	else {
		element = edgeTable[v1];
		while ( NULL != element->next ) {
			element = element->next;
		}
		element->next = (EdgeInfoPtr)malloc( sizeof(struct EdgeInfo) );
		element = element->next;
	}

	// Setup data for new edge
	//
	element->vertId     = v2;
	element->smooth     = smooth;
	element->next       = NULL;

	// Initialize array of id's of polygons that reference this edge.
	// There are at most 2 polygons per edge.
	//
	element->polyIds[0] = INVALID_ID;
	element->polyIds[1] = INVALID_ID;
}

//from obj exporter, without modification
EdgeInfoPtr CityAdvExporter::findEdgeInfo( int v1, int v2 )
//
// Finds the info for the specified edge.
//
{
	EdgeInfoPtr element = NULL;
	element = edgeTable[v1];

	while ( NULL != element ) {
		if ( v2 == element->vertId ) {
			return element;
		}
		element = element->next;
	}

	if ( element == NULL ) {
		element = edgeTable[v2];

		while ( NULL != element ) {
			if ( v1 == element->vertId ) {
				return element;
			}
			element = element->next;
		}
	}

	return NULL;
}

//from obj exporter, without modification
void CityAdvExporter::destroyEdgeTable()
//
// Free up all of the memory used by the edgeTable.
//
{
	if ( !smoothing )
		return;

	EdgeInfoPtr element = NULL;
	EdgeInfoPtr tmp = NULL;

	for ( int v=0; v<edgeTableSize; v++ )
	{
		element = edgeTable[v];
		while ( NULL != element )
		{
			tmp = element;
			element = element->next;
			free( tmp );
		}
	}

	if ( NULL != edgeTable ) {
		free( edgeTable );
		edgeTable = NULL;
	}

	if ( NULL != polySmoothingGroups ) {
		free( polySmoothingGroups );
		polySmoothingGroups = NULL;
	}
}

//from obj exporter, without modification
bool CityAdvExporter::smoothingAlgorithm( int polyId, MFnMesh& fnMesh )
{
	MIntArray vertexList;
	fnMesh.getPolygonVertices( polyId, vertexList );
	int vcount = vertexList.length();
	bool smoothEdgeFound = false;

	for ( int vid=0; vid<vcount;vid++ ) {
		int a = vertexList[vid];
		int b = vertexList[ vid==(vcount-1) ? 0 : vid+1 ];

		EdgeInfoPtr elem = findEdgeInfo( a, b );
		if ( NULL != elem ) {
			// NOTE: We assume there are at most 2 polygons per edge
			//       halfEdge polygons get a smoothing group of
			//       NO_SMOOTHING_GROUP which is equivalent to "s off"
			//
			if ( NO_SMOOTHING_GROUP != elem->polyIds[1] ) { // Edge not a border

				// We are starting a new smoothing group
				//                
				if ( newSmoothingGroup ) {
					currSmoothingGroup = nextSmoothingGroup++;
					newSmoothingGroup = false;

					// This is a SEED (starting) polygon and so we always
					// give it the new smoothing group id.
					// Even if all edges are hard this must be done so
					// that we know we have visited the polygon.
					//
					polySmoothingGroups[polyId] = currSmoothingGroup;
				}

				// If we have a smooth edge then this poly must be a member
				// of the current smoothing group.
				//
				if ( elem->smooth ) {
					polySmoothingGroups[polyId] = currSmoothingGroup;
					smoothEdgeFound = true;
				}
				else { // Hard edge so ignore this polygon
					continue;
				}

				// Find the adjacent poly id
				//
				int adjPoly = elem->polyIds[0];
				if ( adjPoly == polyId ) {
					adjPoly = elem->polyIds[1];
				}                             

				// If we are this far then adjacent poly belongs in this
				// smoothing group.
				// If the adjacent polygon's smoothing group is not
				// NO_SMOOTHING_GROUP then it has already been visited
				// so we ignore it.
				//
				if ( NO_SMOOTHING_GROUP == polySmoothingGroups[adjPoly] ) {
					smoothingAlgorithm( adjPoly, fnMesh );
				}
				else if ( polySmoothingGroups[adjPoly] != currSmoothingGroup ) {
					cerr << "Warning: smoothing group problem at polyon ";
					cerr << adjPoly << endl;
				}
			}
		}
	}
	return smoothEdgeFound;
}

//from obj exporter, without modification
bool CityAdvExporter::lookup( MDagPath& dagPath, 
						   int setIndex,
						   int compIdx,
						   bool isVtxIter )
{

	if (isVtxIter) {
		vertexTable = vertexTablePtr[objectId];
		bool ret = *(vertexTable + numSets*compIdx + setIndex);
		return ret;
	}
	else  {				
		polygonTable = polygonTablePtr[objectId];
		bool ret = *(polygonTable + numSets*compIdx + setIndex);			
		return ret;
	}
}	