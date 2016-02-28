using UnityEngine;
using System.Collections;

public class AutoCameraController : MonoBehaviour
{
    private UnitySensorManager sensorManager;
    private const float updateInterval = 1.0f / 60.0f;
    private const float lowPassKernelWidth = 1.0f;
    private const float lowPassFactor = updateInterval / lowPassKernelWidth;

    //private Vector3 prevAngles;
    //private float updateTimeElapsed;

    // Use this for initialization
    void Start()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            sensorManager = new UnitySensorManager();
            sensorManager.onResume();

            //prevAngles = new Vector3(sensorManager.getXAngle(), sensorManager.getYAngle(), 0);
            //updateTimeElapsed = 0;
        }
    }

    void OnApplicationPause(bool pause)
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            if (sensorManager == null)
            {
                return;
            }

            if (pause)
            {
                sensorManager.onPause();
            }
            else
            {
                sensorManager.onResume();
            }
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            //updateTimeElapsed += Time.deltaTime;

            //if (updateTimeElapsed >= updateInterval)
            //{
            //    Vector3 filteredAngles = Vector3.Lerp(prevAngles, new Vector3(sensorManager.getXAngle(), sensorManager.getYAngle(), 0), lowPassFactor);
            //    transform.localEulerAngles = -filteredAngles;

            //    prevAngles = filteredAngles;
            //    Debug.Log("angles: " + filteredAngles.ToString());




            //    updateTimeElapsed = 0;            
            //}

            transform.localEulerAngles = new Vector3(-sensorManager.getXAngle(), -sensorManager.getYAngle(), 0);
        }
    }
}
