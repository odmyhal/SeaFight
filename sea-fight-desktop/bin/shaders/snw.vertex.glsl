
#define numDirectionalLights 2

attribute vec3 a_position;
uniform mat4 u_projViewTrans;

uniform mat3 u_normalMatrix;
varying vec3 v_normal;

uniform mat4 u_worldTrans;

varying vec3 v_lightDiffuse;
uniform vec3 u_ambientCubemap[6];

struct DirectionalLight
{
	vec3 color;
	vec3 direction;
};
uniform DirectionalLight u_dirLights[numDirectionalLights];


uniform float u_start;

float maxX_p = %.4f;
float maxY_p = %.4f;
float amplitude_p = %.4f;
float waveNum = %.2f;
float PI2 = 3.14159265358979323846264 * 2.0 * waveNum;
	
vec3 calcNormal(){
	float xRad = PI2 * a_position.x / maxX_p;
	float yRad = PI2 * a_position.y / maxY_p;
	
	vec3 norm;
	float zCos = amplitude_p * cos(xRad + yRad + u_start) * PI2 / maxX_p;
	if(zCos == 0.0){
		norm = vec3(0.0, 0.0, 1.0);
	}else{
		float normalX = 1.0;
		zCos = -1.0 / zCos;
		if(zCos < 0.0){
			normalX = -1.0;
		}
		norm = normalize(vec3(normalX, normalX * maxX_p / maxY_p, zCos * normalX));
	}
	return norm;
}

void main(){
	
	float xRad = PI2 * a_position.x / maxX_p;
	float yRad = PI2 * a_position.y / maxY_p;
	
	float rd = amplitude_p * sin(xRad + yRad + u_start);
	vec4 pos = u_worldTrans * vec4(a_position.x, a_position.y, rd, 1.0);
	gl_Position = u_projViewTrans * pos;
	
	vec3 normal = normalize(u_normalMatrix * calcNormal());

	v_normal = normal;
	
	vec3 ambientLight = vec3(0.0);
	vec3 squaredNormal = normal * normal;
			vec3 isPositive  = step(0.0, normal);
			ambientLight += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +
					squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +
					squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);
	v_lightDiffuse = ambientLight;
	for (int i = 0; i < numDirectionalLights; i++) {
				vec3 lightDir = -u_dirLights[i].direction;
				float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
				vec3 value = u_dirLights[i].color * NdotL;
				v_lightDiffuse += value;
	}	
	
}