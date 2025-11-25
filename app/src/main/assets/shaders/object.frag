precision mediump float;

// XYZ = direction, W = intensity
uniform vec4 u_LightingParameters;
uniform vec4 u_MaterialParameters; //x-ambient, y-diffuse, z-specular, w-specular power
uniform vec4 u_ColorCorrectionParameters;
uniform vec4 u_ObjColor;

void main() {
    vec3 normal = vec3(0.0, 0.0, 1.0); // Simplified normal
    vec3 lightDirection = u_LightingParameters.xyz;
    float lightIntensity = u_LightingParameters.w;

    float ambient = u_MaterialParameters.x;
    float diffuse = u_MaterialParameters.y;

    // Use u_ObjColor instead of texture
    vec3 diffuseColor = u_ObjColor.rgb;
    vec3 finalColor = diffuseColor * u_ColorCorrectionParameters.rgb;

    // Apply lighting
    float diffuseIntensity = max(0.0, dot(normal, lightDirection)) * diffuse;
    finalColor = finalColor * (ambient + diffuseIntensity * lightIntensity);

    gl_FragColor = vec4(finalColor, u_ObjColor.a);
}
