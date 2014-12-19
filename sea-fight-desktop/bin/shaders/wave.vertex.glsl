attribute vec3 a_position;
uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

uniform float u_start;

varying vec3 v_normal;

void main(){
	float maxX_p = %.4f;
	float maxY_p = %.4f;
	float amplitude_p = %.4f;
	float PI2 = 3.14159265358979323846264 * 2.0;
	
	float xRad = PI2 * a_position.x / maxX_p;
	float yRad = PI2 * a_position.y / maxY_p;
	
	float rd = amplitude_p * sin(xRad + yRad + u_start);
	vec4 pos = u_worldTrans * vec4(a_position.x, a_position.y, rd, 1.0);
	gl_Position = u_projViewTrans * pos;
	
	float normalX = 1.0;
	float zCos = -1.0 / (amplitude_p * cos(xRad));
	if(zCos < 0.0){
		normalX = -1.0;
	}
	vec3 normal = vec3(normalX, normalX * maxX_p / maxY_p, zCos * normalX);
	v_normal = normalize(normal);
}