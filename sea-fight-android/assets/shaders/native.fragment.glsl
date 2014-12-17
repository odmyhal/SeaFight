#ifdef GL_ES 
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

varying vec3 v_normal;
uniform vec4 u_diffuseColor;

varying vec3 v_lightDiffuse;

void main(){
	vec3 normal = v_normal;
	
	
	vec4 diffuse = u_diffuseColor;
	gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse);
	gl_FragColor.a = 0.9;
}