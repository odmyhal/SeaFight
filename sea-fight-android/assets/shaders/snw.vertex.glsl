
#define numDirectionalLights 2
#define bubblesTotalCount %d

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
uniform vec4 u_bubbles[bubblesTotalCount];
uniform int u_count_bubbles;

float maxX_p = %.4f;
float maxY_p = %.4f;
float amplitude_p = %.4f;
float waveNum = %.2f;
float PI = 3.14159265358979323846264;
float PI2 = PI * 2.0 * waveNum;
	
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

mat3 rotationMatrix3(vec3 axis, float angle)
{
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;
    
    return mat3(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c           );
}

float rand(vec2 co){
  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec3 makeHNormal(vec3 normal, float len){
	vec3 h_normal = vec3(normal.x, normal.y, 0.0);
	mat3 rmat = rotationMatrix3(normal, rand(vec2(normal.x * len, normal.z * len * 1.07)) * PI);
	h_normal.xyz = cross(normal, h_normal);
	h_normal.xyz = rmat * h_normal;
	return h_normal;
}

void main(){
	
	float xRad = PI2 * a_position.x / maxX_p;
	float yRad = PI2 * a_position.y / maxY_p;
	
	float rd = amplitude_p * sin(xRad + yRad + u_start);
	vec4 pos = u_worldTrans * vec4(a_position.x, a_position.y, rd, 1.0);
	gl_Position = u_projViewTrans * pos;
	
	vec3 normal = normalize(u_normalMatrix * calcNormal());

	float diff = 0.0;
	float len = 0.0;
	for(int j=0; j<u_count_bubbles; j++){
		len = length(pos.xy - u_bubbles[j].xy);
		if( len < u_bubbles[j].w ){
			diff = diff + u_bubbles[j].z * ( 1.0 - len / u_bubbles[j].w);
		}
	}
	if(diff > 0.0){
		vec3 h_normal = makeHNormal(normal, len);
		normal = rotationMatrix3(h_normal, rand(vec2(h_normal.z * len, h_normal.y * len * 1.05)) * diff) * normal;	
	}
	
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