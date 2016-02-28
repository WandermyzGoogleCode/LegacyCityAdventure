using UnityEngine;
using System.Collections;

public class AutoPositionController : MonoBehaviour {

    private UnityLocationManager locationManager;
    private GPSBeaconManager beaconManager;

    private LocationServiceStatus prevStatus;
    private LocationInfo prevLocation;

    // Use this for initialization
    void Start () {
        Debug.Log("C#: AutoPositionController Start");
        beaconManager = new GPSBeaconManager();
        
        if (Application.platform == RuntimePlatform.Android)
        {
            iPhoneSettings.StartLocationServiceUpdates();
            //locationManager = new UnityLocationManager();
            //locationManager.onResume();
        }
    }

    void OnApplicationPause(bool pause)
    {
        Debug.Log("C#: AutoPositionController OnPuase: " + pause.ToString());

        
        if (Application.platform == RuntimePlatform.Android)
        {
            if (beaconManager == null)
            {
                return;
            }

            if (pause)
            {
                //locationManager.onPause();
                iPhoneSettings.StopLocationServiceUpdates();
            }
            else
            {
                //locationManager.onResume();
                iPhoneSettings.StartLocationServiceUpdates();
            }
        }
    }
    
    // Update is called once per frame
    void Update () {
        if (Application.platform == RuntimePlatform.Android)
        {
            /*
            double latitude = locationManager.GetLatitude();
            double longitude = locationManager.GetLongitude();

            if (latitude != 0 && longitude != 0)
            {
                transform.position = beaconManager.GetPosition(latitude, longitude);
            }*/

            LocationServiceStatus status = iPhoneSettings.locationServiceStatus;

            if (status != LocationServiceStatus.Running)
            {
                if (prevStatus != status)
                {
                    Debug.Log("Location Status: " + status.ToString());
                    prevStatus = status;
                }
            }
            else
            {
                LocationInfo loc = iPhoneInput.lastLocation;

                if (prevLocation.latitude != loc.longitude || prevLocation.longitude != loc.longitude)
                {
                    Debug.Log("Location: " + loc.latitude.ToString() + ", " + loc.longitude.ToString());
                    prevLocation = loc;
                }

                Vector3 position = beaconManager.GetPosition(loc.latitude, loc.longitude);
                transform.position = new Vector3(position.x, transform.position.y, position.z);
            }
        }
    }
}
