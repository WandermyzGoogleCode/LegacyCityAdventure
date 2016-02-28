using UnityEngine;
using System.Collections;

public class MouseCameraController : MonoBehaviour
{

    public float xSpeed = 250.0f;
    public float ySpeed = 120.0f;

    public float yMinLimit = -20f;
    public float yMaxLimit = 80f;

    private float x = 0.0f;
    private float y = 0.0f;

    void Start()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            return;
        }
    }

    void Update()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            return;
        }

        /*if (!Input.GetMouseButtonDown(0))
        {
            return;
        }*/

        x += Input.GetAxis("Mouse X") * xSpeed * 0.02f;
        y -= Input.GetAxis("Mouse Y") * ySpeed * 0.02f;

        y = ClampAngle(y, yMinLimit, yMaxLimit);

        var rotation = Quaternion.Euler(y, x, 0);

        transform.rotation = rotation;
    }

    static float ClampAngle(float angle, float min, float max)
    {
        if (angle < -360)
            angle += 360;
        if (angle > 360)
            angle -= 360;
        return Mathf.Clamp(angle, min, max);
    }
}
